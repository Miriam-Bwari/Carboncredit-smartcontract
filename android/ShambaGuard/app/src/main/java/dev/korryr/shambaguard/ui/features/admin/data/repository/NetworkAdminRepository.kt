package dev.korryr.shambaguard.ui.features.admin.data.repository

import dev.korryr.shambaguard.ui.features.admin.data.remote.AdminApi
import dev.korryr.shambaguard.ui.features.admin.domain.model.AdminDashboardStats
import dev.korryr.shambaguard.ui.features.admin.domain.model.AgentModel
import dev.korryr.shambaguard.ui.features.admin.domain.model.PoolHealth
import dev.korryr.shambaguard.ui.features.admin.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkAdminRepository @Inject constructor(
    private val adminApi: AdminApi
) : AdminRepository {

    override fun getDashboardStats(): Flow<Result<AdminDashboardStats>> = flow {
        try {
            val response = adminApi.getDashboardStats()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    emit(Result.success(AdminDashboardStats(
                        totalFarmers = dto.totalFarmers,
                        activePolicies = dto.activePolicies,
                        pendingAgents = dto.pendingAgents,
                        poolBalanceKes = dto.poolBalanceKes
                    )))
                } ?: emit(Result.failure(Exception("Empty body")))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getPoolHealth(): Flow<Result<PoolHealth>> = flow {
        try {
            val response = adminApi.getPoolHealth()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    emit(Result.success(PoolHealth(
                        poolBalance = dto.poolBalance,
                        coverageLiability = dto.coverageLiability,
                        ratioPercentage = dto.ratioPercentage,
                        status = dto.status,
                        targetRatio = dto.targetRatio
                    )))
                } ?: emit(Result.failure(Exception("Empty body")))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getPendingAgents(): Flow<Result<List<AgentModel>>> = flow {
        try {
            val response = adminApi.getPendingAgents()
            if (response.isSuccessful) {
                val list = response.body()?.map { dto ->
                    AgentModel(
                        id = dto.id ?: "",
                        fullName = dto.fullName,
                        phoneNumber = dto.phoneNumber,
                        county = dto.county,
                        isActive = false
                    )
                } ?: emptyList<AgentModel>()
                emit(Result.success(list))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun approveAgent(agentId: String): Result<AgentModel> {
        return try {
            val response = adminApi.approveAgent(agentId)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    Result.success(AgentModel(
                        id = dto.id ?: "",
                        fullName = dto.fullName,
                        phoneNumber = dto.phoneNumber,
                        county = dto.county,
                        isActive = true
                    ))
                } else {
                    Result.failure(Exception("Empty body"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
