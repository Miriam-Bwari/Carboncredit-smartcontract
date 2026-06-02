package dev.korryr.shambaguard.ui.features.farmer.data.repository

import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.FarmReportDao
import dev.korryr.shambaguard.data.local.entity.FarmEntity
import dev.korryr.shambaguard.data.local.entity.FarmReportEntity
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmDto
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmRepositoryImpl @Inject constructor(
    private val farmApi: FarmApi,
    private val farmDao: FarmDao,
    private val farmReportDao: FarmReportDao
) : FarmRepository {

    override fun getFarm(farmId: String): Flow<FarmEntity?> {
        return farmDao.getFarmById(farmId)
    }

    override suspend fun syncFarm(farmId: String): Result<Unit> {
        return try {
            val dto = farmApi.getFarm(farmId)
            val entity = FarmEntity(
                farmId = dto.farmId,
                farmerId = dto.farmerId,
                agentId = dto.agentId,
                polygonJson = dto.polygon,
                areaHectares = dto.areaHectares,
                region = dto.region,
                cropType = "",
                tillageMethod = "",
                treeCount = 0,
                carbonStatus = "PENDING",
                syncStatus = "SYNCED",
                lastSyncedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
            farmDao.insertFarm(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pushPendingFarm(farmEntity: FarmEntity): Result<Unit> {
        return try {
            val dto = FarmDto(
                farmId = farmEntity.farmId,
                farmerId = farmEntity.farmerId,
                agentId = farmEntity.agentId,
                polygon = farmEntity.polygonJson,
                areaHectares = farmEntity.areaHectares,
                region = farmEntity.region
            )
            farmApi.registerFarm(dto)
            
            // Update local sync status
            val syncedEntity = farmEntity.copy(
                syncStatus = "SYNCED",
                lastSyncedAt = System.currentTimeMillis()
            )
            farmDao.insertFarm(syncedEntity)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFarmReport(farmId: String): Flow<FarmReportEntity?> {
        return farmReportDao.getLatestReportForFarm(farmId)
    }

    override suspend fun syncFarmReport(farmId: String): Result<Unit> {
        return try {
            val dto = farmApi.getFarmReport(farmId)
            
            val risk = if (dto.droughtScore > 0.8 && dto.rainfallMm < 40) "CRITICAL" else if (dto.droughtScore > 0.5) "MODERATE" else "LOW"
            
            val entity = FarmReportEntity(
                reportId = "${dto.farmId}_latest",
                farmId = dto.farmId,
                ndviMean = dto.ndviMean,
                droughtScore = dto.droughtScore,
                rainfallMm = dto.rainfallMm,
                forecastScore = dto.forecastDroughtProb,
                riskLevel = risk,
                recommendation = if (risk == "CRITICAL") "Prepare for drought" else "Maintain current practices",
                generatedAt = System.currentTimeMillis()
            )
            farmReportDao.insertFarmReport(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
