from fastapi import APIRouter, Depends, HTTPException, BackgroundTasks
from sqlalchemy.orm import Session

from database.connection import get_db, SessionLocal
from database.models import Farm, CarbonRecord
from ai.earth_engine import get_farm_ndvi
from ai.carbon_calculator import estimate_carbon_kg

from datetime import datetime

router = APIRouter()


# ── START SATELLITE SCAN ────────────────────────────────────────────────
@router.post("/scan/{farm_id}")
def scan_farm(farm_id: str, bg: BackgroundTasks, db: Session = Depends(get_db)):

    farm = db.query(Farm).filter(Farm.id == farm_id).first()
    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    bg.add_task(run_real_scan, farm_id)

    return {
        "message": "Satellite scan started",
        "farm_id": farm_id,
        "note": "Using Sentinel-2 via Google Earth Engine"
    }


# ── BACKGROUND TASK ─────────────────────────────────────────────────────
def run_real_scan(farm_id: int):
    db = SessionLocal()

    try:
        farm = db.query(Farm).filter(Farm.id == farm_id).first()
        if not farm:
            print(f"[SCAN ERROR] Farm {farm_id} not found")
            return

        today = datetime.utcnow().strftime("%Y-%m-%d")

        print(f"[SCAN] Starting Sentinel-2 scan for farm {farm_id}")

        result = get_farm_ndvi(farm.boundary_coords, today)

        if not result or result.get("ndvi") is None:
            print("[SCAN ERROR] No NDVI data received")
            return

        current_ndvi = float(result["ndvi"])
        data_hash = result.get("data_hash")
        is_real = not result.get("is_mock", True)

        print(f"[SCAN] NDVI={current_ndvi} | REAL={is_real}")

        prev_record = (
            db.query(CarbonRecord)
            .filter(CarbonRecord.farm_id == farm_id)
            .order_by(CarbonRecord.scan_date.desc())
            .first()
        )

        prev_ndvi = prev_record.ndvi_value if prev_record and prev_record.ndvi_value else 0.1

        calc = estimate_carbon_kg(current_ndvi, prev_ndvi, farm.area_hectares or 0)

        carbon_kg = calc.get("carbon_kg", 0)
        credits = calc.get("credits", 0)

        print(f"[SCAN] Carbon={carbon_kg}kg | Credits={credits}")

        record = CarbonRecord(
            farm_id=farm_id,
            scan_date=datetime.utcnow(),
            ndvi_value=current_ndvi,
            prev_ndvi=prev_ndvi,
            carbon_kg=carbon_kg,
            carbon_credits=credits,
            is_verified=credits > 0,
            ndvi_data_hash=data_hash
        )

        db.add(record)
        db.commit()

        print(f"[SCAN SUCCESS] Saved farm {farm_id}")

    except Exception as e:
        print(f"[SCAN FAILED] {e}")

    finally:
        db.close()


# ── HISTORY ENDPOINT ────────────────────────────────────────────────────
@router.get("/history/{farm_id}")
def carbon_history(farm_id: str, db: Session = Depends(get_db)):

    records = (
        db.query(CarbonRecord)
        .filter(CarbonRecord.farm_id == farm_id)
        .order_by(CarbonRecord.scan_date.desc())
        .all()
    )

    total_credits = sum(r.carbon_credits or 0 for r in records)
    total_carbon = sum(r.carbon_kg or 0 for r in records)

    return {
        "farm_id": farm_id,
        "total_credits": round(total_credits, 4),
        "total_carbon_kg": round(total_carbon, 2),
        "scans": len(records),
        "records": [
            {
                "date": str(r.scan_date),
                "ndvi": r.ndvi_value,
                "carbon_kg": r.carbon_kg,
                "credits": r.carbon_credits,
                "verified": r.is_verified
            }
            for r in records
        ]
    }


# ── NDVI TEST ENDPOINT ─────────────────────────────────────────────────
@router.get("/ndvi-test")
def ndvi_test():

    test_coords = [
        [-1.2850, 36.8200],
        [-1.2850, 36.8260],
        [-1.2910, 36.8260],
        [-1.2910, 36.8200],
        [-1.2850, 36.8200]
    ]

    today = datetime.utcnow().strftime("%Y-%m-%d")
    result = get_farm_ndvi(test_coords, today)

    return {
        "test": "Sentinel-2 NDVI Test",
        "result": result
    }