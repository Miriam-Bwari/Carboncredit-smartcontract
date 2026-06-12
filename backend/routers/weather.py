from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database.connection import get_db
from database.models import Farm
import httpx
from datetime import datetime, timedelta

router = APIRouter()

ARCHIVE_URL = "https://archive-api.open-meteo.com/v1/archive"


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
        }

    except Exception as e:
        print(f"[WEATHER] API error for farm {farm_id}: {e}")
        raise HTTPException(
            status_code=503,
            detail="Weather data temporarily unavailable. Please try again later.",
        )

