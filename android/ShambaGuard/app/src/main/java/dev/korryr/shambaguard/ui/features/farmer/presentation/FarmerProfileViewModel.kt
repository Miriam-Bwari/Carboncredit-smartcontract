package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Week 5: replace stubs with repository.getFarmerProfile(uid)
@HiltViewModel
class FarmerProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerProfileUiState())
    val uiState: StateFlow<FarmerProfileUiState> = _uiState.asStateFlow()

    fun onLanguageSelected(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
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
}
