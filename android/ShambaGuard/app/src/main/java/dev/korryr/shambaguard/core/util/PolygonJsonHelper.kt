package dev.korryr.shambaguard.core.util

import com.google.android.gms.maps.model.LatLng
import dev.korryr.shambaguard.core.network.GeoJsonPolygonDto
import com.google.gson.Gson

object PolygonJsonHelper {
    private val gson = Gson()

    /**
     * Safely parses a polygon JSON string from the database into a list of LatLng points.
     * Handles both the legacy `[{"lat":..., "lng":...}]` format used during local offline creation,
     * and the strict GeoJSON format synced from the backend.
     */
    fun parseToLatLng(jsonStr: String): List<LatLng> {
        if (jsonStr.isBlank() || jsonStr == "{}") return emptyList()
        
        val coords = mutableListOf<LatLng>()
        try {
            // Check if it's a GeoJSON object (has 'type' and 'coordinates')
            if (jsonStr.contains("Polygon") && jsonStr.contains("coordinates")) {
                // Handle edge case where it was accidentally saved as Kotlin toString() previously
                val cleanJson = if (jsonStr.startsWith("GeoJsonPolygonDto")) {
                    // We can't easily parse the Kotlin toString format, but since we are clearing DB on logout,
                    // this will be replaced with real JSON during the next sync. We just return empty list here.
                    return emptyList()
                } else {
                    jsonStr
                }

                val geoJson = gson.fromJson(cleanJson, GeoJsonPolygonDto::class.java)
                geoJson.coordinates.firstOrNull()?.forEach { point ->
                    // GeoJSON format: [longitude, latitude] -> LatLng expects (latitude, longitude)
                    if (point.size >= 2) {
                        coords.add(LatLng(point[1], point[0]))
                    }
                }
            } else if (jsonStr.startsWith("[")) {
                // Legacy array format: [{"lat": -1.2, "lng": 36.8}]
                val jsonArray = org.json.JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val point = jsonArray.getJSONObject(i)
                    coords.add(LatLng(point.getDouble("lat"), point.getDouble("lng")))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return coords
    }
}
