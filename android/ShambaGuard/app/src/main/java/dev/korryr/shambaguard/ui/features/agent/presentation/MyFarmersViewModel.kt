package dev.korryr.shambaguard.ui.features.agent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.PolicyDao
import dev.korryr.shambaguard.data.local.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFarmersViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val farmDao: FarmDao,
    private val userDao: UserDao,
    private val policyDao: PolicyDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyFarmersUiState(isLoading = true))
    val uiState: StateFlow<MyFarmersUiState> = _uiState.asStateFlow()

    init {
        loadFarmers()
    }

    fun loadFarmers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val agentId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

                val farms = farmDao.getFarmsByAgentSync(agentId)
                val farmerList = mutableListOf<FarmerListItem>()

                for (farm in farms) {
                    val user = userDao.getUserByIdSync(farm.farmerId)
                    val policy = policyDao.getPolicyByFarmSync(farm.farmId)

                    farmerList.add(
                        FarmerListItem(
                            farmerId = farm.farmerId,
                            farmId = farm.farmId,
                            name = user?.name ?: "Unknown Farmer",
                            policyStatus = policy?.status ?: "NO POLICY",
                            syncStatus = farm.syncStatus,
                            createdAt = farm.createdAt,
                            cropType = farm.cropType,
                            areaHectares = farm.areaHectares,
                        ),
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        farmers = farmerList.sortedByDescending { f -> f.createdAt },
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load farmers.",
                    )
                }
            }
        }
    }
}
