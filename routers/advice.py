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
def get_advice(farm_id: int, db: Session = Depends(get_db)):

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

    # ✅ FIX: avoid None values for crop_type & county
    crop_type = farm.crop_type or "maize"
    county = farm.county or "default"

    advice = get_recommendations(crop_type, ndvi, county)

    return advice