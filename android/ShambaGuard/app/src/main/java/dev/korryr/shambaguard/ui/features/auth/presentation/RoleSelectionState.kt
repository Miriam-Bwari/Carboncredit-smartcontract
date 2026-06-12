package dev.korryr.shambaguard.ui.features.auth.presentation

// ---------------------------------------------------------------------------
// RoleSelectionState.kt
// Holds the UI state for the "Choose Your Role" screen.
// ---------------------------------------------------------------------------

// App-level user roles a person can self-identify as during onboarding.
enum class AppUserRole {
    Farmer,
    Agent,
}

data class RoleSelectionUiState(
    val selectedRole: AppUserRole? = null,
)
