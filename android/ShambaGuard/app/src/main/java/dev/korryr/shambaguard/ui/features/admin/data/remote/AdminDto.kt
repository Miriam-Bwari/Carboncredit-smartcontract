package dev.korryr.shambaguard.ui.features.admin.data.remote

import com.google.gson.annotations.SerializedName

data class AdminDashboardStatsDto(
    @SerializedName("total_farmers") val totalFarmers: Int,
    @SerializedName("active_policies") val activePolicies: Int,
    @SerializedName("pending_agents") val pendingAgents: Int,
    @SerializedName("pool_balance_kes") val poolBalanceKes: Double
)

data class PoolHealthDto(
    @SerializedName("pool_balance") val poolBalance: Double,
    @SerializedName("coverage_liability") val coverageLiability: Double,
    @SerializedName("ratio_percentage") val ratioPercentage: Double,
    @SerializedName("status") val status: String,
    @SerializedName("target_ratio") val targetRatio: Double
)

data class AdminAgentDto(
    @SerializedName("id") val id: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("county") val county: String,
    @SerializedName("is_active") val isActive: Boolean
)
