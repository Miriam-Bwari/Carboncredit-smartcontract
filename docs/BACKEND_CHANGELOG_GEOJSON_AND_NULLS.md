# Shamba Guard API Changelog & Backend Requirements

This document outlines the recent modifications made to the Android application's backend models and API calls, along with the corresponding tasks the backend engineer needs to complete to support these features.

## 1. Scan Status Endpoint (Admin Dashboard)

The Admin Dashboard now features a "Sentinel-2 Scan Status" health card that displays real-time statistics regarding farm scanning.

### Android Changes
- Added a new Data Transfer Object (DTO) in `AdminDto.kt`:
  ```kotlin
  data class CarbonScanStatusDto(
      @SerializedName("total_farms") val totalFarms: Int,
      @SerializedName("scanned_farms") val scannedFarms: Int,
      @SerializedName("pending_farms") val pendingFarms: Int,
  )
  ```
- Added a new GET endpoint to `AdminApi.kt`:
  ```kotlin
  @GET("carbon/scan-status")
  suspend fun getCarbonScanStatus(): Response<CarbonScanStatusDto>
  ```

### 🔴 Backend Requirements
- **Create Endpoint:** Implement the `GET /carbon/scan-status` endpoint.
- **Calculate Stats:** 
  - `total_farms`: Total number of registered farms in the database.
  - `scanned_farms`: Number of farms that have successfully received a Sentinel-2 Earth Engine scan and have NDVI data within the current period.
  - `pending_farms`: Number of farms that are awaiting a scan or have `null` data from the Earth Engine satellite.
- **Return Format:** The endpoint must return a JSON object matching the `CarbonScanStatusDto` schema above.

## 2. Weather Data Endpoint Update (Farmer Dashboard)

The app now dynamically calculates historical rainfall metrics instead of using hardcoded baselines.

### Android Changes
- Updated `WeatherDto` in `FarmDtos.kt`:
  ```kotlin
  data class WeatherDto(
      @SerializedName("farm_id") val farmId: String,
      @SerializedName("rainfall_mm") val rainfallMm: Float,
      @SerializedName("rainfall_delta_percent") val rainfallDeltaPercent: Float,
  )
  ```

### 🔴 Backend Requirements
- **Update Endpoint:** Modify the existing `/farms/{farmId}/weather` endpoint.
- **Calculate Delta:** Implement logic (e.g., using Open-Meteo historical data) to calculate `rainfall_delta_percent`. This should represent the percentage difference in rainfall for the current month compared to the historical average (or the same month last year).
- **Format:** Ensure the `rainfall_delta_percent` is returned as a float (e.g., `-15.5` for a 15.5% decrease).

## 3. Earth Engine & Null Handling (Critical Fix)

### Android Changes
- The Android app has been refactored to **strictly remove all fallback/mock data** if Earth Engine data is missing.
- The UI now gracefully handles `null` values by displaying a "Scan Pending" state to the farmers.

### 🔴 Backend Requirements
- **Stop Returning Mock Data:** Ensure that the Python backend `earth_engine.py` (or equivalent service) does **not** return fake data when satellite scans fail or data is unavailable.
- **Return Null/404:** If a farm has no valid scan, the backend should return `null` for the relevant fields or a `404 Not Found` for the specific scan report, allowing the client to show the "Scan Pending" UI.

## 4. GeoJSON Strict Enforcement

### Android Changes
- The client now strictly maps Google Maps polygons to `GeoJSON` structures (`GeoJsonPolygonDto`) for all farm registrations and map rendering.

### 🔴 Backend Requirements
- **Validation:** Ensure the backend validates incoming farm registration payloads to ensure the `boundary_coords` strictly adhere to the GeoJSON Polygon standard `[[[lon, lat], [lon, lat], ...]]]`.
- **Area Calculation:** Verify that backend area calculations (e.g., `area_hectares`) handle the standard GeoJSON coordinate ordering (Longitude, Latitude).
