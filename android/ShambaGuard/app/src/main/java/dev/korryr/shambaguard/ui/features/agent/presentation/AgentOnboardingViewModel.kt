package dev.korryr.shambaguard.ui.features.agent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.UserDao
import dev.korryr.shambaguard.data.local.entity.FarmEntity
import dev.korryr.shambaguard.data.local.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class AgentOnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val userDao: UserDao,
    private val farmDao: FarmDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentOnboardingUiState())
    val uiState: StateFlow<AgentOnboardingUiState> = _uiState.asStateFlow()

    fun updateDetails(name: String, nationalId: String, phone: String) {
        _uiState.update { it.copy(name = name, nationalId = nationalId, phone = phone) }
    }

    fun updatePolygon(points: List<LatLng>) {
        _uiState.update { it.copy(polygonPoints = points) }
    }

    fun onUndoLastPoint() {
        _uiState.update { state ->
            if (state.polygonPoints.isEmpty()) {
                state
            } else {
                state.copy(polygonPoints = state.polygonPoints.dropLast(1))
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

    fun onCameraMoved(point: LatLng) {
        viewModelScope.launch {
            val name = getRegionName(point)
            _uiState.update { it.copy(currentRegionName = name) }
        }
    }

    private suspend fun getRegionName(point: LatLng): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val subAdmin = address.subAdminArea
                val admin = address.adminArea
                val locality = address.locality

                when {
                    !subAdmin.isNullOrBlank() -> subAdmin
                    !locality.isNullOrBlank() -> locality
                    !admin.isNullOrBlank() -> admin
                    else -> "Unknown Region"
                }
            } else {
                "Unknown Region"
            }
        } catch (e: Exception) {
            "Offline Area"
        }
    }

    fun updatePractices(cropType: String, tillageMethod: String, treeCount: Int, irrigationSource: String) {
        _uiState.update {
            it.copy(
                cropType = cropType,
                tillageMethod = tillageMethod,
                treeCount = treeCount,
                irrigationSource = irrigationSource,
            )
        }
    }

    fun saveFarmerOffline() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val agentId = sessionManager.userIdFlow.firstOrNull()
                    ?: throw Exception("Agent not logged in")

                val state = _uiState.value
                val farmerId = UUID.randomUUID().toString()
                val farmId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()

                val user = UserEntity(
                    userId = farmerId,
                    phone = state.phone,
                    role = "FARMER",
                    name = state.name,
                    nationalId = state.nationalId,
                    region = "Pending Sync", // Could be reverse geocoded later
                    isApproved = true,
                    createdAt = now,
                )

                // Simple JSON array structure for geojson placeholder
                val polygonJson = "[" + state.polygonPoints.joinToString(",") {
                    "[${it.longitude},${it.latitude}]"
                } + "]"

                val farm = FarmEntity(
                    farmId = farmId,
                    farmerId = farmerId,
                    agentId = agentId,
                    polygonJson = polygonJson,
                    areaHectares = calculateEstimatedArea(state.polygonPoints),
                    region = "Pending Sync",
                    cropType = state.cropType.ifBlank { "Unknown" },
                    tillageMethod = state.tillageMethod.ifBlank { "Unknown" },
                    treeCount = state.treeCount,
                    carbonStatus = "PENDING",
                    syncStatus = "PENDING_SYNC",
                    lastSyncedAt = null,
                    createdAt = now,
                )

                userDao.insertUser(user)
                farmDao.insertFarm(farm)

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun consumeNavEvent() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    // Dummy area calculation for now, just to satisfy the Entity requirement
    private fun calculateEstimatedArea(points: List<LatLng>): Double {
        if (points.size < 3) return 0.0
        // Placeholder area calculation. In production, use SphericalUtil.computeArea
        return 1.5
    }
}
