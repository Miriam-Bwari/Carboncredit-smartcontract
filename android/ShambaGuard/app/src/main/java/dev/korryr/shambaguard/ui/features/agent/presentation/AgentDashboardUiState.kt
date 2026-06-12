package dev.korryr.shambaguard.ui.features.agent.presentation

// Sync status of a recently registered farmer
enum class RegistrationStatus { ACTIVE, QUEUED, DRAFT }

data class RecentRegistration(
    val id: String,
    val name: String,
    val county: String,
    val status: RegistrationStatus,
    val syncText: String, // "Sync: 2h ago" / "Sync: Pending" / "Saved local"
)

data class AgentDashboardUiState(
    // Offline banner — hidden when offlinePending == 0
    val offlinePending: Int = 0,

    // Stats
    val farmersRegistered: Int = 0,
    val pendingSyncs: Int = 0,
    val newThisMonth: Int = 0,

    // Recent registrations list
    val recentRegistrations: List<RecentRegistration> = emptyList(),

    val isLoading: Boolean = false,
)
