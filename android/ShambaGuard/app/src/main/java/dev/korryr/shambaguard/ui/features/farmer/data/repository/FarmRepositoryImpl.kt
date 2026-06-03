package dev.korryr.shambaguard.ui.features.farmer.data.repository

import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.FarmReportDao
import dev.korryr.shambaguard.data.local.entity.FarmEntity
import dev.korryr.shambaguard.data.local.entity.FarmReportEntity
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmRegisterRequestDto
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
                farmId        = dto.farmId,
                farmerId      = dto.farmerId,
                agentId       = "",             // backend FarmDto doesn't include agentId
                polygonJson   = dto.boundaryCoords.toString(),
                areaHectares  = dto.areaHectares,
                region        = dto.county,     // backend uses county; stored as region locally
                cropType      = dto.cropType,
                tillageMethod = "",
                treeCount     = 0,
                carbonStatus  = "PENDING",
                syncStatus    = "SYNCED",
                lastSyncedAt  = System.currentTimeMillis(),
                createdAt     = System.currentTimeMillis()
            )
            farmDao.insertFarm(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pushPendingFarm(farmEntity: FarmEntity): Result<Unit> {
        return try {
            // Parse polygonJson to GeoJsonPolygonDto.
            // GeoJSON expects [longitude, latitude]!
            val coordsList = mutableListOf<List<Double>>()
            try {
                val jsonArray = org.json.JSONArray(farmEntity.polygonJson)
                for (i in 0 until jsonArray.length()) {
                    val point = jsonArray.getJSONObject(i)
                    // GeoJSON format: [longitude, latitude]
                    coordsList.add(listOf(point.getDouble("lng"), point.getDouble("lat")))
                }
                
                // GeoJSON Polygon MUST be closed (first point == last point)
                if (coordsList.isNotEmpty() && coordsList.first() != coordsList.last()) {
                    coordsList.add(coordsList.first())
                }
            } catch (e: Exception) {
                // Return empty if parsing fails
            }

            val geoJsonPolygon = dev.korryr.shambaguard.core.network.GeoJsonPolygonDto(
                coordinates = listOf(coordsList) // Wrap in another list for the outer ring
            )

            val dto = FarmRegisterRequestDto(
                farmerId = farmEntity.farmerId,
                name = "Farm ${farmEntity.farmId.take(4)}", // Mock name
                boundaryCoords = geoJsonPolygon,
                soilType = "Loam", // Default placeholder
                cropType = farmEntity.cropType.takeIf { it.isNotBlank() } ?: "Maize",
                county = farmEntity.region
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
