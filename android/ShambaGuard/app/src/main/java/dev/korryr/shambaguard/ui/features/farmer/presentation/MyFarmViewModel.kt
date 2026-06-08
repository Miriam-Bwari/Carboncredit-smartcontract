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
    private val sessionManager: SessionManager
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
                        activeCrop = firstFarm.cropType
                    ) 
                }
                
                val adviceResult = farmRepository.getAdvice(farmId).getOrNull()
                if (adviceResult != null) {
                    _uiState.update { 
                        it.copy(
                            ndviScore = adviceResult.ndviScore,
                            ndviStatus = adviceResult.farmHealth,
                            vegCoverPercent = (adviceResult.ndviScore * 100).coerceIn(0f, 100f),
                            vegCoverStatus = adviceResult.farmHealth
                        ) 
                    }
                }
                
                val carbonResult = farmRepository.getCarbonHistory(farmId).getOrNull()
                if (carbonResult != null) {
                    _uiState.update {
                        it.copy(
                            soilCarbonPercent = (carbonResult.totalCarbonKg / 1000f).coerceIn(0f, 100f),
                            soilCarbonMax = 100f,
                            soilCarbonChange = "Total: ${carbonResult.totalCarbonKg} kg"
                        )
                    }
                }
            }
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
