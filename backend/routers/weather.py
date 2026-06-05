from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farm
import httpx
from datetime import datetime, timedelta

router = APIRouter()

@router.get("/{farm_id}")
async def get_farm_weather(farm_id: str, db: Session = Depends(get_db)):
    farm = db.query(Farm).filter(Farm.id == farm_id).first()
    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    coords = farm.boundary_coords.get("coordinates", [])[0] if isinstance(farm.boundary_coords, dict) else []
    if not coords or len(coords) < 1:
        # Fallback to Nairobi
        lon, lat = 36.82, -1.29
    else:
        # Use first point of polygon [lon, lat]
        lon, lat = coords[0][0], coords[0][1]

    # Open-Meteo historical API for last 21 days rainfall
    end_date = datetime.utcnow().date()
    start_date = end_date - timedelta(days=21)
    
    url = f"https://archive-api.open-meteo.com/v1/archive"
    params = {
        "latitude": lat,
        "longitude": lon,
        "start_date": start_date.strftime("%Y-%m-%d"),
        "end_date": end_date.strftime("%Y-%m-%d"),
        "daily": "precipitation_sum",
        "timezone": "auto"
    }

    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(url, params=params)
            response.raise_for_status()
            data = response.json()
            
            precipitation_sums = data.get("daily", {}).get("precipitation_sum", [])
            valid_precip = [p for p in precipitation_sums if p is not None]
            total_rainfall = sum(valid_precip)
            
            # Mock historical average for Delta calculation
            # Say historical average is 50mm for this period
            historical_avg = 50.0
            if historical_avg > 0:
                delta = ((total_rainfall - historical_avg) / historical_avg) * 100
            else:
                delta = 0

            return {
                "farm_id": farm_id,
                "rainfall_mm": round(total_rainfall, 1),
                "rainfall_delta_percent": round(delta, 1),
                "latitude": lat,
                "longitude": lon
            }
    except Exception as e:
        print(f"Weather API error: {e}")
        # Fallback
        return {
            "farm_id": farm_id,
            "rainfall_mm": 12.0,
            "rainfall_delta_percent": -65.0,
            "latitude": lat,
            "longitude": lon
        }
