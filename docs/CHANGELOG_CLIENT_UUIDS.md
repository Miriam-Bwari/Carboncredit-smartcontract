# Backend Changelog: Client-Side UUID Idempotency

**Date:** June 9, 2026
**Topic:** Offline-First Sync Architecture Updates

Hello Backend Team,

We have made some necessary architectural changes to the API schemas to support robust offline-first synchronization for the Android app. 

Previously, when the Android app (specifically the Agent app) registered a new Farmer or Farm while offline, it generated a local UUID. When it eventually synced with the backend, the backend would generate a *new* database UUID and return it. This forced the Android app to perform complex "UUID swapping" (updating foreign keys for all child entities like Reports or Payments that relied on that temporary local ID). 

To eliminate this fragility and ensure idempotency during intermittent network retries, we have shifted to **Client-Side UUID Generation**.

## What Changed in the Codebase

### `routers/farmers.py` & `routers/farms.py`
We updated the FastAPI Request Schemas to accept an optional `id` field provided by the client:

1. **`FarmerRegister` Schema:**
   - Added an optional `id: Optional[str] = None` field.
   - The `register_farmer` endpoint logic was updated to use this client-provided ID when creating the database row. If the client doesn't provide one, it falls back to the previous behavior of generating one on the server.

2. **`FarmRegister` Schema:**
   - Added an optional `id: Optional[str] = None` field.
   - The `register_farm` endpoint logic was updated to use this client-provided ID when creating the database row.

## Why this is important
By allowing the Android client to specify the UUIDs, the client can safely retry failed network requests without the risk of creating duplicate entries. Furthermore, it completely eliminates the need for cascading ID updates in the local SQLite database.

## Action Items for Backend Team
- No immediate action is required on your part. This changelog is purely informational to explain why the API schemas now accept an `id` field.
- If you build any future `POST` endpoints (e.g., submitting Carbon Evidence Photos or new Practice Logs), please ensure the schema accepts an optional `id` so the Android app can continue generating its own UUIDs for offline queuing!
