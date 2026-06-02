import requests
import base64
import os
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

CONSUMER_KEY    = os.getenv('MPESA_CONSUMER_KEY')
CONSUMER_SECRET = os.getenv('MPESA_CONSUMER_SECRET')
SHORTCODE       = os.getenv('MPESA_SHORTCODE', '174379')
PASSKEY         = os.getenv('MPESA_PASSKEY')
CALLBACK_URL    = os.getenv('MPESA_CALLBACK_URL')


AUTH_URL = 'https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials'
STK_URL  = 'https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest'


# ── ACCESS TOKEN ─────────────────────────────────────────────────────────
def get_access_token() -> str:
    if not CONSUMER_KEY or not CONSUMER_SECRET:
        raise Exception("Missing MPESA_CONSUMER_KEY or MPESA_CONSUMER_SECRET")

    credentials = base64.b64encode(
        f"{CONSUMER_KEY}:{CONSUMER_SECRET}".encode()
    ).decode()

    try:
        response = requests.get(
            AUTH_URL,
            headers={"Authorization": f"Basic {credentials}"},
            timeout=30
        )

        data = response.json()
        return data.get("access_token")

    except Exception as e:
        raise Exception(f"Token request failed: {str(e)}")


# ── PASSWORD GENERATION ─────────────────────────────────────────────────
def make_password_and_timestamp():
    timestamp = datetime.now().strftime('%Y%m%d%H%M%S')
    raw = f"{SHORTCODE}{PASSKEY}{timestamp}"
    password = base64.b64encode(raw.encode()).decode()
    return password, timestamp


# ── PHONE VALIDATION ─────────────────────────────────────────────────────
def validate_phone(phone: str) -> str:
    """
    Converts phone to 2547XXXXXXXX format
    """
    if phone.startswith("0"):
        phone = "254" + phone[1:]
    if phone.startswith("+"):
        phone = phone[1:]
    if not phone.startswith("254"):
        raise Exception("Phone must be in format 2547XXXXXXXX")

    return phone


# ── STK PUSH ─────────────────────────────────────────────────────────────
def send_mpesa_payment(phone_number: str, amount: int) -> dict:
    try:
        phone_number = validate_phone(phone_number)

        if amount <= 0:
            return {"success": False, "message": "Invalid amount"}

        token = get_access_token()
        password, timestamp = make_password_and_timestamp()

        payload = {
            "BusinessShortCode": SHORTCODE,
            "Password": password,
            "Timestamp": timestamp,
            "TransactionType": "CustomerPayBillOnline",
            "Amount": amount,
            "PartyA": phone_number,
            "PartyB": SHORTCODE,
            "PhoneNumber": phone_number,
            "CallBackURL": CALLBACK_URL,
            "AccountReference": "ShambaGuard",
            "TransactionDesc": "Carbon Credit Payment"
        }

        response = requests.post(
            STK_URL,
            json=payload,
            headers={"Authorization": f"Bearer {token}"},
            timeout=30
        )

        try:
            data = response.json()
        except Exception:
            return {
                "success": False,
                "message": "Invalid response from Safaricom",
                "raw": response.text
            }

        if data.get("ResponseCode") == "0":
            return {
                "success": True,
                "message": "STK Push sent successfully",
                "checkout_id": data.get("CheckoutRequestID"),
                "merchant_id": data.get("MerchantRequestID")
            }

        return {
            "success": False,
            "message": data.get("errorMessage", "STK Push failed"),
            "code": data.get("errorCode")
        }

    except Exception as e:
        return {
            "success": False,
            "message": str(e)
        }


# ── TEST CONNECTION ─────────────────────────────────────────────────────
def test_mpesa_connection():
    try:
        token = get_access_token()
        return {
            "success": True,
            "message": "Daraja API working",
            "token_preview": token[:15] + "..."
        }
    except Exception as e:
        return {
            "success": False,
            "message": str(e)
        }