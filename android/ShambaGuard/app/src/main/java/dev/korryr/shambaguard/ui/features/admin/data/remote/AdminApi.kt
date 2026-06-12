package dev.korryr.shambaguard.ui.features.admin.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApi {

    @GET("admin/dashboard")
    suspend fun getDashboardStats(): Response<AdminDashboardStatsDto>

    @GET("admin/pool/health")
    suspend fun getPoolHealth(): Response<PoolHealthDto>

    @GET("admin/agents/pending")
    suspend fun getPendingAgents(): Response<List<AdminAgentDto>>

    @PUT("admin/agents/{agent_id}/approve")
    suspend fun approveAgent(
        @Path("agent_id") agentId: String,
    ): Response<AdminAgentDto>

    @GET("carbon/scan-status")
    suspend fun getCarbonScanStatus(): Response<CarbonScanStatusDto>
}
