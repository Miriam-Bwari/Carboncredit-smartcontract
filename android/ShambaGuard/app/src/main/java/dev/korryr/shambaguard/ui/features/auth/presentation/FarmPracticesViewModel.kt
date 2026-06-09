package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmRegisterRequestDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Step 3 ViewModel — all selection and counter logic lives here
@HiltViewModel
class FarmPracticesViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmPracticesUiState())
    val uiState: StateFlow<FarmPracticesUiState> = _uiState.asStateFlow()

    private var polygonJson: String = ""

    fun setPolygonJson(json: String) {
        polygonJson = json
    }

    fun onCropToggled(crop: String) {
        _uiState.update { state ->
            val updated = if (crop in state.selectedCrops) {
                state.selectedCrops - crop
            } else {
                state.selectedCrops + crop
            }
            state.copy(selectedCrops = updated)
        }
    }

    fun onMethodSelected(method: String) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun onWaterSelected(water: String) {
        _uiState.update { it.copy(selectedWater = water) }
    }

    fun onIncrementTrees() {
        _uiState.update { it.copy(treeCount = it.treeCount + 1) }
    }

    fun onDecrementTrees() {
        _uiState.update { state ->
            state.copy(treeCount = maxOf(0, state.treeCount - 1))
        }
    }

    fun canComplete(): Boolean = _uiState.value.canComplete()

    fun onNavigationConsumed() {
        _uiState.update { it.copy(submissionSuccess = false) }
    }

    fun submitFarmDetails() {
        if (!canComplete() || polygonJson.isEmpty()) return

        _uiState.update { it.copy(isSubmitting = true, submissionError = null) }

        viewModelScope.launch {
            val farmerId = sessionManager.userIdFlow.firstOrNull()
            if (farmerId == null) {
                _uiState.update { it.copy(isSubmitting = false, submissionError = "User not logged in.") }
                return@launch
            }

            // Parse polygonJson to GeoJsonPolygonDto
            val coordsList = mutableListOf<List<Double>>()
            try {
                val jsonArray = org.json.JSONArray(polygonJson)
                for (i in 0 until jsonArray.length()) {
                    val point = jsonArray.getJSONObject(i)
                    // GeoJSON expects [longitude, latitude]
                    coordsList.add(listOf(point.getDouble("lng"), point.getDouble("lat")))
                }
                if (coordsList.isNotEmpty() && coordsList.first() != coordsList.last()) {
                    coordsList.add(coordsList.first())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, submissionError = "Failed to parse boundary.") }
                return@launch
            }

            val geoJsonPolygon = dev.korryr.shambaguard.core.network.GeoJsonPolygonDto(
                coordinates = listOf(coordsList),
            )

            // Step 1: Push Farm
            val farmId = java.util.UUID.randomUUID().toString()
            val state = _uiState.value
            
            // Note: Since this is the frontend logic for a Farmer, we directly call the API.
            // But wait, the farmApi.registerFarm is not directly available here, we need it exposed in FarmRepository.
            // Let's use the local Room database to save it, then trigger sync, OR update FarmRepository.
            // Wait, FarmRepositoryImpl already has `pushPendingFarm`! But it expects a FarmEntity.
            // We can just construct a FarmEntity and call pushPendingFarm!
            
            val farmEntity = dev.korryr.shambaguard.data.local.entity.FarmEntity(
                farmId = farmId,
                farmerId = farmerId,
                agentId = "",
                polygonJson = polygonJson,
                areaHectares = 2.0, // Default for now
                region = "Local",
                cropType = state.selectedCrops.joinToString(", "),
                tillageMethod = state.selectedMethod ?: "",
                treeCount = state.treeCount,
                carbonStatus = "PENDING",
                syncStatus = "PENDING",
                lastSyncedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
            )
            
            val pushResult = farmRepository.pushPendingFarm(farmEntity)

            if (pushResult.isFailure) {
                _uiState.update { it.copy(isSubmitting = false, submissionError = "Failed to save farm to server.") }
                return@launch
            }

            // Step 2: Push Practices
            val practiceDto = PracticeLogDto(
                cropType = state.selectedCrops.joinToString(", "),
                tillageMethod = state.selectedMethod ?: "",
                treeCount = state.treeCount,
                irrigationSource = state.selectedWater ?: ""
            )

            farmRepository.addPractice(farmId, practiceDto)

            _uiState.update { it.copy(isSubmitting = false, submissionSuccess = true) }
        }
    }
}
