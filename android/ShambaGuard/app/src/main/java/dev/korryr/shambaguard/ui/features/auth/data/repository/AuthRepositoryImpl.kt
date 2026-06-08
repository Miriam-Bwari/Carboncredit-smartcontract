package dev.korryr.shambaguard.ui.features.auth.data.repository

import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.features.auth.data.remote.AuthApi
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AgentLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.AgentRegisterRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerLoginRequestDto
import dev.korryr.shambaguard.ui.features.auth.data.remote.dto.FarmerRegisterRequestDto
import dev.korryr.shambaguard.ui.features.auth.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
) : AuthRepository {

    // Login

    override suspend fun login(
        phone: String,
        password: String,
        role: UserRole,
    ): Result<UserRole> = runCatching {
        val response = when (role) {
            UserRole.Farmer -> authApi.loginFarmer(FarmerLoginRequestDto(phone, password))
            UserRole.Agent -> authApi.loginAgent(AgentLoginRequestDto(phone, password))
            // Admin login not yet implemented on backend — fallback to farmer endpoint
            else -> authApi.loginFarmer(FarmerLoginRequestDto(phone, password))
        }

        sessionManager.saveSession(
            token = response.accessToken,
            role = response.role,
            userId = response.userId,
        )

        // Map backend role string to app enum — default Farmer on unknown value
        try {
            UserRole.valueOf(response.role.replaceFirstChar { it.uppercase() })
        } catch (e: IllegalArgumentException) {
            Timber.w("Unknown role '%s' from backend — defaulting to Farmer", response.role)
            UserRole.Farmer
        }
    }

    // Farmer registration

    override suspend fun registerFarmer(
        fullName: String,
        phone: String,
        county: String,
        password: String,
    ): Result<String> = runCatching {
        val response = authApi.registerFarmer(
            FarmerRegisterRequestDto(
                fullName = fullName,
                phoneNumber = phone,
                password = password,
                county = county,
            ),
        )
        response.farmerId
    }

    // Agent registration

    override suspend fun registerAgent(
        fullName: String,
        phone: String,
        county: String,
        password: String,
    ): Result<String> = runCatching {
        val response = authApi.registerAgent(
            AgentRegisterRequestDto(
                fullName = fullName,
                phoneNumber = phone,
                password = password,
                county = county,
            ),
        )
        response.agentId
    }

    // Logout

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}
