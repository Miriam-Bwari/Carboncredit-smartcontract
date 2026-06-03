package dev.korryr.shambaguard.core.network

import com.google.gson.annotations.SerializedName

/**
 * Strict GeoJSON Polygon representation.
 * Coordinates are formatted as: [ [ [longitude, latitude], [longitude, latitude], ... ] ]
 * The outer list represents the polygon, the inner list represents the linear ring (boundary),
 * and the innermost list contains exactly 2 doubles: [longitude, latitude].
 */
data class GeoJsonPolygonDto(
    @SerializedName("type") val type: String = "Polygon",
    @SerializedName("coordinates") val coordinates: List<List<List<Double>>>
)
