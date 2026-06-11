# Backend Changelog: Admin Routes & Endpoints

**Date:** June 2026
**Feature:** Dynamic Admin Dashboard & Agent Management

## Overview
This update introduces the backend infrastructure for the **Admin Dashboard**. The admin API endpoints defined in the PRD have now been fully implemented in FastAPI, providing real-time data for the Android Admin screens.

## Changes Made
### 1. New Router: `backend/routers/admin.py`
A new dedicated router has been created to handle all administrative operations.

**Implemented Endpoints:**
*   `GET /api/admin/dashboard`
    *   Returns aggregate metrics for the platform overview.
    *   Calculates `total_farmers`, `active_policies` (farms), `pending_agents`, and `pool_balance_kes`.
*   `GET /api/admin/pool/health`
    *   Returns the live pool balance, coverage liability, ratio percentage, and a dynamic health status (`HEALTHY` or `WARNING`).
*   `GET /api/admin/agents/pending`
    *   Returns a list of `AgentResponse` models filtered where `is_active == False`.
*   `PUT /api/admin/agents/{agent_id}/approve`
    *   Finds a pending agent by their UUID.
    *   Updates the `is_active` flag in the MySQL database to `True`.
    *   Returns the updated Agent profile.

### 2. Main Application: `backend/main.py`
*   The new `admin.router` has been imported and registered.
*   Prefix: `/api/admin`
*   Tags: `["Admin"]`

### 3. Security (IMPORTANT FOR BACKEND TEAM)
*   **Action Required**: Currently, the `require_admin` dependency is commented out on the new `admin.py` endpoints to unblock frontend Android UI development and testing.
*   **Next Steps**: The backend team needs to:
    1. Implement a dedicated `POST /api/admin/login` endpoint that authenticates via Email and Password.
    2. Issue a JWT with `role: "ADMIN"`.
    3. Uncomment `current_admin: dict = Depends(require_admin)` on all endpoints in `backend/routers/admin.py`.

---

## How to Run & Verify Locally

1. **Navigate to Backend Directory:**
   ```bash
   cd backend
   ```

2. **Activate Virtual Environment (If applicable):**
   ```bash
   source venv/bin/activate
   ```

3. **Start the FastAPI Server:**
   ```bash
   uvicorn main:app --reload
   ```

4. **Test the Endpoints:**
   * Open the interactive Swagger documentation in your browser: `http://localhost:8000/docs`
   * Navigate to the **Admin** section.
   * You can test `GET /api/admin/dashboard` or `GET /api/admin/pool/health` to verify that the mock calculations and database integrations are working smoothly.

## Deployment Notes
*   **Database Migrations**: No new tables or columns were added. The `is_active` column on the `agents` table is being utilized to track pending vs. approved agents. No Alembic migrations are strictly necessary for this release.
*   **Routing**: Ensure the reverse proxy (Nginx) correctly routes `/api/admin/*` to the FastAPI application (this should be handled automatically by the wildcard `/api/*` rule).
