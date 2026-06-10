package dev.korryr.shambaguard.ui.features.farmer.presentation

data class FarmPractice(
    val title: String,
    val date: String,
    val carbonBadge: String, // e.g. "+0.2t CO2e"
    val hasImage: Boolean, // true = dark compost thumbnail, false = grey placeholder
)

data class MyFarmUiState(
    // Farm map & identity
    val plotName: String = "Loading...",
    val farmAcres: Float = 0f,
    val activeCrop: String = "Loading...",
    val polygonCoords: List<com.google.android.gms.maps.model.LatLng> = emptyList(),

    // Land health
    val ndviScore: Float = 0f,
    val ndviStatus: String = "Syncing...",
    val vegCoverPercent: Float = 0f,
    val vegCoverStatus: String = "Syncing...",
    val soilCarbonPercent: Float = 0f,
    val soilCarbonMax: Float = 100f, // scale max for progress bar
    val soilCarbonChange: String = "Fetching...",

    // Practice log
    val practices: List<FarmPractice> = emptyList(),

    // Dialog state
    val showAddPracticeDialog: Boolean = false,
    val isSubmittingPractice: Boolean = false,

    val isLoading: Boolean = true,
)
