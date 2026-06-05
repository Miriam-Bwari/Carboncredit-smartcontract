package dev.korryr.shambaguard.ui.features.farmer.presentation

// Risk level — drives the alert card colour and messaging
enum class DroughtRisk { LOW, MODERATE, HIGH, CRITICAL }

// A single item in the Recent Activity timeline
data class ActivityItem(
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: ActivityType,
)

enum class ActivityType { DROUGHT_ALERT, PAYOUT, CARBON }

data class FarmerDashboardUiState(
    val farmerName: String = "",
    val farmName: String = "",
    val farmRegion: String = "",
    val droughtRisk: DroughtRisk = DroughtRisk.LOW,
    // NDVI — 0.0 to 1.0; healthy vegetation > 0.5; drought risk < 0.2
    val ndviScore: Float = 0.0f,
    // Rainfall over last 21 days (mm)
    val rainfallMm: Int = 0,
    // Rainfall % deviation from the seasonal average
    val rainfallDelta: Int = 0,
    // Policy
    val policyActive: Boolean = false,
    val policyExpiry: String = "",
    // Carbon
    val carbonTonnes: Float = 0f,
    // Recent activity timeline
    val recentActivity: List<ActivityItem> = emptyList(),
    val isLoading: Boolean = false,
)
