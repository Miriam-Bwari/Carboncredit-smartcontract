from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from database.connection import get_db
from database.models import Farm

from pydantic import BaseModel
from typing import List

router = APIRouter()


# ── REQUEST SCHEMA ───────────────────────────────────────────────────────
class FarmRegister(BaseModel):
    farmer_id: int
    name: str
    boundary_coords: List[List[float]]  # [[lat,lng],[lat,lng]...]
    soil_type: str
    crop_type: str
    county: str


# ── AREA CALCULATION (IMPROVED SAFETY) ───────────────────────────────────
def calculate_area_hectares(coords: List[List[float]]) -> float:
    if not coords or len(coords) < 3:
        return 0.0

    area = 0.0

    for i in range(len(coords)):
        j = (i + 1) % len(coords)
        area += coords[i][1] * coords[j][0]
        area -= coords[j][1] * coords[i][0]

    # crude geo conversion (good for MVP, replace later with Geo libraries)
    area_m2 = abs(area) / 2.0 * (111320 ** 2)

    return round(area_m2 / 10000, 2)


# ── REGISTER FARM ────────────────────────────────────────────────────────
@router.post("/register")
def register_farm(data: FarmRegister, db: Session = Depends(get_db)):

    # validate polygon
    if len(data.boundary_coords) < 3:
        raise HTTPException(status_code=400, detail="Invalid farm boundary")

    # prevent duplicate farm name per farmer (basic safety rule)
    existing = (
        db.query(Farm)
        .filter(Farm.farmer_id == data.farmer_id, Farm.name == data.name)
        .first()
    )

    if existing:
        raise HTTPException(status_code=409, detail="Farm already exists")

    area = calculate_area_hectares(data.boundary_coords)

    farm = Farm(
        farmer_id=data.farmer_id,
        name=data.name,
        boundary_coords=data.boundary_coords,
        area_hectares=area,
        soil_type=data.soil_type,
        crop_type=data.crop_type,
        county=data.county
    )

    db.add(farm)
    db.commit()
    db.refresh(farm)

    return {
        "message": "Farm registered successfully",
        "farm_id": farm.id,
        "area_hectares": area
    }


# ── GET SINGLE FARM ──────────────────────────────────────────────────────
@router.get("/{farm_id}")
def get_farm(farm_id: int, db: Session = Depends(get_db)):

    farm = db.query(Farm).filter(Farm.id == farm_id).first()

    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    return {
        "id": farm.id,
        "name": farm.name,
        "area_hectares": farm.area_hectares,
        "crop_type": farm.crop_type,
        "soil_type": farm.soil_type,
        "county": farm.county
    }


# ── GET ALL FARMS FOR FARMER ─────────────────────────────────────────────
@router.get("/farmer/{farmer_id}")
def get_farmer_farms(farmer_id: int, db: Session = Depends(get_db)):

    farms = db.query(Farm).filter(Farm.farmer_id == farmer_id).all()

    return [
        {
            "id": f.id,
            "name": f.name,
            "area_hectares": f.area_hectares,
            "crop_type": f.crop_type
        }
        for f in farms
    ]