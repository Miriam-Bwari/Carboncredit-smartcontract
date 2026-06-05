package dev.korryr.shambaguard.ui.features.agent.domain.repository

import dev.korryr.shambaguard.ui.features.agent.data.remote.dto.AgentDashboardDto

interface AgentRepository {
    suspend fun getDashboardStats(agentId: String): Result<AgentDashboardDto>
}
