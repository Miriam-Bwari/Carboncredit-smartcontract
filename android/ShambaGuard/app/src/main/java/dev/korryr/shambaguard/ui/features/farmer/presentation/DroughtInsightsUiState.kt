package dev.korryr.shambaguard.ui.features.farmer.presentation

// DroughtInsightsScreen UI state — shown when farmer drills into "Full Analysis"
data class DroughtInsightsUiState(
    val ndviScore: Float = 0.24f,
    val ndviTrend: String = "Declining",
    val ndviTrendSwahili: String = "Inapungua",
    val rainfallMm: Int = 12,
    val rainfallDelta: Int = -65,
    // NDVI history for the simple bar visualisation (last 6 readings, newest last)
    val ndviHistory: List<Float> = listOf(0.48f, 0.42f, 0.37f, 0.31f, 0.27f, 0.24f),
    val satelliteSource: String = "Sentinel-2",
    val lastUpdated: String = "14 May 2026, 02:14 EAT",
    // Payout trigger status
    val payoutThresholdMet: Boolean = false,
    val payoutTriggerNdvi: Float = 0.30f,
    val payoutTriggerRain: Int = 40,
    val isLoading: Boolean = false,
)
