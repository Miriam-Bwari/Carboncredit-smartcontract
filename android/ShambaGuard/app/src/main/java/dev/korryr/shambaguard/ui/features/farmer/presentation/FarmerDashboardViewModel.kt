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
class FarmerDashboardViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerDashboardUiState(isLoading = true))
    val uiState: StateFlow<FarmerDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            // 1. Get Farmer Details
            val farmerResult = farmRepository.getFarmer(farmerId).getOrNull()
            if (farmerResult != null) {
                _uiState.update { it.copy(farmerName = farmerResult.fullName.split(" ").firstOrNull() ?: "Farmer") }
            }

            // 2. Get Farms & Pick First
            val farmsResult = farmRepository.getFarmerFarms(farmerId).getOrNull()
            val firstFarm = farmsResult?.firstOrNull()

            if (firstFarm != null) {
                _uiState.update {
                    it.copy(
                        hasFarm = true,
                        farmName = firstFarm.name,
                        farmRegion = farmerResult?.county ?: "Unknown Region",
                    )
                }

                val farmId = firstFarm.id

                // 3. Get Carbon History
                val carbonResult = farmRepository.getCarbonHistory(farmId).getOrNull()
                val recentActivities = mutableListOf<ActivityItem>()

                if (carbonResult != null) {
                    _uiState.update { it.copy(carbonTonnes = carbonResult.totalCarbonKg / 1000f) }

                    // Add carbon verifications to activity timeline
                    carbonResult.records.filter { it.verified }.take(2).forEach { record ->
                        recentActivities.add(
                            ActivityItem(
                                title = "Carbon Verification",
                                description = "${record.credits} credits verified for agroforestry practices.",
                                timeAgo = record.date.take(10), // Simplistic date formatting
                                type = ActivityType.CARBON,
                            ),
                        )
                    }

                    // Update NDVI if available from latest scan
                    val latestScan = carbonResult.records.firstOrNull()
                    if (latestScan != null) {
                        _uiState.update { it.copy(ndviScore = latestScan.ndvi) }
                    }
                }

                // 4. Get Weather
                val weatherResult = farmRepository.getWeather(farmId).getOrNull()
                if (weatherResult != null) {
                    _uiState.update {
                        it.copy(
                            rainfallMm = weatherResult.rainfallMm.toInt(),
                            rainfallDelta = weatherResult.rainfallDeltaPercent.toInt(),
                        )
                    }
                }

                // 5. Get Advice (for drought risk)
                val adviceResult = farmRepository.getAdvice(farmId).getOrNull()
                if (adviceResult != null) {
                    val risk = when (adviceResult.farmHealth) {
                        "LOW" -> DroughtRisk.CRITICAL
                        "MEDIUM" -> DroughtRisk.MODERATE
                        else -> DroughtRisk.LOW
                    }
                    _uiState.update { it.copy(droughtRisk = risk) }
                }

                // 6. Get Policy
                val policyResult = farmRepository.getPolicy(farmerId).getOrNull()
                if (policyResult != null) {
                    _uiState.update {
                        it.copy(
                            policyActive = policyResult.isActive,
                            policyExpiry = policyResult.expiryDate ?: "None",
                        )
                    }
                }

                if (recentActivities.isNotEmpty()) {
                    _uiState.update { it.copy(recentActivity = recentActivities) }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
