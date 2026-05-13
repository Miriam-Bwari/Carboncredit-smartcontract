package dev.korryr.shambaguard.ui.features.auth.presentation

// Step 3 — farm practices UI state
data class FarmPracticesUiState(
    val selectedCrops:   Set<String> = emptySet(),
    val selectedMethod:  String?     = null,
    val selectedWater:   String?     = null,
    val treeCount:       Int         = 0,
    val isSubmitting:    Boolean     = false,
)

// Returns true when the minimum required fields are filled in
fun FarmPracticesUiState.canComplete(): Boolean =
    selectedCrops.isNotEmpty() && selectedMethod != null && selectedWater != null
