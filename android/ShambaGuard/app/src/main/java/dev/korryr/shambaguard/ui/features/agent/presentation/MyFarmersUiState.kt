package dev.korryr.shambaguard.ui.features.agent.presentation

data class FarmerListItem(
    val farmerId: String,
    val farmId: String,
    val name: String,
    val policyStatus: String, // e.g. "ACTIVE", "EXPIRED", "NO POLICY"
    val syncStatus: String, // e.g. "SYNCED", "PENDING_SYNC"
    val createdAt: Long,
    val cropType: String,
    val areaHectares: Double,
)

data class MyFarmersUiState(
    val isLoading: Boolean = false,
    val farmers: List<FarmerListItem> = emptyList(),
    val errorMessage: String? = null,
)
