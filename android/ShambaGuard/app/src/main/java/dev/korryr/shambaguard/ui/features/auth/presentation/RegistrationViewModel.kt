package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ---------------------------------------------------------------------------
// RegistrationViewModel.kt
// Owns all state and validation logic for the multi-step registration flow.
// No Android Context held. No business logic in Composables.
// ---------------------------------------------------------------------------
@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationStep1UiState())
    val uiState: StateFlow<RegistrationStep1UiState> = _uiState.asStateFlow()

    // ── Field updates ────────────────────────────────────────────────────────

    fun onFullNameChanged(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onNationalIdChanged(value: String) {
        // Accept digits only, max 8 chars
        val filtered = value.filter { ch -> ch.isDigit() }.take(8)
        _uiState.update { it.copy(nationalId = filtered, nationalIdError = null) }
    }

    fun onMpesaPhoneChanged(value: String) {
        _uiState.update { it.copy(mpesaPhone = value, mpesaPhoneError = null) }
    }

    // ── Validation ───────────────────────────────────────────────────────────

    /**
     * Runs inline validation on all Step 1 fields.
     * Returns true if every field is valid so the caller can navigate forward.
     */
    fun validateStep1(
        errorFullNameEmpty: String,
        errorNationalIdInvalid: String,
        errorPhoneInvalid: String,
    ): Boolean {
        val state = _uiState.value
        var isValid = true

        val nameError = if (state.fullName.isBlank()) {
            isValid = false
            errorFullNameEmpty
        } else null

        val idError = if (!state.nationalId.matches(Regex("\\d{8}"))) {
            isValid = false
            errorNationalIdInvalid
        } else null

        // Normalise: strip spaces before matching
        val normPhone = state.mpesaPhone.replace(" ", "")
        val phoneError = if (!normPhone.matches(Regex("^\\+2547\\d{8}$"))) {
            isValid = false
            errorPhoneInvalid
        } else null

        _uiState.update {
            it.copy(
                fullNameError = nameError,
                nationalIdError = idError,
                mpesaPhoneError = phoneError,
            )
        }

        return isValid
    }
}
