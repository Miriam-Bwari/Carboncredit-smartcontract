package dev.korryr.shambaguard.ui.features.farmer.presentation

data class FarmerProfileUiState(
    val farmerName:     String  = "Ezekiel Mutua",
    val farmerId:       String  = "84739201",
    val phone:          String  = "+254 722 000 111",
    val isVerified:     Boolean = true,

    // Preferences
    val selectedLanguage:       String  = "ENG",  // "ENG" or "SWA"
    val pushNotificationsOn:    Boolean = true,
    val droughtAlertsOn:        Boolean = true,

    // Security
    val biometricEnabled:       Boolean = false,

    // App version
    val appVersion:             String  = "v2.4.1",

    val isLoading:              Boolean = false,
)
