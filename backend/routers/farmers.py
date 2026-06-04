from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from database.connection import get_db
from database.models import Farmer
from core.security import create_access_token, get_current_user
from schemas.responses import LoginResponse, FarmerRegisterResponse, FarmerResponse
from pydantic import BaseModel

router = APIRouter()

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class FarmerRegister(BaseModel):
    full_name: str
    phone_number: str
    password: str
    county: str


class FarmerLogin(BaseModel):
    phone_number: str
    password: str


@router.post("/register", response_model=FarmerRegisterResponse)
def register_farmer(data: FarmerRegister, db: Session = Depends(get_db)):
    existing = db.query(Farmer).filter(Farmer.phone_number == data.phone_number).first()
    if existing:
        raise HTTPException(status_code=400, detail="Phone number already registered")

    farmer = Farmer(
        full_name=data.full_name,
        phone_number=data.phone_number,
        password_hash=pwd_context.hash(data.password),
        county=data.county
    )
    db.add(farmer)
    db.commit()
    db.refresh(farmer)

    return FarmerRegisterResponse(message="Farmer registered successfully", farmer_id=farmer.id)


@router.post("/login", response_model=LoginResponse)
def login_farmer(data: FarmerLogin, db: Session = Depends(get_db)):
    farmer = db.query(Farmer).filter(Farmer.phone_number == data.phone_number).first()

    if not farmer or not pwd_context.verify(data.password, str(farmer.password_hash)):
        raise HTTPException(status_code=401, detail="Invalid phone number or password")

    token = create_access_token(user_id=farmer.id, role="Farmer")

    return LoginResponse(access_token=token, role="Farmer", user_id=farmer.id)


@router.get("/{farmer_id}", response_model=FarmerResponse)
def get_farmer(farmer_id: str, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    farmer = db.query(Farmer).filter(Farmer.id == farmer_id).first()
    if not farmer:
        raise HTTPException(status_code=404, detail="Farmer not found")

    return FarmerResponse(
        id=farmer.id,
        full_name=farmer.full_name,
        phone_number=farmer.phone_number,
        county=farmer.county or ""
    )