# All primary keys are UUID strings for offline-first sync compatibility.
# MIGRATION NOTE: Drop and recreate all tables when deploying this version.

from sqlalchemy import Column, String, Float, Boolean, DateTime, JSON, ForeignKey, Integer
from sqlalchemy.orm import relationship
from database.connection import Base
from datetime import datetime
import uuid


def new_uuid() -> str:
    return str(uuid.uuid4())


class Farmer(Base):
    __tablename__ = 'farmers'

    id            = Column(String(36), primary_key=True, default=new_uuid, index=True)
    full_name     = Column(String(100), nullable=False)
    phone_number  = Column(String(20), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    county        = Column(String(50))
    created_at    = Column(DateTime, default=datetime.utcnow, index=True)

    farms    = relationship('Farm', back_populates='farmer', cascade='all, delete-orphan')
    payments = relationship('Payment', back_populates='farmer', cascade='all, delete-orphan')


class Agent(Base):
    __tablename__ = 'agents'

    id            = Column(String(36), primary_key=True, default=new_uuid, index=True)
    full_name     = Column(String(100), nullable=False)
    phone_number  = Column(String(20), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    county        = Column(String(50))
    is_active     = Column(Boolean, default=True, index=True)
    created_at    = Column(DateTime, default=datetime.utcnow, index=True)


class Farm(Base):
    __tablename__ = 'farms'

    id              = Column(String(36), primary_key=True, default=new_uuid, index=True)
    farmer_id       = Column(String(36), ForeignKey('farmers.id', ondelete="CASCADE"), nullable=False, index=True)
    agent_id        = Column(String(36), ForeignKey('agents.id'), nullable=True, index=True)
    name            = Column(String(100), index=True)
    boundary_coords = Column(JSON, nullable=False)
    area_hectares   = Column(Float)
    soil_type       = Column(String(50))
    crop_type       = Column(String(50), index=True)
    county          = Column(String(50), index=True)
    created_at      = Column(DateTime, default=datetime.utcnow, index=True)

    farmer         = relationship('Farmer', back_populates='farms')
    carbon_records = relationship('CarbonRecord', back_populates='farm', cascade='all, delete-orphan')
    practice_logs  = relationship('PracticeLog', back_populates='farm', cascade='all, delete-orphan')


class PracticeLog(Base):
    __tablename__ = 'practice_logs'

    id                = Column(String(36), primary_key=True, default=new_uuid, index=True)
    farm_id           = Column(String(36), ForeignKey('farms.id', ondelete="CASCADE"), nullable=False, index=True)
    crop_type         = Column(String(50))
    tillage_method    = Column(String(50))
    tree_count        = Column(Integer)
    irrigation_source = Column(String(50))
    created_at        = Column(DateTime, default=datetime.utcnow, index=True)

    farm = relationship('Farm', back_populates='practice_logs')


class CarbonRecord(Base):
    __tablename__ = 'carbon_records'

    id             = Column(String(36), primary_key=True, default=new_uuid, index=True)
    farm_id        = Column(String(36), ForeignKey('farms.id', ondelete="CASCADE"), nullable=False, index=True)
    scan_date      = Column(DateTime, nullable=False, index=True)
    ndvi_value     = Column(Float, index=True)
    prev_ndvi      = Column(Float)
    carbon_kg      = Column(Float)
    carbon_credits = Column(Float, index=True)
    is_verified    = Column(Boolean, default=False, index=True)
    fraud_flags    = Column(JSON, default=lambda: [])
    blockchain_tx  = Column(String(100), index=True)
    ndvi_data_hash = Column(String(64), unique=True)

    farm = relationship('Farm', back_populates='carbon_records')


class Payment(Base):
    __tablename__ = 'payments'

    id              = Column(String(36), primary_key=True, default=new_uuid, index=True)
    farmer_id       = Column(String(36), ForeignKey('farmers.id', ondelete="CASCADE"), index=True)
    checkout_id     = Column(String(100), unique=True, index=True)  # Safaricom CheckoutRequestID
    amount_kes      = Column(Float)
    carbon_credits  = Column(Float)
    mpesa_reference = Column(String(100), unique=True, index=True)
    status          = Column(String(20), default='pending', index=True)
    created_at      = Column(DateTime, default=datetime.utcnow, index=True)

    farmer = relationship('Farmer', back_populates='payments')