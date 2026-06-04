package dev.korryr.shambaguard.ui.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FarmerLoginRequestDto(
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String
)

data class FarmerRegisterRequestDto(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("county") val county: String
)

data class AuthResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer",
    @SerializedName("role") val role: String,       // "Farmer" | "Agent" | "Admin"
    @SerializedName("user_id") val userId: String   // UUID
)
