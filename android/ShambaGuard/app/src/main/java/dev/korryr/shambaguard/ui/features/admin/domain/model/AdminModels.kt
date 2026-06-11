package dev.korryr.shambaguard.ui.features.admin.domain.model

data class AdminDashboardStats(
    val totalFarmers: Int,
    val activePolicies: Int,
    val pendingAgents: Int,
    val poolBalanceKes: Double
)

data class PoolHealth(
    val poolBalance: Double,
    val coverageLiability: Double,
    val ratioPercentage: Double,
    val status: String,
    val targetRatio: Double
)

data class AgentModel(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val county: String,
    val isActive: Boolean
)
