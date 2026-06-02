# database/models.py

from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, JSON, ForeignKey, Index
from sqlalchemy.orm import relationship
from database.connection import Base
from datetime import datetime


class Farmer(Base):
    __tablename__ = 'farmers'

    id            = Column(Integer, primary_key=True, index=True)
    full_name     = Column(String(100), nullable=False)
    phone_number  = Column(String(20), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    county        = Column(String(50))
    created_at    = Column(DateTime, default=datetime.utcnow, index=True)

    farms = relationship(
        'Farm',
        back_populates='farmer',
        cascade='all, delete-orphan'
    )


class Farm(Base):
    __tablename__ = 'farms'

    id              = Column(Integer, primary_key=True, index=True)
    farmer_id       = Column(
        Integer,
        ForeignKey('farmers.id', ondelete="CASCADE"),
        nullable=False,
        index=True
    )
    name            = Column(String(100), index=True)
    boundary_coords = Column(JSON, nullable=False)
    area_hectares   = Column(Float)
    soil_type       = Column(String(50))
    crop_type       = Column(String(50), index=True)
    county          = Column(String(50), index=True)
    created_at      = Column(DateTime, default=datetime.utcnow, index=True)

    farmer = relationship('Farmer', back_populates='farms')

    carbon_records = relationship(
        'CarbonRecord',
        back_populates='farm',
        cascade='all, delete-orphan'
    )


class CarbonRecord(Base):
    __tablename__ = 'carbon_records'

    id              = Column(Integer, primary_key=True, index=True)
    farm_id         = Column(
        Integer,
        ForeignKey('farms.id', ondelete="CASCADE"),
        nullable=False,
        index=True
    )
    scan_date       = Column(DateTime, nullable=False, index=True)
    ndvi_value      = Column(Float, index=True)
    prev_ndvi       = Column(Float)
    carbon_kg       = Column(Float)
    carbon_credits  = Column(Float, index=True)
    is_verified     = Column(Boolean, default=False, index=True)
    fraud_flags     = Column(JSON, default=lambda: [])
    blockchain_tx   = Column(String(100), index=True)
    ndvi_data_hash  = Column(String(64), unique=True)

    farm = relationship('Farm', back_populates='carbon_records')


class Payment(Base):
    __tablename__ = 'payments'

    id              = Column(Integer, primary_key=True, index=True)
    farmer_id       = Column(
        Integer,
        ForeignKey('farmers.id', ondelete="CASCADE"),
        index=True
    )
    amount_kes      = Column(Float)
    carbon_credits  = Column(Float)
    mpesa_reference = Column(String(100), unique=True, index=True)
    status          = Column(String(20), default='pending', index=True)
    created_at      = Column(DateTime, default=datetime.utcnow, index=True)