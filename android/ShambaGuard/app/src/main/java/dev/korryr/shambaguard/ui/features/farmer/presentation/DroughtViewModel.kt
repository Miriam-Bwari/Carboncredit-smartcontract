package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DroughtViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _warningState = MutableStateFlow(EarlyWarningUiState(isLoading = true))
    val warningState: StateFlow<EarlyWarningUiState> = _warningState.asStateFlow()

    private val _coverageState = MutableStateFlow(CoverageStatusUiState(isLoading = true))
    val coverageState: StateFlow<CoverageStatusUiState> = _coverageState.asStateFlow()

    private val _insightsState = MutableStateFlow(DroughtInsightsUiState(isLoading = true))
    val insightsState: StateFlow<DroughtInsightsUiState> = _insightsState.asStateFlow()

    init {
        loadDroughtData()
    }

    private fun loadDroughtData() {
        viewModelScope.launch {
            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            val farmsResult = farmRepository.getFarmerFarms(farmerId).getOrNull()
            val firstFarm = farmsResult?.firstOrNull()

            if (firstFarm != null) {
                val farmId = firstFarm.id

                // Get the local FarmEntity to extract the boundary polygon for the stress map
                launch {
                    farmRepository.getFarm(farmId).collect { localFarm ->
                        if (localFarm != null) {
                            try {
                                val geoJson: dev.korryr.shambaguard.core.network.GeoJsonPolygonDto = com.google.gson.Gson().fromJson(localFarm.polygonJson, dev.korryr.shambaguard.core.network.GeoJsonPolygonDto::class.java)
                                val polygonPoints = geoJson.coordinates.firstOrNull()?.map { point: List<Double> ->
                                    com.google.android.gms.maps.model.LatLng(point[1], point[0])
                                } ?: emptyList<com.google.android.gms.maps.model.LatLng>()
                                _insightsState.update { it.copy(polygonPoints = polygonPoints) }
                            } catch (e: Exception) {
                                // Ignore parsing errors
                            }
                        }
                    }
                }

                val weatherResult = farmRepository.getWeather(farmId).getOrNull()
                if (weatherResult != null) {
                    val mm = weatherResult.rainfallMm.toInt()
                    val delta = weatherResult.rainfallDeltaPercent.toInt()

                    _insightsState.update { it.copy(rainfallMm = mm, rainfallDelta = delta) }
                    _coverageState.update { it.copy(rainfallMm = weatherResult.rainfallMm) }
                }

                val adviceResult = farmRepository.getAdvice(farmId).getOrNull()
                if (adviceResult != null) {
                    val risk = when (adviceResult.farmHealth) {
                        "LOW" -> DroughtRisk.CRITICAL
                        "MEDIUM" -> DroughtRisk.MODERATE
                        else -> DroughtRisk.LOW
                    }
                    val adviceRec = adviceResult.recommendations.joinToString(" ")

                    _warningState.update {
                        it.copy(
                            currentRisk = risk,
                            farmHealthValue = adviceResult.farmHealth,
                            alertBody = adviceRec,
                            aiCropBody = adviceRec,
                            aiConfidence = adviceResult.confidenceScore,
                        )
                    }
                    _insightsState.update {
                        it.copy(
                            ndviScore = adviceResult.ndviScore,
                            ndviTrend = adviceResult.farmHealth,
                            ndviTrendSwahili = "",
                        )
                    }
                    _coverageState.update { it.copy(ndviValue = adviceResult.ndviScore) }
                }

                val policyResult = farmRepository.getPolicy(farmerId).getOrNull()
                if (policyResult != null) {
                    _warningState.update { it.copy(coverageActive = policyResult.isActive) }
                    _coverageState.update {
                        it.copy(
                            isActive = policyResult.isActive,
                            validThrough = policyResult.expiryDate ?: "None",
                        )
                    }
                }
            }

            _warningState.update { it.copy(isLoading = false) }
            _coverageState.update { it.copy(isLoading = false) }
            _insightsState.update { it.copy(isLoading = false) }
        }
    }
}
