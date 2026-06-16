package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.messaging.FirebaseMessaging
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FcmTokenRequestDto
// ---------------------------------------------------------------------------
// LoginViewModel.kt
// Handles login for both Farmers and Agents using phone + password.
// Role is selected on this screen (via toggle) so the correct
// backend endpoint is called.
// ---------------------------------------------------------------------------

data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val role: UserRole = UserRole.Farmer, // which endpoint to call
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successRole: UserRole? = null, // non-null on successful login
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val farmApi: FarmApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Field updates

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }

    fun onPasswordChanged(password: String) {
        if (password.length <= 4) {
            _uiState.update { it.copy(password = password, error = null) }
        }
    }

    fun onRoleToggled(role: UserRole) {
        _uiState.update { it.copy(role = role, error = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    // Login

    fun login(
        errorFieldsRequired: String,
    ) {
        val state = _uiState.value

        if (state.phone.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = errorFieldsRequired) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.login(
                phone = state.phone.replace(" ", ""),
                password = state.password,
                role = state.role,
            ).fold(
                onSuccess = { role ->
                    // Fetch FCM token and sync to backend if Farmer
                    if (role == UserRole.Farmer) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                viewModelScope.launch {
                                    try {
                                        val userId = sessionManager.userIdFlow.firstOrNull()
                                        if (userId != null) {
                                            farmApi.updateFcmToken(FcmTokenRequestDto(farmer_id = userId, fcm_token = token))
                                            Timber.d("Successfully synced FCM token on login.")
                                        }
                                    } catch (e: Exception) {
                                        Timber.e(e, "Failed to sync FCM token on login.")
                                    }
                                }
                            }
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, successRole = role) }
                },
                onFailure = { e ->
                    val isAuthError = e.message?.contains("401", ignoreCase = true) == true
                    val msg = if (isAuthError && state.role == UserRole.Agent) {
                        "Invalid credentials or account awaiting admin approval."
                    } else if (isAuthError) {
                        "Invalid phone number or password."
                    } else {
                        e.message ?: "Login failed. Check your details and try again."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = msg,
                        )
                    }
                },
            )
        }
    }

    // Dev Bypass
    fun devBypass(role: UserRole) {
        viewModelScope.launch {
            // Save a dummy session so the NavGraph displays the correct bottom tabs
            // Note: Since this is a fake ID, backend API calls will return empty/404.
            sessionManager.saveSession(
                token = "dev_bypass_token",
                role = role.name,
                userId = "dev_bypass_user_id",
            )
            _uiState.update { it.copy(successRole = role) }
        }
    }

    /** Call after navigation to prevent re-triggering on recomposition. */
    fun onNavigationConsumed() {
        _uiState.update { it.copy(successRole = null) }
    }
}
