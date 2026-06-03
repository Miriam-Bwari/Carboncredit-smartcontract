package dev.korryr.shambaguard.ui.features.auth.data.repository

import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.features.auth.data.remote.AuthApi
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(phone: String, pin: String): Result<UserRole> {
        return try {
            val response = authApi.login(FarmerLoginRequestDto(phone, pin))
            
            // Backend doesn't explicitly return role in OpenAPI, assuming "Farmer" as default for /api/farmers/login
            val roleStr = response.role ?: "Farmer"
            val userId = response.userId ?: "unknown_id"
            
            // Save to SessionManager
            sessionManager.saveSession(
                token = response.accessToken,
                role = roleStr,
                userId = userId
            )
            
            val userRole = try {
                UserRole.valueOf(roleStr.replaceFirstChar { it.uppercase() })
            } catch (e: Exception) {
                UserRole.Farmer
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
