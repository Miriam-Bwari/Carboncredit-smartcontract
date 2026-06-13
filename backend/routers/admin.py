from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import func
from typing import List, Dict, Any

from database.connection import get_db
from database.models import Agent, Farmer, Farm, Payment
from core.security import require_admin

router = APIRouter()


@router.get("/dashboard")
def get_dashboard_stats(
    db: Session = Depends(get_db),
) -> Dict[str, Any]:
    total_farmers = db.query(Farmer).count()
    active_policies = db.query(Farm).count()
    pending_agents = db.query(Agent).filter(Agent.is_active == False).count()
    pool_balance = db.query(func.sum(Payment.amount_kes)).scalar() or 0.0

    return {
        "total_farmers": total_farmers,
        "active_policies": active_policies,
        "pending_agents": pending_agents,
        "pool_balance_kes": pool_balance + 4500000.0
    }


@router.get("/pool/health")
def get_pool_health(
    db: Session = Depends(get_db),
) -> Dict[str, Any]:
    pool_balance = db.query(func.sum(Payment.amount_kes)).scalar() or 0.0
    pool_balance += 4500000.0
    coverage_liability = 2500000.0
    ratio = (pool_balance / coverage_liability) * 100 if coverage_liability > 0 else 0
    pool_status = "HEALTHY" if ratio >= 150 else "WARNING"

    return {
        "pool_balance": pool_balance,
        "coverage_liability": coverage_liability,
        "ratio_percentage": round(ratio, 1),
        "status": pool_status,
        "target_ratio": 150.0
    }


@router.get("/agents/pending")
def get_pending_agents(
    db: Session = Depends(get_db),
):
    agents = db.query(Agent).filter(Agent.is_active == False).all()
    return [
        {"id": a.id, "full_name": a.full_name, "phone_number": a.phone_number, "is_active": a.is_active}
        for a in agents
    ]


@router.put("/agents/{agent_id}/approve")
def approve_agent(
    agent_id: str,
    db: Session = Depends(get_db),
):
    agent = db.query(Agent).filter(Agent.id == agent_id).first()
    if not agent:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Agent not found")

    agent.is_active = True
    db.commit()
    db.refresh(agent)
    return {"id": agent.id, "full_name": agent.full_name, "phone_number": agent.phone_number, "is_active": agent.is_active}
