# Shared JWT creation, validation, and role guard dependencies.

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import jwt, JWTError
from datetime import datetime, timedelta
from typing import Optional
import os

SECRET_KEY = os.getenv("SECRET_KEY", "fallback_secret_key")
ALGORITHM  = "HS256"
TOKEN_EXPIRE_HOURS = 24

bearer_scheme = HTTPBearer()


def create_access_token(user_id: str, role: str) -> str:
    payload = {
        "sub":  user_id,
        "role": role,
        "exp":  datetime.utcnow() + timedelta(hours=TOKEN_EXPIRE_HOURS)
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme)
) -> dict:
    token = credentials.credentials
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id: Optional[str] = payload.get("sub")
        role: Optional[str]    = payload.get("role")
        if not user_id or not role:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token payload")
        return {"user_id": user_id, "role": role}
    except JWTError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token is invalid or has expired"
        )


def require_farmer(current_user: dict = Depends(get_current_user)) -> dict:
    if current_user["role"] not in ("Farmer", "Admin"):
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Farmer access required")
    return current_user


def require_agent(current_user: dict = Depends(get_current_user)) -> dict:
    if current_user["role"] not in ("Agent", "Admin"):
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Agent access required")
    return current_user


def require_admin(current_user: dict = Depends(get_current_user)) -> dict:
    if current_user["role"] != "Admin":
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Admin access required")
    return current_user
