package dev.korryr.shambaguard.ui.features.agent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentRecentRegistrationDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("county") val county: String,
    @SerializedName("status") val status: String,
    @SerializedName("syncText") val syncText: String
)

data class AgentDashboardDto(
    @SerializedName("farmersRegistered") val farmersRegistered: Int,
    @SerializedName("pendingSyncs") val pendingSyncs: Int,
    @SerializedName("newThisMonth") val newThisMonth: Int,
    @SerializedName("recentRegistrations") val recentRegistrations: List<AgentRecentRegistrationDto>
)
