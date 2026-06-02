package dev.korryr.shambaguard.ui.features.auth.data.repository

import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.features.auth.data.remote.AuthApi
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.SendOtpRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.VerifyOtpRequestDto
import dev.korryr.shambaguard.ui.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    
    override suspend fun sendOtp(phone: String): Result<Unit> {
        return try {
            authApi.sendOtp(SendOtpRequestDto(phone))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<UserRole> {
        return try {
            val response = authApi.verifyOtp(VerifyOtpRequestDto(phone, otp))
            
            // Save to SessionManager
            sessionManager.saveSession(
                token = response.accessToken,
                role = response.role,
                userId = response.userId
            )
            
            val userRole = try {
                UserRole.valueOf(response.role.replaceFirstChar { it.uppercase() })
            } catch (e: Exception) {
                UserRole.Unauthenticated
            }
            
            Result.success(userRole)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}
