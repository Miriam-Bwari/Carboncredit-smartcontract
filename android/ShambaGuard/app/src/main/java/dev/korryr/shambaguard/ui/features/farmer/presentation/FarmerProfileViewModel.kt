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

import dev.korryr.shambaguard.core.datastore.SettingsManager
import dev.korryr.shambaguard.core.datastore.AppThemeMode

@HiltViewModel
class FarmerProfileViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerProfileUiState(isLoading = true))
    val uiState: StateFlow<FarmerProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
        
        viewModelScope.launch {
            settingsManager.appThemeFlow.collect { theme ->
                _uiState.update { it.copy(themeMode = theme) }
            }
        }

        viewModelScope.launch {
            settingsManager.appLanguageFlow.collect { lang ->
                _uiState.update { it.copy(selectedLanguage = lang) }
            }
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            val farmerResult = farmRepository.getFarmer(farmerId).getOrNull()

            if (farmerResult != null) {
                // Keep only the first 8 characters of ID if it's a UUID for display purposes
                val displayId = if (farmerResult.id.length > 8) farmerResult.id.take(8).uppercase() else farmerResult.id

                _uiState.update {
                    it.copy(
                        farmerName = farmerResult.fullName,
                        farmerId = displayId,
                        phone = farmerResult.phoneNumber,
                        isLoading = false,
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLanguageSelected(lang: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(lang)
        }
    }

    fun onPushNotificationsToggled() {
        _uiState.update { it.copy(pushNotificationsOn = !it.pushNotificationsOn) }
    }

    fun onDroughtAlertsToggled() {
        _uiState.update { it.copy(droughtAlertsOn = !it.droughtAlertsOn) }
    }

    fun onBiometricToggled() {
        _uiState.update { it.copy(biometricEnabled = !it.biometricEnabled) }
    }

    fun setTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}
