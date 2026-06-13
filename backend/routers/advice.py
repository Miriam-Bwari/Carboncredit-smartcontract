# routers/advice.py
# Returns AI farming recommendations for a farm

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farm, CarbonRecord
from ai.farming_advisor import get_recommendations

router = APIRouter()


# ─────────────────────────────────────────────
# GET FARM ADVICE
# ─────────────────────────────────────────────
@router.get("/{farm_id}")
def get_advice(farm_id: str, db: Session = Depends(get_db)):

    farm = db.query(Farm).filter(Farm.id == farm_id).first()

    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    # Get latest NDVI safely
    latest = (
        db.query(CarbonRecord)
        .filter(CarbonRecord.farm_id == farm_id)
        .order_by(CarbonRecord.scan_date.desc())
        .first()
    )

    # ✅ FIX: ensure proper float conversion
    ndvi = float(latest.ndvi_value) if latest and latest.ndvi_value is not None else 0.3

    # Derived from two factors (DRY – reuse the scan we already queried):
    #   1. Scan recency:  full score if scanned today, decays over 14 days
    #   2. NDVI quality:  values in 0.15–0.85 are reliable; extremes suggest cloud/noise
    # Final score clamped to 0–100 and returned as int.
    if latest and latest.scan_date:
        from datetime import datetime
        days_old = (datetime.utcnow() - latest.scan_date).days
        recency_score = max(0.0, 1.0 - (days_old / 14.0))          # 1.0 → 0.0 over 14 days

        # NDVI quality: peak at 0.5, drops toward 0 or 1
        ndvi_quality = 1.0 - abs(ndvi - 0.5) * 1.5                 # 1.0 at ndvi=0.5, ~0 at extremes
        ndvi_quality = max(0.0, min(1.0, ndvi_quality))

        confidence = int((recency_score * 0.6 + ndvi_quality * 0.4) * 100)
    else:
        confidence = 0  # No scan data — be honest

    # ✅ FIX: avoid None values for crop_type & county
    crop_type = farm.crop_type or "maize"
    county = farm.county or "default"

    advice = get_recommendations(crop_type, ndvi, county)
    advice["confidence_score"] = confidence  # Attach to response (DRY — no extra endpoint)

    return advice