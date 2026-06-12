package dev.korryr.shambaguard.ui.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

// Registration request bodies

data class FarmerRegisterRequestDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("county") val county: String,
)

data class AgentRegisterRequestDto(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("county") val county: String,
)

// Registration response bodies

/** Returned by POST /api/farmers/register */
data class FarmerRegisterResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("farmer_id") val farmerId: String,
)

/** Returned by POST /api/agents/register */
data class AgentRegisterResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("agent_id") val agentId: String,
)

// Login request bodies

data class FarmerLoginRequestDto(
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
)

data class AgentLoginRequestDto(
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
)

// Login response body

/** Returned by both /api/farmers/login and /api/agents/login */
data class AuthResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("role") val role: String, // "Farmer" | "Agent" | "Admin"
    @SerializedName("user_id") val userId: String, // UUID
)
