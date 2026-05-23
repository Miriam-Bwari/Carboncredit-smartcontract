package dev.korryr.shambaguard.ui.features.farmer.presentation

// A single day in the 7-day rainfall forecast strip
data class RainfallDay(
    val day:        String,   // "Mon", "Tue", etc.
    val hasRain:    Boolean,  // false = sun icon, true = cloud icon
    val rainfallMm: Int,
)

data class EarlyWarningUiState(
    // Alert banner
    val alertTitleSwahili: String = "Ukame unakuja /",
    val alertTitleEnglish: String = "Drought Coming",
    val alertBody:         String = "Severe lack of rain expected in the next 14 days.",
    val aiConfidence:      Int    = 89,

    // Farm stress map
    val mapLastUpdated: String  = "Today",

    // 7-day rainfall forecast
    val rainfallForecast: List<RainfallDay> = listOf(
        RainfallDay("Mon", false, 0),
        RainfallDay("Tue", false, 0),
        RainfallDay("Wed", true,  1),
        RainfallDay("Thu", false, 0),
        RainfallDay("Fri", false, 0),
        RainfallDay("Sat", false, 0),
        RainfallDay("Sun", false, 0),
    ),

    // AI crop recommendation
    val aiCropTitle:  String = "Panda Cowpeas, si Mahindi",
    val aiCropBody:   String = "Due to the projected severe lack of rainfall over the next crucial growing weeks, maize (mahindi) will likely fail. Cowpeas are highly drought-resistant and have a 75% higher chance of yield in current conditions.",

    // Policy coverage card
    val coverageActive:    Boolean = true,
    val payoutKes:         Int     = 8_000,
    val payoutCondition:   String  = "If drought is confirmed by end of month.",

    val currentRisk: DroughtRisk = DroughtRisk.HIGH,
    val isLoading:   Boolean     = false,
)
