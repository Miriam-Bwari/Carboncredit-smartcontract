package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val phone: String = "",
    val otp: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successRole: UserRole? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }

    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otp = otp, error = null) }
    }

    fun sendOtp() {
        val phone = _uiState.value.phone
        if (phone.isBlank()) {
            _uiState.update { it.copy(error = "Phone number is required") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.sendOtp(phone).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to send OTP") }
                }
            )
        }
    }

    fun verifyOtp() {
        val state = _uiState.value
        if (state.otp.isBlank()) {
            _uiState.update { it.copy(error = "OTP is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.verifyOtp(state.phone, state.otp).fold(
                onSuccess = { role ->
                    _uiState.update { it.copy(isLoading = false, successRole = role) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to verify OTP") }
                }
            )
        }
    }
}
