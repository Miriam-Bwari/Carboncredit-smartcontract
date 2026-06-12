package dev.korryr.shambaguard.ui.features.farmer.presentation

// DroughtInsightsScreen UI state — shown when farmer drills into "Full Analysis"
data class DroughtInsightsUiState(
    val ndviScore: Float = 0.0f,
    val ndviTrend: String = "Syncing",
    val ndviTrendSwahili: String = "Inapakia",
    val rainfallMm: Int = 0,
    val rainfallDelta: Int = 0,
    // NDVI history for the simple bar visualisation (last 6 readings, newest last)
    val ndviHistory: List<Float> = emptyList(),
    val satelliteSource: String = "Syncing",
    val lastUpdated: String = "Syncing",
    // Payout trigger status
    val payoutThresholdMet: Boolean = false,
    val payoutTriggerNdvi: Float = 0.30f,
    val payoutTriggerRain: Int = 40,
    val isLoading: Boolean = true,
    val polygonPoints: List<com.google.android.gms.maps.model.LatLng> = emptyList(),
)
