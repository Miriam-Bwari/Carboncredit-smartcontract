package dev.korryr.shambaguard.ui.features.farmer.presentation

// A single day in the 7-day rainfall forecast strip
data class RainfallDay(
    val day:        String,   // "Mon", "Tue", etc.
    val hasRain:    Boolean,  // false = sun icon, true = cloud icon
    val rainfallMm: Int,
)

data class EarlyWarningUiState(
    // Alert banner
    val alertTitleSwahili: String = "Inapakia...",
    val alertTitleEnglish: String = "Loading...",
    val alertBody:         String = "Fetching latest drought data...",
    val aiConfidence:      Int    = 0,

    // Farm stress map
    val mapLastUpdated: String  = "Syncing",

    // 7-day rainfall forecast
    val rainfallForecast: List<RainfallDay> = emptyList(),

    // AI crop recommendation
    val aiCropTitle:  String = "Analysing farm...",
    val aiCropBody:   String = "Fetching AI recommendations for your crop.",

    // Policy coverage card
    val coverageActive:    Boolean = false,
    val payoutKes:         Int     = 0,
    val payoutCondition:   String  = "Loading policy details...",

    val currentRisk: DroughtRisk = DroughtRisk.LOW,
    val isLoading:   Boolean     = true,
)
