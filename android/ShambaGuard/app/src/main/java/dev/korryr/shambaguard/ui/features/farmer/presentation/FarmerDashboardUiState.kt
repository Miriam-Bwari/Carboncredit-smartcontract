package dev.korryr.shambaguard.ui.features.farmer.presentation

// Risk level — drives the alert card colour and messaging
enum class DroughtRisk { LOW, MODERATE, HIGH, CRITICAL }

// A single item in the Recent Activity timeline
data class ActivityItem(
    val title:       String,
    val description: String,
    val timeAgo:     String,
    val type:        ActivityType,
)

enum class ActivityType { DROUGHT_ALERT, PAYOUT, CARBON }

data class FarmerDashboardUiState(
    val farmerName:     String       = "Mary",
    val farmName:       String       = "Shamba la Mary",
    val farmRegion:     String       = "Ukambani, Machakos",
    val droughtRisk:    DroughtRisk  = DroughtRisk.HIGH,
    // NDVI — 0.0 to 1.0; healthy vegetation > 0.5; drought risk < 0.2
    val ndviScore:      Float        = 0.24f,
    // Rainfall over last 21 days (mm)
    val rainfallMm:     Int          = 12,
    // Rainfall % deviation from the seasonal average
    val rainfallDelta:  Int          = -65,
    // Policy
    val policyActive:   Boolean      = true,
    val policyExpiry:   String       = "Dec 2024",
    // Carbon
    val carbonTonnes:   Float        = 4.2f,
    // Recent activity timeline
    val recentActivity: List<ActivityItem> = listOf(
        ActivityItem(
            title       = "Drought Alert Issued",
            description = "NDVI dropped below threshold in Machakos region.",
            timeAgo     = "2 days ago",
            type        = ActivityType.DROUGHT_ALERT,
        ),
        ActivityItem(
            title       = "Payout Disbursed",
            description = "KES 1,200 deposited to M-PESA for delayed short rains.",
            timeAgo     = "14 days ago",
            type        = ActivityType.PAYOUT,
        ),
        ActivityItem(
            title       = "Carbon Verification",
            description = "0.8 tonnes verified for agroforestry practices.",
            timeAgo     = "1 month ago",
            type        = ActivityType.CARBON,
        ),
    ),
    val isLoading: Boolean = false,
)
