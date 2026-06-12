package dev.korryr.shambaguard.ui.features.admin.domain.repository

import dev.korryr.shambaguard.ui.features.admin.domain.model.AdminDashboardStats
import dev.korryr.shambaguard.ui.features.admin.domain.model.AgentModel
import dev.korryr.shambaguard.ui.features.admin.domain.model.PoolHealth
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getDashboardStats(): Flow<Result<AdminDashboardStats>>
    fun getPoolHealth(): Flow<Result<PoolHealth>>
    fun getPendingAgents(): Flow<Result<List<AgentModel>>>
    suspend fun approveAgent(agentId: String): Result<AgentModel>
    fun getCarbonScanStatus(): Flow<Result<dev.korryr.shambaguard.ui.features.admin.domain.model.CarbonScanStatus>>
}
