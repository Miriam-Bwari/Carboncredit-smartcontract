package dev.korryr.shambaguard.ui.features.auth.presentation

// ---------------------------------------------------------------------------
// RegistrationState.kt
// Immutable UI state for the multi-step registration flow.
// Step 1 — Personal Details: full name, national ID, M-Pesa phone.
// ---------------------------------------------------------------------------

data class RegistrationStep1UiState(
    // Field values
    val fullName: String = "",
    val nationalId: String = "",
    val mpesaPhone: String = "",

    // Per-field validation errors (null = no error shown yet)
    val fullNameError: String? = null,
    val nationalIdError: String? = null,
    val mpesaPhoneError: String? = null,

    // True while a network/backend call is in-flight
    val isLoading: Boolean = false,
)

/** Returns true only when all three fields pass inline validation rules. */
fun RegistrationStep1UiState.isStep1Valid(): Boolean =
    fullName.isNotBlank() &&
    nationalId.matches(Regex("\\d{8}")) &&
    mpesaPhone.matches(Regex("^\\+2547\\d{8}$"))
