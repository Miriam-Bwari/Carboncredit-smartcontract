package dev.korryr.shambaguard.ui.features.farmer.presentation

data class FarmerProfileUiState(
    val farmerName: String = "",
    val farmerId: String = "",
    val phone: String = "",
    val isVerified: Boolean = true,

    // Preferences
    val selectedLanguage: String = "ENG", // "ENG" or "SWA"
    val pushNotificationsOn: Boolean = true,
    val droughtAlertsOn: Boolean = true,

    // Security
    val biometricEnabled: Boolean = false,

    // App version
    val appVersion: String = "v1.0.0",

    val themeMode: dev.korryr.shambaguard.core.datastore.AppThemeMode = dev.korryr.shambaguard.core.datastore.AppThemeMode.SYSTEM,
    val isLoading: Boolean = false,
)
