package dev.korryr.shambaguard.ui.features.agent.data.repository

import dev.korryr.shambaguard.ui.features.agent.data.remote.AgentApi
import dev.korryr.shambaguard.ui.features.agent.data.remote.dto.AgentDashboardDto
import dev.korryr.shambaguard.ui.features.agent.domain.repository.AgentRepository
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentApi: AgentApi
) : AgentRepository {

    override suspend fun getDashboardStats(agentId: String): Result<AgentDashboardDto> {
        return try {
            val response = agentApi.getAgentDashboard(agentId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
