"""
services/scan_scheduler.py

Background scheduler that automatically triggers Sentinel-2 satellite
scans for every registered farm every 5 days — matching the Sentinel-2
revisit cadence.

Uses APScheduler (AsyncIOScheduler) which integrates cleanly with FastAPI's
async event loop. No extra worker processes needed.

Dependency: pip install apscheduler
"""

import logging
from datetime import datetime, timedelta

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.interval import IntervalTrigger

from database.connection import SessionLocal
from database.models import Farm, CarbonRecord
from ai.earth_engine import get_farm_ndvi
from ai.carbon_calculator import estimate_carbon_kg

logger = logging.getLogger(__name__)

scheduler = AsyncIOScheduler()

SCAN_INTERVAL_DAYS = 5  # Matches Sentinel-2 revisit time


def _should_scan(farm_id: str, db) -> bool:
    """
    Returns True if no scan exists, or the last scan is older than
    SCAN_INTERVAL_DAYS. Prevents unnecessary GEE API calls.
    """
    latest = (
        db.query(CarbonRecord)
        .filter(CarbonRecord.farm_id == farm_id)
        .order_by(CarbonRecord.scan_date.desc())
        .first()
    )
    if latest is None:
        return True  # Never scanned — scan now
    age = datetime.utcnow() - latest.scan_date
    return age >= timedelta(days=SCAN_INTERVAL_DAYS)


def _scan_single_farm(farm: Farm, db) -> None:
    """Runs a Sentinel-2 NDVI scan for a single farm and saves the result."""
    farm_id = farm.id
    today = datetime.utcnow().strftime("%Y-%m-%d")

    logger.info(f"[SCHEDULER] Scanning farm {farm_id} ({farm.name})")

    # Extract boundary from GeoJSON — backend stores as [lng, lat]
    boundary = farm.boundary_coords
    if not boundary:
        logger.warning(f"[SCHEDULER] Farm {farm_id} has no boundary — skipping")
        return

    coords = boundary.get("coordinates", [[]])[0] if isinstance(boundary, dict) else []
    if len(coords) < 3:
        logger.warning(f"[SCHEDULER] Farm {farm_id} boundary too small — skipping")
        return

    # GEE expects [[lat, lng], ...] — convert from GeoJSON [lng, lat]
    lat_lng_coords = [[pt[1], pt[0]] for pt in coords]

    result = get_farm_ndvi(lat_lng_coords, today)

    if not result or result.get("ndvi") is None:
        logger.error(f"[SCHEDULER] No NDVI returned for farm {farm_id}")
        return

    current_ndvi = float(result["ndvi"])
    data_hash = result.get("data_hash")
    is_mock = result.get("is_mock", True)

    # Skip saving if this exact data_hash was already stored (deduplication)
    if data_hash:
        existing = db.query(CarbonRecord).filter(
            CarbonRecord.ndvi_data_hash == data_hash
        ).first()
        if existing:
            logger.info(f"[SCHEDULER] Duplicate scan for farm {farm_id} — skipping save")
            return

    prev_record = (
        db.query(CarbonRecord)
        .filter(CarbonRecord.farm_id == farm_id)
        .order_by(CarbonRecord.scan_date.desc())
        .first()
    )
    prev_ndvi = float(prev_record.ndvi_value) if prev_record and prev_record.ndvi_value else 0.1

    calc = estimate_carbon_kg(current_ndvi, prev_ndvi, farm.area_hectares or 0)
    carbon_kg = calc.get("carbon_kg", 0)
    credits = calc.get("credits", 0)

    record = CarbonRecord(
        farm_id=farm_id,
        scan_date=datetime.utcnow(),
        ndvi_value=current_ndvi,
        prev_ndvi=prev_ndvi,
        carbon_kg=carbon_kg,
        carbon_credits=credits,
        is_verified=credits > 0,
        ndvi_data_hash=data_hash,
    )
    db.add(record)
    db.commit()

    source = "REAL Sentinel-2" if not is_mock else "MOCK fallback"
    logger.info(
        f"[SCHEDULER] Farm {farm_id} | NDVI={current_ndvi} | "
        f"Carbon={carbon_kg}kg | Credits={credits} | Source={source}"
    )


def run_all_farm_scans() -> None:
    """
    Entry point called by the scheduler every 5 days.
    Iterates over all farms and scans those that are due.
    """
    db = SessionLocal()
    try:
        farms = db.query(Farm).all()
        logger.info(f"[SCHEDULER] Starting scan cycle — {len(farms)} farm(s) found")

        scanned = 0
        skipped = 0
        for farm in farms:
            if _should_scan(farm.id, db):
                try:
                    _scan_single_farm(farm, db)
                    scanned += 1
                except Exception as e:
                    logger.error(f"[SCHEDULER] Failed scan for farm {farm.id}: {e}")
            else:
                skipped += 1
                logger.debug(f"[SCHEDULER] Farm {farm.id} scan not due yet — skipping")

        logger.info(
            f"[SCHEDULER] Cycle complete — scanned={scanned}, skipped={skipped}"
        )
    finally:
        db.close()


def start_scheduler() -> None:
    """Start the APScheduler background scheduler."""
    scheduler.add_job(
        run_all_farm_scans,
        trigger=IntervalTrigger(days=SCAN_INTERVAL_DAYS),
        id="sentinel2_scan",
        name="Sentinel-2 Farm Scan (every 5 days)",
        replace_existing=True,
        next_run_time=datetime.utcnow(),  # Run immediately on startup, then every 5 days
    )
    scheduler.start()
    logger.info("[SCHEDULER] Sentinel-2 scan scheduler started")


def stop_scheduler() -> None:
    """Gracefully shut down the scheduler on app exit."""
    if scheduler.running:
        scheduler.shutdown(wait=False)
        logger.info("[SCHEDULER] Scheduler stopped")
