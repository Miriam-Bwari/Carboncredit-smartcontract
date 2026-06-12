package dev.korryr.shambaguard.ui.features.auth.domain.repository

import dev.korryr.shambaguard.navigation.UserRole

interface AuthRepository {

    /**
     * Login for both Farmers and Agents.
     * The [role] param tells which backend endpoint to call.
     * Returns the [UserRole] parsed from the JWT response.
     */
    suspend fun login(phone: String, password: String, role: UserRole): Result<UserRole>

    /**
     * Register a new Farmer account.
     * Returns the farmer_id UUID string on success.
     */
    suspend fun registerFarmer(
        fullName: String,
        phone: String,
        county: String,
        password: String,
    ): Result<String>

    /**
     * Register a new Agent account.
     * Returns the agent_id UUID string on success.
     */
    suspend fun registerAgent(
        fullName: String,
        phone: String,
        county: String,
        password: String,
    ): Result<String>

    /** Clears the stored JWT session — called on sign-out. */
    suspend fun logout()
}
