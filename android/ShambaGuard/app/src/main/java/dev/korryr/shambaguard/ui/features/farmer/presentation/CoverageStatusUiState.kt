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
    val policyName:           String        = "Premium Weather Index",
    val isActive:             Boolean       = true,
    val validThrough:         String        = "Long Rains (May 2024)",
    // Installment circles: true = paid (green ✓), false = pending (grey ○)
    val premiumInstallments:  List<Boolean> = listOf(true, true, false),

    // Current triggers
    val triggersLastUpdated:  String = "Today",
    val rainfallMm:           Float  = 142f,
    val rainfallTriggerMm:    Float  = 120f,
    val rainfallMaxMm:        Float  = 200f,
    val ndviValue:            Float  = 0.38f,
    val ndviWarning:          Float  = 0.40f,

    // History timeline
    val history: List<CoverageHistoryItem> = listOf(
        CoverageHistoryItem(
            title    = "Drought Payout - Phase 2",
            date     = "Nov 12, 2023",
            detail   = "Paid via M-Pesa",
            amount   = "+ KES 12,500",
            isPayout = true,
        ),
        CoverageHistoryItem(
            title    = "Policy Renewed",
            date     = "Oct 01, 2023",
            detail   = "",
            isPayout = false,
        ),
    ),

    val isLoading: Boolean = false,
)
