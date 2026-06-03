package dev.korryr.shambaguard.ui.features.auth.data.remote

import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AuthResponseDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerRegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/farmers/register")
    suspend fun register(@Body request: FarmerRegisterRequestDto): AuthResponseDto

    @POST("api/farmers/login")
    suspend fun login(@Body request: FarmerLoginRequestDto): AuthResponseDto
}
