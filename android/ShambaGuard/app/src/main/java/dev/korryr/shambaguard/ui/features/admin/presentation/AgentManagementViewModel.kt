package dev.korryr.shambaguard.ui.features.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.ui.features.admin.domain.model.AgentModel
import dev.korryr.shambaguard.ui.features.admin.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentManagementUiState(
    val isLoading: Boolean = false,
    val pendingAgents: List<AgentModel> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AgentManagementViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentManagementUiState(isLoading = true))
    val uiState: StateFlow<AgentManagementUiState> = _uiState.asStateFlow()

    init {
        loadPendingAgents()
    }

    fun loadPendingAgents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getPendingAgents().collect { result ->
                result.fold(
                    onSuccess = { agents ->
                        _uiState.update { it.copy(isLoading = false, pendingAgents = agents) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    }
                )
            }
        }
    }

    fun approveAgent(agentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.approveAgent(agentId)
            result.fold(
                onSuccess = { approvedAgent ->
                    // Remove from pending list
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            pendingAgents = state.pendingAgents.filter { it.id != approvedAgent.id }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
