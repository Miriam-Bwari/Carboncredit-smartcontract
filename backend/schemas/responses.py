# Pydantic response models for all API endpoints.
# These are the contract between the backend and the Android app.

from pydantic import BaseModel
from typing import List, Optional


# Auth
class LoginResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    role: str           # "Farmer" | "Agent" | "Admin"
    user_id: str        # UUID


class FarmerRegisterResponse(BaseModel):
    message: str
    farmer_id: str


class AgentRegisterResponse(BaseModel):
    message: str
    agent_id: str


# Farmer
class FarmerResponse(BaseModel):
    id: str
    full_name: str
    phone_number: str
    county: str


# Farm
class FarmResponse(BaseModel):
    id: str
    farmer_id: str
    name: str
    boundary_coords: List[List[float]]
    area_hectares: float
    soil_type: str
    crop_type: str
    county: str


class FarmRegisterResponse(BaseModel):
    message: str
    farm_id: str
    area_hectares: float


class FarmSummary(BaseModel):
    id: str
    name: str
    area_hectares: float
    crop_type: str


# Carbon
class CarbonScanRecord(BaseModel):
    date: str
    ndvi: Optional[float]
    carbon_kg: Optional[float]
    credits: Optional[float]
    verified: bool


class CarbonHistoryResponse(BaseModel):
    farm_id: str
    total_credits: float
    total_carbon_kg: float
    scans: int
    records: List[CarbonScanRecord]


# Payments
class StkPushResponse(BaseModel):
    success: bool
    message: str
    checkout_id: Optional[str] = None
    merchant_id: Optional[str] = None


class PaymentStatusResponse(BaseModel):
    checkout_id: str
    status: str         # "pending" | "confirmed" | "failed"
    amount_kes: Optional[float] = None
    mpesa_reference: Optional[str] = None
