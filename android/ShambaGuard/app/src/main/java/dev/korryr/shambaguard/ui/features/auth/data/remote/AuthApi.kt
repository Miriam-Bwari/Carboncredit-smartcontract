package dev.korryr.shambaguard.ui.features.auth.data.remote

import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AgentLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AgentRegisterRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AgentRegisterResponseDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AuthResponseDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerRegisterRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerRegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Farmer endpoints

    @POST("api/farmers/register")
    suspend fun registerFarmer(
        @Body request: FarmerRegisterRequestDto,
    ): FarmerRegisterResponseDto

    @POST("api/farmers/login")
    suspend fun loginFarmer(
        @Body request: FarmerLoginRequestDto,
    ): AuthResponseDto

    // Agent endpoints

    @POST("api/agents/register")
    suspend fun registerAgent(
        @Body request: AgentRegisterRequestDto,
    ): AgentRegisterResponseDto

    @POST("api/agents/login")
    suspend fun loginAgent(
        @Body request: AgentLoginRequestDto,
    ): AuthResponseDto
}
