from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farm, CarbonRecord, Payment
from core.security import get_current_user
from schemas.responses import StkPushResponse, PaymentStatusResponse
from services.mpesa import send_mpesa_payment
from pydantic import BaseModel

router = APIRouter()


class StkPushRequest(BaseModel):
    farmer_id: str
    phone_number: str
    amount_kes: int


@router.post("/stk-push", response_model=StkPushResponse)
def trigger_stk_push(
    data: StkPushRequest,
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_user)
):
    if data.amount_kes <= 0:
        raise HTTPException(status_code=400, detail="Amount must be greater than 0")

    result = send_mpesa_payment(data.phone_number, data.amount_kes)

    # Save a pending payment record regardless of outcome
    payment = Payment(
        farmer_id=data.farmer_id,
        checkout_id=result.get("checkout_id"),
        amount_kes=data.amount_kes,
        status="pending" if result.get("success") else "failed"
    )
    db.add(payment)
    db.commit()

    return StkPushResponse(
        success=result.get("success", False),
        message=result.get("message", ""),
        checkout_id=result.get("checkout_id"),
        merchant_id=result.get("merchant_id")
    )


@router.post("/callback")
def mpesa_callback(payload: dict):
    # Safaricom sends a POST to this URL after payment confirmation
    # Extract the result and update the payment status in the database
    # Note: This endpoint must be publicly accessible (not protected by auth)
    try:
        body = payload.get("Body", {}).get("stkCallback", {})
        checkout_id = body.get("CheckoutRequestID")
        result_code = body.get("ResultCode")
        return {"received": True, "checkout_id": checkout_id, "result_code": result_code}
    except Exception:
        return {"received": False}


@router.get("/status/{checkout_id}", response_model=PaymentStatusResponse)
def payment_status(checkout_id: str, db: Session = Depends(get_db), current_user: dict = Depends(get_current_user)):
    payment = db.query(Payment).filter(Payment.checkout_id == checkout_id).first()
    if not payment:
        raise HTTPException(status_code=404, detail="Payment not found")

    return PaymentStatusResponse(
        checkout_id=checkout_id,
        status=payment.status,
        amount_kes=payment.amount_kes,
        mpesa_reference=payment.mpesa_reference
    )
