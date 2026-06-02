package dev.korryr.shambaguard.ui.features.auth.data.remote

import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AuthResponseDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.SendOtpRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.VerifyOtpRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/otp/send")
    suspend fun sendOtp(@Body request: SendOtpRequestDto)

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): AuthResponseDto
}
