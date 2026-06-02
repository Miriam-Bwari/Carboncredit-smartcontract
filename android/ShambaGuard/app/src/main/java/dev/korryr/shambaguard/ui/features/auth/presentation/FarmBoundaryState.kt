package dev.korryr.shambaguard.ui.features.auth.presentation

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.atan2

// Step 2 — farm polygon drawing state
data class FarmBoundaryUiState(
    val points: List<LatLng> = emptyList(),
    val mapType: Int = com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE,
    val isSaving: Boolean = false,
)

// Returns polygon area in acres using the Shoelace formula with haversine correction.
// Requires >= 3 points. Returns null if the polygon is not yet closed.
fun FarmBoundaryUiState.estimatedAreaAcres(): Double? {
    if (points.size < 3) return null
    val closed = points + points.first()
    var area = 0.0
    for (i in 0 until closed.size - 1) {
        val a = closed[i]
        val b = closed[i + 1]
        area += Math.toRadians(a.longitude) * sin(Math.toRadians(b.latitude))
        area -= Math.toRadians(b.longitude) * sin(Math.toRadians(a.latitude))
    }
    // Earth radius in km, convert m² → acres (1 acre ≈ 4046.86 m²)
    val earthRadius = 6_371_000.0
    val areaM2 = abs(area) / 2.0 * earthRadius * earthRadius
    return areaM2 / 4_046.86
}
