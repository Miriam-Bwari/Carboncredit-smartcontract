package dev.korryr.shambaguard.ui.features.agent.data.remote

import dev.korryr.shambaguard.ui.features.agent.data.remote.dto.AgentDashboardDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AgentApi {
    @GET("api/agents/dashboard/{agent_id}")
    suspend fun getAgentDashboard(@Path("agent_id") agentId: String): AgentDashboardDto
}
