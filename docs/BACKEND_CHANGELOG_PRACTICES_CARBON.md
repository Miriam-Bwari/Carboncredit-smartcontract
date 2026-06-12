# Backend Changelog: Practice Logs & Dashboard Refinements

**Date:** June 8, 2026  
**Component:** FastAPI Backend  
**Target Audience:** Backend Engineering / DevOps Team  

## Overview
This changelog documents the recent updates to the backend to support the new "Log Practice" feature on the Android Farmer Dashboard, as well as modifications made to support real-time data fetching on the Carbon and Profile tabs.

---

## 1. Database Schema Additions

### A. New `PracticeLog` Model
- **File:** `database/models.py`
- **Description:** Added a new SQLAlchemy model `PracticeLog` to track farmer-submitted agricultural practices.
- **Fields:**
  - `id`: Primary Key (String/UUID)
  - `farm_id`: Foreign Key linking to `Farm.id`
  - `crop_type`: String (e.g., "Maize")
  - `tillage_method`: String (e.g., "Minimum Tillage")
  - `tree_count`: Integer
  - `irrigation_source`: String
  - `created_at`: DateTime (Defaults to UTC now)

### Action Required by Backend Engineer 🚨
1. **Run Migrations:** Because a new table (`practice_logs`) and its corresponding foreign key relationship to the `farms` table were added, **you must execute the database migrations**. 
   - If using Alembic: Generate a new revision and upgrade `head`.
   - If using automatic generation on startup: Ensure `Base.metadata.create_all(bind=engine)` runs.
2. **Review Cascading Deletes:** The relationship is currently established, but please review if `ondelete="CASCADE"` should be explicitly added to ensure `PracticeLog` records are deleted automatically if a `Farm` is deleted.

---

## 2. New Endpoints Added

### A. Practice Logging API
- **File:** `routers/farms.py`
- **Endpoints:**
  - `POST /api/farms/{farm_id}/practices`: Accepts a `PracticeLogCreate` schema and persists it to the database.
  - `GET /api/farms/{farm_id}/practices`: Retrieves a chronological list of practices for a specific farm (returns a list of `PracticeLogResponse`).
- **Schemas Added:** `PracticeLogCreate` and `PracticeLogResponse` were added to `schemas/responses.py` to enforce strict validation.

---

## 3. Existing Endpoints Verified & Consumed

The mobile application now strictly consumes the following existing endpoints to populate the Carbon and Profile tabs. **Please ensure the schemas for these endpoints remain stable, as altering them will break Android DTO parsing.**

- **Carbon History:** `GET /api/carbon/history/{farm_id}` (located in `routers/carbon.py`). The mobile app relies on `total_credits`, `total_carbon_kg`, and the `records` list.
- **Farmer Profile:** `GET /api/farmers/{farmer_id}` (located in `routers/farmers.py`). The mobile app consumes this to pull the `full_name` and `phone_number`.

---

## 4. Deployment & Testing Instructions

1. **No New Dependencies:** No new `pip` packages were introduced in this phase.
2. **Local Testing:** 
   ```bash
   uvicorn main:app --reload --port 8000
   ```
3. **Verify the new table:** After running your schema update, you can test the new endpoints using the auto-generated Swagger documentation at `http://127.0.0.1:8000/docs#/Farms/`.
