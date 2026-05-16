package dev.korryr.shambaguard.ui.features.farmer.presentation

// A single day in the 14-day forecast timeline
data class ForecastDay(
    val dayLabel:    String,  // e.g. "Day 1", "Day 7"
    val riskScore:   Float,   // 0.0 to 1.0 drought probability
    val rainfallMm:  Float,
)

data class EarlyWarningUiState(
    val farmName:        String       = "Shamba la Mary",
    val farmRegion:      String       = "Ukambani, Machakos",
    val currentRisk:     DroughtRisk  = DroughtRisk.HIGH,
    // Overall 14-day forecast drought probability
    val forecastRisk:    Float        = 0.72f,
    // 14-day forecast breakdown
    val forecast:        List<ForecastDay> = listOf(
        ForecastDay("Day 1",  0.70f, 1.2f),
        ForecastDay("Day 3",  0.74f, 0.8f),
        ForecastDay("Day 5",  0.78f, 0.0f),
        ForecastDay("Day 7",  0.80f, 0.0f),
        ForecastDay("Day 10", 0.82f, 2.0f),
        ForecastDay("Day 14", 0.75f, 3.5f),
    ),
    // Crop recommendation based on the forecast
    val recommendedCrop:        String = "Cowpeas (Kunde)",
    val recommendedCropReason:  String = "Drought-resistant. Plant within 3 days before soil dries out.",
    val recommendedCropSwahili: String = "Panda kunde — mvua kidogo inatosha.",
    val isLoading: Boolean = false,
)
