package dev.korryr.shambaguard.ui.features.auth.presentation

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// Step 2 ViewModel — owns polygon points, map type, and geocoding.
@HiltViewModel
class FarmBoundaryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmBoundaryUiState())
    val uiState: StateFlow<FarmBoundaryUiState> = _uiState.asStateFlow()

    fun onMapTapped(point: LatLng) {
        _uiState.update { it.copy(points = it.points + point) }
    }

    fun onUndoLastPoint() {
        _uiState.update { state ->
            if (state.points.isEmpty()) {
                state
            } else {
                state.copy(points = state.points.dropLast(1))
            }
        }
    }

    fun onToggleMapType() {
        _uiState.update { state ->
            val next = if (state.mapType == GoogleMap.MAP_TYPE_HYBRID) {
                GoogleMap.MAP_TYPE_NORMAL
            } else {
                GoogleMap.MAP_TYPE_HYBRID
            }
            state.copy(mapType = next)
        }
    }

    // Returns true when there are enough points to form a valid polygon
    fun canSave(): Boolean = _uiState.value.points.size >= 3

    fun getPolygonJson(): String {
        val points = _uiState.value.points
        val jsonArray = org.json.JSONArray()
        points.forEach { point ->
            val jsonObject = org.json.JSONObject()
            jsonObject.put("lat", point.latitude)
            jsonObject.put("lng", point.longitude)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    fun onCameraMoved(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val regionName = address.subAdminArea ?: address.locality ?: address.adminArea ?: "Unknown Area"
                    _uiState.update { it.copy(currentRegionName = regionName) }
                } else {
                    _uiState.update { it.copy(currentRegionName = "Offline Area") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentRegionName = "Offline Area") }
            }
        }
    }
}
