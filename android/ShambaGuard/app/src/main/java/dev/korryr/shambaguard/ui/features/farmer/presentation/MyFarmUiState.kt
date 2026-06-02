package dev.korryr.shambaguard.ui.features.farmer.presentation

data class FarmPractice(
    val title:       String,
    val date:        String,
    val carbonBadge: String,   // e.g. "+0.2t CO2e"
    val hasImage:    Boolean,  // true = dark compost thumbnail, false = grey placeholder
)

data class MyFarmUiState(
    // Farm map & identity
    val plotName:   String = "Plot Alpha",
    val farmAcres:  Float  = 5.2f,
    val activeCrop: String = "Active Maize Cultivation",

    // Land health
    val ndviScore:          Float  = 0.7f,
    val ndviStatus:         String = "Healthy",
    val vegCoverPercent:    Float  = 82f,
    val vegCoverStatus:     String = "Optimal",
    val soilCarbonPercent:  Float  = 2.4f,
    val soilCarbonMax:      Float  = 5.0f,   // scale max for progress bar
    val soilCarbonChange:   String = "+0.1% since last season",

    // Practice log
    val practices: List<FarmPractice> = listOf(
        FarmPractice("Applied Compost",  "Oct 12, 2023", "+0.2t CO2e",  true),
        FarmPractice("Cover Crop Sown",  "Sep 28, 2023", "+0.15t CO2e", false),
    ),

    val isLoading: Boolean = false,
)
