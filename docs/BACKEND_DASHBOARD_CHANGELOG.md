# Backend Changelog: Dashboard Integration API Updates

**Date:** June 5, 2026  
**Component:** FastAPI Backend  
**Target Audience:** Backend Engineering / DevOps Team  

## Overview
This changelog details the recent API additions made to support the live data integration for the Android Farmer and Agent dashboards. Hardcoded mock data on the mobile frontend has been replaced with these live endpoints.

---

## 1. New Endpoints Added

### A. Weather API (New Router)
- **File:** `routers/weather.py` (New)
- **Endpoint:** `GET /api/weather/{farm_id}`
- **Description:** Integrates with the free, public [Open-Meteo Historical API](https://open-meteo.com/) to fetch the last 21 days of precipitation data for a given farm's GPS coordinates. It calculates the total `rainfall_mm` and the `rainfall_delta_percent` dynamically.
- **Notes for Backend:** 
  - This uses Python's `httpx` client to make async HTTP requests.
  - The `weather.router` has been successfully registered in `main.py`.
  - **No new API keys or environment variables** are required since Open-Meteo is open-access.

### B. Policy Status API
- **File:** `routers/payments.py`
- **Endpoint:** `GET /api/payments/policy/{farmer_id}`
- **Description:** Checks the `Payment` table for any recent `COMPLETED` M-PESA transactions linked to the `farmer_id`. If a valid payment is found within the last 365 days, it returns `is_active: True` and an `expiry_date`.

### C. Agent Dashboard Metrics
- **File:** `routers/agents.py`
- **Endpoint:** `GET /api/agents/dashboard/{agent_id}`
- **Description:** Aggregates statistics for an Agent. 
  - Calculates `total_farms` registered by the agent.
  - Calculates `newThisMonth` based on `Farm.created_at`.
  - Returns a joined list of `RecentRegistrations` (Farm data joined with Farmer details).

---

## 2. Deployment & Run Instructions

When pulling these changes to your local environment or deploying to production, please follow these steps:

### Dependencies
The new `weather.py` router relies on `httpx`. Please ensure your environment has it installed.
```bash
pip install httpx
```
*(If you maintain a `requirements.txt`, please add `httpx` to it).*

### Local Testing
No changes to the startup command. You can run the server as usual:
```bash
uvicorn main:app --reload --port 8000
```

### Database Migrations
**None required.** All queries utilize the existing SQLAlchemy `Farm`, `Farmer`, and `Payment` models. No schema changes were introduced in this update.

---

## 3. Frontend Integration Status
The Android engineering team has already updated the Retrofit `FarmApi` and `AgentApi` to consume these exact paths. **Please do not alter the response schemas** (keys like `rainfall_mm`, `is_active`, `farmersRegistered`) without coordinating with the mobile team, as it will break the Kotlin DTO deserialization.
