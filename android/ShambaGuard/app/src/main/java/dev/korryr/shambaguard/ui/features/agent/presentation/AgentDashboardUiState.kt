package dev.korryr.shambaguard.ui.features.agent.presentation

// Sync status of a recently registered farmer
enum class RegistrationStatus { ACTIVE, QUEUED, DRAFT }

data class RecentRegistration(
    val id:       String,
    val name:     String,
    val county:   String,
    val status:   RegistrationStatus,
    val syncText: String,  // "Sync: 2h ago" / "Sync: Pending" / "Saved local"
)

data class AgentDashboardUiState(
    // Offline banner — hidden when offlinePending == 0
    val offlinePending:     Int  = 3,

    // Stats
    val farmersRegistered:  Int  = 47,
    val pendingSyncs:       Int  = 3,
    val newThisMonth:       Int  = 8,

    // Recent registrations list
    val recentRegistrations: List<RecentRegistration> = listOf(
        RecentRegistration("1", "Juma Kiprono",  "Makueni County",  RegistrationStatus.ACTIVE,  "Sync: 2h ago"),
        RecentRegistration("2", "Amina Muthoni", "Machakos County", RegistrationStatus.QUEUED,  "Sync: Pending"),
        RecentRegistration("3", "David Ochieng", "Kitui County",    RegistrationStatus.DRAFT,   "Saved local"),
    ),

    val isLoading: Boolean = false,
)
