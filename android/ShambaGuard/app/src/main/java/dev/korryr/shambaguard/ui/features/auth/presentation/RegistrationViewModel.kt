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

// ---------------------------------------------------------------------------
// RegistrationViewModel.kt
// Owns all state and validation logic for the account-creation screen.
// No Android Context held. No business logic in Composables.
// ---------------------------------------------------------------------------
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountRegistrationUiState())
    val uiState: StateFlow<AccountRegistrationUiState> = _uiState.asStateFlow()

    // Field updates

    fun onFullNameChanged(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onCountyChanged(value: String) {
        _uiState.update { it.copy(county = value, countyError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, confirmPasswordError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    // Validation

    /**
     * Validates all fields inline and shows per-field errors.
     * Returns true only if every field is valid so the caller can proceed.
     */
    fun validate(
        errorNameEmpty: String,
        errorPhoneInvalid: String,
        errorCountyEmpty: String,
        errorPasswordShort: String,
        errorPasswordMismatch: String,
    ): Boolean {
        val state = _uiState.value
        var isValid = true

        val nameError = if (state.fullName.isBlank()) {
            isValid = false; errorNameEmpty
        } else null

        val normPhone = state.phone.replace(" ", "")
        val phoneError = if (!normPhone.matches(Regex("^\\+2547\\d{8}$"))) {
            isValid = false; errorPhoneInvalid
        } else null

        val countyError = if (state.county.isBlank()) {
            isValid = false; errorCountyEmpty
        } else null

        val passwordError = if (state.password.length < 8) {
            isValid = false; errorPasswordShort
        } else null

        val confirmError = if (state.password != state.confirmPassword) {
            isValid = false; errorPasswordMismatch
        } else null

        _uiState.update {
            it.copy(
                fullNameError        = nameError,
                phoneError           = phoneError,
                countyError          = countyError,
                passwordError        = passwordError,
                confirmPasswordError = confirmError,
            )
        }

        return isValid
    }

    // Registration

    /**
     * Calls the correct backend endpoint based on [role].
     * On success, updates [AccountRegistrationUiState.successId] — the NavGraph
     * observes this and navigates accordingly.
     */
    fun register(
        role: AppUserRole,
        errorNameEmpty: String,
        errorPhoneInvalid: String,
        errorCountyEmpty: String,
        errorPasswordShort: String,
        errorPasswordMismatch: String,
    ) {
        if (!validate(
                errorNameEmpty,
                errorPhoneInvalid,
                errorCountyEmpty,
                errorPasswordShort,
                errorPasswordMismatch,
            )
        ) return

        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, networkError = null) }

        viewModelScope.launch {
            val result = when (role) {
                AppUserRole.Farmer -> authRepository.registerFarmer(
                    fullName = state.fullName,
                    phone    = state.phone.replace(" ", ""),
                    county   = state.county,
                    password = state.password,
                )
                AppUserRole.Agent -> authRepository.registerAgent(
                    fullName = state.fullName,
                    phone    = state.phone.replace(" ", ""),
                    county   = state.county,
                    password = state.password,
                )
            }

            result.fold(
                onSuccess = { id ->
                    _uiState.update { it.copy(isLoading = false, successId = id) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            networkError = e.message ?: "Registration failed. Please try again.",
                        )
                    }
                },
            )
        }
    }

    /** Call after navigation to prevent re-triggering on recomposition. */
    fun onNavigationConsumed() {
        _uiState.update { it.copy(successId = null) }
    }
}
