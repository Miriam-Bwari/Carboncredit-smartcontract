# Backend API Updates: Mvua AI (Confidence Score)

**Date:** June 13, 2026
**Target Audience:** Backend Engineer

## Overview
We've made a minor, backward-compatible tweak to the `get_advice` endpoint (`/api/advice/{farm_id}`) to expose the AI confidence score for the Android app. 

Previously, the Android client's `EarlyWarningScreen` (Mvua AI) was showing `0%` confidence because the `confidence_score` field was only returned by the Carbon Credits API, but not by the Advice API. To avoid forcing the Android app to make duplicate network calls, we've enriched the existing Advice response.

## Changes Made

### `backend/routers/advice.py`
We added logic to compute and return a `confidence_score` (integer, 0-100) directly in the `get_advice` endpoint response.

**Calculation Logic:**
The score is derived from the existing `CarbonRecord` query (keeping it DRY):
1. **Recency Score (60% weight):** A full score is given if the farm was scanned today, decaying to 0 over 14 days.
2. **NDVI Quality Score (40% weight):** Values near `0.5` are considered high-quality signals. Extreme values (near `0` or `1`) are penalized as they usually indicate cloud cover or noise.

If no scan data exists, it gracefully and honestly returns `0`.

**Resulting DTO Change:**
The returned dictionary now includes `confidence_score`:
```python
    advice = get_recommendations(crop_type, ndvi, county)
    advice["confidence_score"] = confidence  # <--- NEW FIELD

    return advice
```

## Action Items for Backend Engineer
- Please review the confidence calculation in `backend/routers/advice.py`. It is currently calculating the score based on the `CarbonRecord` recency and NDVI quality.
- If you have a different ML model or formula you'd prefer for "Mvua AI" confidence, feel free to adjust the algorithm in `advice.py`. The Android app is now successfully parsing the integer `confidence_score` from this endpoint. No Android-side changes will be required as long as it remains an integer in the Advice response.
