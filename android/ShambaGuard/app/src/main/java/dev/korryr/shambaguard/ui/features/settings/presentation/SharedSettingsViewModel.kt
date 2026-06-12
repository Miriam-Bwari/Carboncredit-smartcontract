package dev.korryr.shambaguard.ui.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.AppThemeMode
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.core.datastore.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedSettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedSettingsUiState())
    val uiState: StateFlow<SharedSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.appThemeFlow.collect { theme ->
                _uiState.update { it.copy(themeMode = theme) }
            }
        }
    }

    fun setTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggingOut = false, logoutSuccess = true) }
        }
    }
}
