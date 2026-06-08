package dev.korryr.shambaguard.ui.features.farmer.presentation

// A single item in the coverage history timeline
data class CoverageHistoryItem(
    val title:     String,
    val date:      String,
    val detail:    String,       // "Paid via M-Pesa", "Oct 01, 2023", etc.
    val amount:    String? = null, // "+ KES 12,500" — null if not a payout
    val isPayout:  Boolean = false,
)

data class CoverageStatusUiState(
    // Policy card
    val policyName:           String        = "Fetching Policy...",
    val isActive:             Boolean       = false,
    val validThrough:         String        = "---",
    // Installment circles: true = paid (green ✓), false = pending (grey ○)
    val premiumInstallments:  List<Boolean> = emptyList(),

    // Current triggers
    val triggersLastUpdated:  String = "Syncing",
    val rainfallMm:           Float  = 0f,
    val rainfallTriggerMm:    Float  = 120f,
    val rainfallMaxMm:        Float  = 200f,
    val ndviValue:            Float  = 0.0f,
    val ndviWarning:          Float  = 0.40f,

    // History timeline
    val history: List<CoverageHistoryItem> = emptyList(),

    val isLoading: Boolean = true,
)
