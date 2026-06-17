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
class MyFarmViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyFarmUiState())
    val uiState: StateFlow<MyFarmUiState> = _uiState.asStateFlow()

    init {
        loadFarmData()
    }

    private fun loadFarmData() {
        viewModelScope.launch {
            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            val farmsResult = farmRepository.getFarmerFarms(farmerId).getOrNull()
            val firstFarm = farmsResult?.firstOrNull()

            if (firstFarm != null) {
                val farmId = firstFarm.id

                // Format acres to 1 decimal place to look cleaner
                val acres = Math.round((firstFarm.areaHectares * 2.47105) * 10.0) / 10.0

                _uiState.update {
                    it.copy(
                        plotName = firstFarm.name,
                        farmAcres = acres.toFloat(),
                        activeCrop = firstFarm.cropType,
                    )
                }

                // Fetch local FarmEntity to get the polygon JSON
                viewModelScope.launch {
                    farmRepository.getFarm(farmId).collect { farmEntity ->
                        if (farmEntity != null && farmEntity.polygonJson.isNotBlank()) {
                            try {
                                val coordsList = dev.korryr.shambaguard.core.util.PolygonJsonHelper.parseToLatLng(farmEntity.polygonJson)
                                _uiState.update { it.copy(polygonCoords = coordsList) }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                // Sync the full farm details from remote API to local DB so the polygon is available!
                farmRepository.syncFarm(farmId)

                val adviceResult = farmRepository.getAdvice(farmId).getOrNull()
                if (adviceResult != null) {
                    _uiState.update {
                        it.copy(
                            ndviScore = adviceResult.ndviScore,
                            ndviStatus = adviceResult.farmHealth,
                            vegCoverPercent = (adviceResult.ndviScore * 100).coerceIn(0f, 100f),
                            vegCoverStatus = adviceResult.farmHealth,
                        )
                    }
                }

                val carbonResult = farmRepository.getCarbonHistory(farmId).getOrNull()
                if (carbonResult != null) {
                    _uiState.update {
                        it.copy(
                            soilCarbonPercent = (carbonResult.totalCarbonKg / 1000f).coerceIn(0f, 100f),
                            soilCarbonMax = 100f,
                            soilCarbonChange = "Total: ${carbonResult.totalCarbonKg} kg",
                        )
                    }
                }

                loadPractices(farmId)
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadPractices(farmId: String) {
        val practicesResult = farmRepository.getPractices(farmId).getOrNull()
        if (practicesResult != null) {
            val mapped = practicesResult.map { log ->
                val hasImage = log.tillageMethod.contains("compost", ignoreCase = true) || log.tillageMethod.contains("minimum", ignoreCase = true)
                // parse date "2026-06-08T05:26:40" to "2026-06-08"
                val dateStr = try {
                    val idx = log.createdAt.indexOf('T')
                    if (idx != -1) log.createdAt.substring(0, idx) else log.createdAt
                } catch (e: Exception) {
                    log.createdAt
                }

                FarmPractice(
                    title = log.tillageMethod.ifBlank { "Tillage Update" },
                    date = dateStr,
                    carbonBadge = "+0.1t CO2e", // Estimated impact
                    hasImage = hasImage,
                )
            }
            _uiState.update { it.copy(practices = mapped) }
        }
    }

    fun onShowAddPracticeDialog(show: Boolean) {
        _uiState.update { it.copy(showAddPracticeDialog = show) }
    }

    fun submitPractice(tillageMethod: String, treeCountStr: String, irrigationSource: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingPractice = true) }
            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch
            val farmsResult = farmRepository.getFarmerFarms(farmerId).getOrNull()
            val firstFarm = farmsResult?.firstOrNull() ?: return@launch

            val dto = dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto(
                cropType = firstFarm.cropType,
                tillageMethod = tillageMethod,
                treeCount = treeCountStr.toIntOrNull() ?: 0,
                irrigationSource = irrigationSource,
            )

            val result = farmRepository.addPractice(firstFarm.id, dto)
            if (result.isSuccess) {
                loadPractices(firstFarm.id)
            }

            _uiState.update {
                it.copy(
                    isSubmittingPractice = false,
                    showAddPracticeDialog = false,
                )
            }
        }
    }
}
