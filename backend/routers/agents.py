from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from database.connection import get_db
from database.models import Agent
from core.security import create_access_token, get_current_user, require_admin
from schemas.responses import LoginResponse, AgentRegisterResponse
from pydantic import BaseModel

router = APIRouter()

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class AgentRegister(BaseModel):
    full_name: str
    phone_number: str
    password: str
    county: str


class AgentLogin(BaseModel):
    phone_number: str
    password: str


@router.post("/register", response_model=AgentRegisterResponse)
def register_agent(data: AgentRegister, db: Session = Depends(get_db)):
    existing = db.query(Agent).filter(Agent.phone_number == data.phone_number).first()
    if existing:
        raise HTTPException(status_code=400, detail="Phone number already registered")

    agent = Agent(
        full_name=data.full_name,
        phone_number=data.phone_number,
        password_hash=pwd_context.hash(data.password),
        county=data.county
    )
    db.add(agent)
    db.commit()
    db.refresh(agent)

    return AgentRegisterResponse(message="Agent registered successfully", agent_id=agent.id)


@router.post("/login", response_model=LoginResponse)
def login_agent(data: AgentLogin, db: Session = Depends(get_db)):
    agent = db.query(Agent).filter(Agent.phone_number == data.phone_number).first()

    if not agent or not agent.is_active:
        raise HTTPException(status_code=401, detail="Invalid credentials or account suspended")

    if not pwd_context.verify(data.password, str(agent.password_hash)):
        raise HTTPException(status_code=401, detail="Invalid phone number or password")

    token = create_access_token(user_id=agent.id, role="Agent")

    return LoginResponse(access_token=token, role="Agent", user_id=agent.id)


@router.get("/dashboard/{agent_id}")
def get_agent_dashboard(agent_id: str, db: Session = Depends(get_db)):
    from database.models import Farm, Farmer
    from datetime import datetime, timedelta

    # Count total farms registered by this agent (assuming 1 farm = 1 farmer for metrics)
    total_farms = db.query(Farm).filter(Farm.agent_id == agent_id).count()

    # Count new farms this month
    thirty_days_ago = datetime.utcnow() - timedelta(days=30)
    new_this_month = db.query(Farm)\
        .filter(Farm.agent_id == agent_id, Farm.created_at >= thirty_days_ago)\
        .count()

    # Get recent registrations (using Farm joined with Farmer)
    recent_farms = db.query(Farm)\
        .filter(Farm.agent_id == agent_id)\
        .order_by(Farm.created_at.desc())\
        .limit(5)\
        .all()

    recent_registrations = []
    for f in recent_farms:
        farmer = db.query(Farmer).filter(Farmer.id == f.farmer_id).first()
        recent_registrations.append({
            "id": f.farmer_id,
            "name": farmer.full_name if farmer else "Unknown",
            "county": f.county or "Unknown",
            "status": "ACTIVE",
            "syncText": "Synced"
        })

    return {
        "farmersRegistered": total_farms,
        "pendingSyncs": 0, # Backend doesn't know about pending local syncs
        "newThisMonth": new_this_month,
        "recentRegistrations": recent_registrations
    }
