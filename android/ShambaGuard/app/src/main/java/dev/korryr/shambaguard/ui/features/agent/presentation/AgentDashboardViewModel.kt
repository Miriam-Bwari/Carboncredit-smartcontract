package dev.korryr.shambaguard.ui.features.agent.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.agent.domain.repository.AgentRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Week 5: replace stub with repository.getAgentDashboard(agentId) + FarmSyncWorker
@HiltViewModel
class AgentDashboardViewModel @Inject constructor(
    private val agentRepository: AgentRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentDashboardUiState(isLoading = true))
    val uiState: StateFlow<AgentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val agentId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            val dashboardResult = agentRepository.getDashboardStats(agentId).getOrNull()

            if (dashboardResult != null) {
                val mappedRegistrations = dashboardResult.recentRegistrations.map { dto ->
                    RecentRegistration(
                        id = dto.id,
                        name = dto.name,
                        county = dto.county,
                        status = try {
                            RegistrationStatus.valueOf(dto.status)
                        } catch (e: Exception) {
                            RegistrationStatus.ACTIVE
                        },
                        syncText = dto.syncText,
                    )
                }

                _uiState.update {
                    it.copy(
                        farmersRegistered = dashboardResult.farmersRegistered,
                        pendingSyncs = dashboardResult.pendingSyncs,
                        newThisMonth = dashboardResult.newThisMonth,
                        recentRegistrations = mappedRegistrations,
                    )
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSyncNow() {
        // Week 5: enqueue FarmSyncWorker via WorkManager
    }

    fun onFilterToggled() {
        // Week 5: show filter bottom sheet
    }
}
