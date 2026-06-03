package dev.korryr.shambaguard.ui.features.auth.domain.repository

import dev.korryr.shambaguard.navigation.UserRole

interface AuthRepository {
    suspend fun login(phone: String, pin: String): Result<UserRole>
    suspend fun logout()
}
