# routers/farmers.py
# Handles: farmer registration, login, and profile retrieval

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farmer

from pydantic import BaseModel
from passlib.context import CryptContext
from jose import jwt
from datetime import datetime, timedelta
import os

router = APIRouter()

# Password hashing system
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# JWT config
SECRET_KEY = os.getenv("SECRET_KEY", "fallback_secret_key")
ALGORITHM = "HS256"


# ─────────────────────────────────────────────
# REQUEST SCHEMAS
# ─────────────────────────────────────────────
class FarmerRegister(BaseModel):
    full_name: str
    phone_number: str
    password: str
    county: str


class FarmerLogin(BaseModel):
    phone_number: str
    password: str


# ─────────────────────────────────────────────
# REGISTER FARMER
# ─────────────────────────────────────────────
@router.post("/register")
def register_farmer(data: FarmerRegister, db: Session = Depends(get_db)):

    existing = db.query(Farmer).filter(
        Farmer.phone_number == data.phone_number
    ).first()

    if existing:
        raise HTTPException(
            status_code=400,
            detail="Phone number already registered"
        )

    farmer = Farmer(
        full_name=data.full_name,
        phone_number=data.phone_number,
        password_hash=pwd_context.hash(data.password),
        county=data.county
    )

    db.add(farmer)
    db.commit()
    db.refresh(farmer)

    return {
        "message": "Farmer registered successfully",
        "farmer_id": farmer.id
    }


# ─────────────────────────────────────────────
# LOGIN FARMER
# ─────────────────────────────────────────────
@router.post("/login")
def login_farmer(data: FarmerLogin, db: Session = Depends(get_db)):

    farmer = db.query(Farmer).filter(
        Farmer.phone_number == data.phone_number
    ).first()

    # FIX: force string so Pylance stops complaining
    if not farmer or not pwd_context.verify(data.password, str(farmer.password_hash)):
        raise HTTPException(
            status_code=401,
            detail="Invalid phone number or password"
        )

    token = jwt.encode(
        {
            "sub": str(farmer.id),
            "exp": datetime.utcnow() + timedelta(hours=24)
        },
        SECRET_KEY,
        algorithm=ALGORITHM
    )

    return {
        "access_token": token,
        "farmer_id": farmer.id
    }


# ─────────────────────────────────────────────
# GET FARMER PROFILE
# ─────────────────────────────────────────────
@router.get("/{farmer_id}")
def get_farmer(farmer_id: int, db: Session = Depends(get_db)):

    farmer = db.query(Farmer).filter(
        Farmer.id == farmer_id
    ).first()

    if not farmer:
        raise HTTPException(
            status_code=404,
            detail="Farmer not found"
        )

    return {
        "id": farmer.id,
        "full_name": farmer.full_name,
        "phone_number": farmer.phone_number,
        "county": farmer.county
    }