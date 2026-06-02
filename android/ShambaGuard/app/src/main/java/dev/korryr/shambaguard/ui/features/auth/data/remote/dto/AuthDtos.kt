package dev.korryr.shambaguard.ui.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendOtpRequestDto(
    @SerializedName("phone") val phone: String
)

data class VerifyOtpRequestDto(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)

data class AuthResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("role") val role: String,
    @SerializedName("user_id") val userId: String
)
