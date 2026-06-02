package dev.korryr.shambaguard.ui.features.auth.domain.repository

import dev.korryr.shambaguard.navigation.UserRole

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<UserRole>
    suspend fun logout()
}
