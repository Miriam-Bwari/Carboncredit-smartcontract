from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from database.connection import get_db
from database.models import Farm, PracticeLog
from core.security import get_current_user, require_agent
from schemas.responses import FarmResponse, FarmRegisterResponse, FarmSummary, PracticeLogCreate, PracticeLogResponse
from schemas.geojson import GeoJsonPolygon
from pydantic import BaseModel

router = APIRouter()


class FarmRegister(BaseModel):
    farmer_id: str
    name: str
    boundary_coords: GeoJsonPolygon
    soil_type: str
    crop_type: str
    county: str


def calculate_area_hectares(polygon: GeoJsonPolygon) -> float:
    coords = polygon.coordinates[0] if polygon.coordinates else []
    if not coords or len(coords) < 3:
        return 0.0
    area = 0.0
    for i in range(len(coords)):
        j = (i + 1) % len(coords)
        # coords[i] is [longitude, latitude]
        # X is longitude (coords[0]), Y is latitude (coords[1])
        area += coords[i][0] * coords[j][1]
        area -= coords[j][0] * coords[i][1]
    area_m2 = abs(area) / 2.0 * (111320 ** 2)
    return round(area_m2 / 10000, 2)


@router.post("/register", response_model=FarmRegisterResponse)
def register_farm(data: FarmRegister, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    coords = data.boundary_coords.coordinates[0] if data.boundary_coords.coordinates else []
    if len(coords) < 3:
        raise HTTPException(status_code=400, detail="Invalid farm boundary: need at least 3 coordinates")

    existing = db.query(Farm).filter(Farm.farmer_id == data.farmer_id, Farm.name == data.name).first()
    if existing:
        raise HTTPException(status_code=409, detail="Farm with this name already exists for this farmer")

    area = calculate_area_hectares(data.boundary_coords)

    farm = Farm(
        farmer_id=data.farmer_id,
        agent_id=current_user["user_id"] if current_user["role"] == "Agent" else None,
        name=data.name,
        boundary_coords=data.boundary_coords.model_dump(),
        area_hectares=area,
        soil_type=data.soil_type,
        crop_type=data.crop_type,
        county=data.county
    )
    db.add(farm)
    db.commit()
    db.refresh(farm)

    return FarmRegisterResponse(message="Farm registered successfully", farm_id=farm.id, area_hectares=area)


@router.get("/{farm_id}", response_model=FarmResponse)
def get_farm(farm_id: str, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    farm = db.query(Farm).filter(Farm.id == farm_id).first()
    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    return FarmResponse(
        id=farm.id,
        farmer_id=farm.farmer_id,
        name=farm.name or "",
        boundary_coords=farm.boundary_coords or {},
        area_hectares=farm.area_hectares or 0.0,
        soil_type=farm.soil_type or "",
        crop_type=farm.crop_type or "",
        county=farm.county or ""
    )


@router.get("/farmer/{farmer_id}", response_model=List[FarmSummary])
def get_farmer_farms(farmer_id: str, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    farms = db.query(Farm).filter(Farm.farmer_id == farmer_id).all()
    return [
        FarmSummary(id=f.id, name=f.name or "", area_hectares=f.area_hectares or 0.0, crop_type=f.crop_type or "")
        for f in farms
    ]


@router.post("/{farm_id}/practices", response_model=PracticeLogResponse)
def add_practice_log(farm_id: str, data: PracticeLogCreate, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    farm = db.query(Farm).filter(Farm.id == farm_id).first()
    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    log = PracticeLog(
        farm_id=farm_id,
        crop_type=data.crop_type,
        tillage_method=data.tillage_method,
        tree_count=data.tree_count,
        irrigation_source=data.irrigation_source
    )
    db.add(log)
    db.commit()
    db.refresh(log)

    return PracticeLogResponse(
        id=log.id,
        farm_id=log.farm_id,
        crop_type=log.crop_type or "",
        tillage_method=log.tillage_method or "",
        tree_count=log.tree_count or 0,
        irrigation_source=log.irrigation_source or "",
        created_at=log.created_at.isoformat() if log.created_at else ""
    )


@router.get("/{farm_id}/practices", response_model=List[PracticeLogResponse])
def get_practice_logs(farm_id: str, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    logs = db.query(PracticeLog).filter(PracticeLog.farm_id == farm_id).order_by(PracticeLog.created_at.desc()).all()
    return [
        PracticeLogResponse(
            id=log.id,
            farm_id=log.farm_id,
            crop_type=log.crop_type or "",
            tillage_method=log.tillage_method or "",
            tree_count=log.tree_count or 0,
            irrigation_source=log.irrigation_source or "",
            created_at=log.created_at.isoformat() if log.created_at else ""
        )
        for log in logs
    ]