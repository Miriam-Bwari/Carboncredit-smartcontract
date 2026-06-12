import ee
import os
import hashlib
import random
from datetime import datetime, timedelta
from dotenv import load_dotenv

load_dotenv()

# ── Earth Engine initialization guard ─────────────────────────────
_EE_INITIALIZED = False


def init_earth_engine():
    global _EE_INITIALIZED

    if _EE_INITIALIZED:
        return

    service_account = os.getenv('EARTH_ENGINE_SERVICE_ACCOUNT')
    key_file = os.getenv('EARTH_ENGINE_KEY_FILE', 'ee_service_account.json')

    credentials = ee.ServiceAccountCredentials(service_account, key_file)
    ee.Initialize(credentials)

    _EE_INITIALIZED = True


# ── REAL NDVI FUNCTION ────────────────────────────────────────────
def get_farm_ndvi(boundary_coords: list, scan_date: str) -> dict | None:
    """
    Computes NDVI using Sentinel-2 imagery for a farm polygon.
    Returns None if satellite data is unavailable (cloudy, GEE down, etc.)
    so callers can surface a proper 'data unavailable' message rather than
    showing fabricated values.
    """

    try:
        init_earth_engine()

        # Convert [lat, lng] to GEE [lng, lat]
        ee_coords = [[lng, lat] for lat, lng in boundary_coords]
        geometry = ee.Geometry.Polygon([ee_coords])

        start = scan_date
        end = (
            datetime.strptime(scan_date, "%Y-%m-%d") + timedelta(days=14)
        ).strftime("%Y-%m-%d")

        collection = (
            ee.ImageCollection("COPERNICUS/S2_SR_HARMONIZED")
            .filterDate(start, end)
            .filterBounds(geometry)
            .filter(ee.Filter.lt("CLOUDY_PIXEL_PERCENTAGE", 20))
            .sort("CLOUDY_PIXEL_PERCENTAGE")
        )

        if collection.size().getInfo() == 0:
            print(f"[GEE] No cloud-free imagery for scan date {scan_date} — skipping scan")
            return None

        image = collection.first()

        ndvi = image.normalizedDifference(["B8", "B4"]).rename("NDVI")

        stats = ndvi.reduceRegion(
            reducer=ee.Reducer.mean(),
            geometry=geometry,
            scale=10,
            maxPixels=1e9
        ).getInfo()

        ndvi_value = stats.get("NDVI")

        if ndvi_value is None:
            print("[GEE] NDVI computation returned null — skipping scan")
            return None

        raw = f"{boundary_coords}{scan_date}{ndvi_value}"
        hash_val = hashlib.sha256(raw.encode()).hexdigest()

        return {
            "ndvi": round(ndvi_value, 4),
            "data_hash": hash_val,
            "scan_date": scan_date,
            "source": "Sentinel-2 COPERNICUS/S2_SR_HARMONIZED"
        }

    except Exception as e:
        print(f"[GEE] Satellite scan failed: {e}")
        return None


# ── TEST CONNECTION ───────────────────────────────────────────────
def test_gee_connection():
    try:
        init_earth_engine()

        point = ee.Geometry.Point([36.8219, -1.2921])
        _ = ee.Image(1).getInfo()

        print("GEE CONNECTION OK — Sentinel-2 ready")
        return True

    except Exception as e:
        print(f"GEE CONNECTION FAILED: {e}")
        return False