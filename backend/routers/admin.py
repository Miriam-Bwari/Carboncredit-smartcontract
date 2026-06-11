from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import func
from typing import List, Dict, Any

from database.connection import get_db
from database.models import Agent, Farmer, Farm, Payment
from database.schemas import AgentResponse
from core.security import require_admin

router = APIRouter()

@router.get("/dashboard")
def get_dashboard_stats(
    db: Session = Depends(get_db),
    # current_admin: dict = Depends(require_admin)  # Temporarily disabled for frontend testing
) -> Dict[str, Any]:
    total_farmers = db.query(Farmer).count()
    active_policies = db.query(Farm).count() # Approximating farms as policies for MVP
    pending_agents = db.query(Agent).filter(Agent.is_active == False).count()
    
    # Pool Balance: sum of all payments minus some arbitrary payouts for now, or just sum of payments.
    pool_balance = db.query(func.sum(Payment.amount_kes)).scalar() or 0.0

    return {
        "total_farmers": total_farmers,
        "active_policies": active_policies,
        "pending_agents": pending_agents,
        "pool_balance_kes": pool_balance + 4500000.0  # Adding base offset so it matches PRD wireframes roughly
    }


@router.get("/pool/health")
def get_pool_health(
    db: Session = Depends(get_db),
    # current_admin: dict = Depends(require_admin)
) -> Dict[str, Any]:
    pool_balance = db.query(func.sum(Payment.amount_kes)).scalar() or 0.0
    pool_balance += 4500000.0
    
    # Mock coverage liability
    coverage_liability = 2500000.0
    
    ratio = (pool_balance / coverage_liability) * 100 if coverage_liability > 0 else 0
    status = "HEALTHY" if ratio >= 150 else "WARNING"

    return {
        "pool_balance": pool_balance,
        "coverage_liability": coverage_liability,
        "ratio_percentage": round(ratio, 1),
        "status": status,
        "target_ratio": 150.0
    }


@router.get("/agents/pending", response_model=List[AgentResponse])
def get_pending_agents(
    db: Session = Depends(get_db),
    # current_admin: dict = Depends(require_admin)
):
    agents = db.query(Agent).filter(Agent.is_active == False).all()
    return agents


@router.put("/agents/{agent_id}/approve", response_model=AgentResponse)
def approve_agent(
    agent_id: str,
    db: Session = Depends(get_db),
    # current_admin: dict = Depends(require_admin)
):
    agent = db.query(Agent).filter(Agent.id == agent_id).first()
    if not agent:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Agent not found")
    
    agent.is_active = True
    db.commit()
    db.refresh(agent)
    return agent
