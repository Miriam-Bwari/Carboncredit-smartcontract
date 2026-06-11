package dev.korryr.shambaguard.ui.features.agent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentSettingsUiState(
    val isLoggingOut: Boolean = false,
    val logoutSuccess: Boolean = false,
)

@HiltViewModel
class AgentSettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentSettingsUiState())
    val uiState: StateFlow<AgentSettingsUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggingOut = false, logoutSuccess = true) }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(logoutSuccess = false) }
    }
}
