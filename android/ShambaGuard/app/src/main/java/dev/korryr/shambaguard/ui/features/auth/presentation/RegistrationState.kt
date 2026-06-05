package dev.korryr.shambaguard.ui.features.auth.presentation

// ---------------------------------------------------------------------------
// RegistrationState.kt
// Immutable UI state for the account-creation registration screen.
// This screen collects: full name, phone number, county, password, confirm password.
// Farm setup (polygon + practices) is a separate post-registration flow for Farmers only.
// ---------------------------------------------------------------------------

data class AccountRegistrationUiState(

    // Field values
    val fullName:        String = "",
    val phone:           String = "",
    val county:          String = "",
    val password:        String = "",
    val confirmPassword: String = "",

    // Per-field validation errors (null = no error shown)
    val fullNameError:        String? = null,
    val phoneError:           String? = null,
    val countyError:          String? = null,
    val passwordError:        String? = null,
    val confirmPasswordError: String? = null,

    // UI visibility toggles
    val passwordVisible:        Boolean = false,
    val confirmPasswordVisible: Boolean = false,

    // Async state
    val isLoading:    Boolean = false,
    val successId:    String? = null,   // non-null on successful registration (farmer_id or agent_id)
    val networkError: String? = null,   // non-null on network/backend error
)

/** Returns true only when all required fields pass basic inline validation rules. */
fun AccountRegistrationUiState.isFormValid(): Boolean =
    fullName.isNotBlank() &&
    phone.matches(Regex("^\\+2547\\d{8}$")) &&
    county.isNotBlank() &&
    password.length >= 8 &&
    confirmPassword == password
