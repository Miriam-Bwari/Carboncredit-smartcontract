package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Step 2 ViewModel — owns polygon points and map type. No Context held.
@HiltViewModel
class FarmBoundaryViewModel @Inject constructor() : ViewModel() {

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
            val next = if (state.mapType == GoogleMap.MAP_TYPE_SATELLITE) {
                GoogleMap.MAP_TYPE_NORMAL
            } else {
                GoogleMap.MAP_TYPE_SATELLITE
            }
            state.copy(mapType = next)
        }
    }

    // Returns true when there are enough points to form a valid polygon
    fun canSave(): Boolean = _uiState.value.points.size >= 3
}
