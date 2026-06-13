from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farm
import httpx
from datetime import datetime, timedelta

router = APIRouter()

ARCHIVE_URL = "https://archive-api.open-meteo.com/v1/archive"
FORECAST_URL = "https://api.open-meteo.com/v1/forecast"

async def fetch_forecast(client: httpx.AsyncClient, lat: float, lon: float) -> list:
    """Fetch 7-day rainfall forecast from Open-Meteo API."""
    response = await client.get(
        FORECAST_URL,
        params={
            "latitude": lat,
            "longitude": lon,
            "daily": "precipitation_sum",
            "timezone": "auto",
            "forecast_days": 7,
        },
        timeout=30,
    )
    response.raise_for_status()
    data = response.json()
    
    forecast_days = []
    daily = data.get("daily", {})
    times = daily.get("time", [])
    precips = daily.get("precipitation_sum", [])
    
    for i in range(len(times)):
        try:
            date_obj = datetime.strptime(times[i], "%Y-%m-%d")
            day_name = date_obj.strftime("%a") # e.g. "Mon"
            val = precips[i]
            rain_mm = int(round(val)) if val is not None else 0
            forecast_days.append({
                "day": day_name,
                "has_rain": rain_mm > 0,
                "rainfall_mm": rain_mm
            })
        except Exception:
            pass
            
    return forecast_days


async def fetch_rainfall(client: httpx.AsyncClient, lat: float, lon: float, start: str, end: str) -> float:
    """Fetch total precipitation for a date range from Open-Meteo archive."""
    response = await client.get(
        ARCHIVE_URL,
        params={
            "latitude": lat,
            "longitude": lon,
            "start_date": start,
            "end_date": end,
            "daily": "precipitation_sum",
            "timezone": "auto",
        },
        timeout=30,
    )
    response.raise_for_status()
    data = response.json()
    values = data.get("daily", {}).get("precipitation_sum", [])
    return round(sum(v for v in values if v is not None), 1)


@router.get("/{farm_id}")
async def get_farm_weather(farm_id: str, db: Session = Depends(get_db)):
    farm = db.query(Farm).filter(Farm.id == farm_id).first()
    if not farm:
        raise HTTPException(status_code=404, detail="Farm not found")

    coords = farm.boundary_coords.get("coordinates", [])[0] if isinstance(farm.boundary_coords, dict) else []
    if not coords or len(coords) < 1:
        lon, lat = 36.82, -1.29  # Default: Nairobi
    else:
        lon, lat = coords[0][0], coords[0][1]

    # Current window: last 21 days
    end_date = datetime.utcnow().date()
    start_date = end_date - timedelta(days=21)

    # Baseline window: same 21-day period one year ago
    baseline_end = end_date - timedelta(days=365)
    baseline_start = start_date - timedelta(days=365)

    try:
        async with httpx.AsyncClient() as client:
            current_rainfall = await fetch_rainfall(
                client, lat, lon,
                start_date.strftime("%Y-%m-%d"),
                end_date.strftime("%Y-%m-%d"),
            )
            historical_avg = await fetch_rainfall(
                client, lat, lon,
                baseline_start.strftime("%Y-%m-%d"),
                baseline_end.strftime("%Y-%m-%d"),
            )
            forecast_data = await fetch_forecast(client, lat, lon)

        if historical_avg > 0:
            delta = ((current_rainfall - historical_avg) / historical_avg) * 100
        else:
            delta = 0.0

        return {
            "farm_id": farm_id,
            "rainfall_mm": current_rainfall,
            "rainfall_delta_percent": round(delta, 1),
            "historical_avg_mm": historical_avg,
            "latitude": lat,
            "longitude": lon,
            "forecast": forecast_data,
        }

    except Exception as e:
        print(f"[WEATHER] API error for farm {farm_id}: {e}")
        raise HTTPException(
            status_code=503,
            detail="Weather data temporarily unavailable. Please try again later.",
        )

