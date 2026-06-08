package dev.korryr.shambaguard.ui.features.farmer.data.repository

import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.FarmReportDao
import dev.korryr.shambaguard.data.local.entity.FarmEntity
import dev.korryr.shambaguard.data.local.entity.FarmReportEntity
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmRegisterRequestDto
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmRepositoryImpl @Inject constructor(
    private val farmApi: FarmApi,
    private val farmDao: FarmDao,
    private val farmReportDao: FarmReportDao,
) : FarmRepository {

    override fun getFarm(farmId: String): Flow<FarmEntity?> = farmDao.getFarmById(farmId)

    override suspend fun syncFarm(farmId: String): Result<Unit> = try {
        val dto = farmApi.getFarm(farmId)
        val entity = FarmEntity(
            farmId = dto.farmId,
            farmerId = dto.farmerId,
            agentId = "", // backend FarmDto doesn't include agentId
            polygonJson = dto.boundaryCoords.toString(),
            areaHectares = dto.areaHectares,
            region = dto.county, // backend uses county; stored as region locally
            cropType = dto.cropType,
            tillageMethod = "",
            treeCount = 0,
            carbonStatus = "PENDING",
            syncStatus = "SYNCED",
            lastSyncedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
        )
        farmDao.insertFarm(entity)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun pushPendingFarm(farmEntity: FarmEntity): Result<Unit> = try {
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
            coordinates = listOf(coordsList), // Wrap in another list for the outer ring
        )

        val dto = FarmRegisterRequestDto(
            farmerId = farmEntity.farmerId,
            name = "Farm ${farmEntity.farmId.take(4)}", // Mock name
            boundaryCoords = geoJsonPolygon,
            soilType = "Loam", // Default placeholder
            cropType = farmEntity.cropType.takeIf { it.isNotBlank() } ?: "Maize",
            county = farmEntity.region,
        )
        farmApi.registerFarm(dto)

        // Update local sync status
        val syncedEntity = farmEntity.copy(
            syncStatus = "SYNCED",
            lastSyncedAt = System.currentTimeMillis(),
        )
        farmDao.insertFarm(syncedEntity)

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getFarmReport(farmId: String): Flow<FarmReportEntity?> = farmReportDao.getLatestReportForFarm(farmId)

    override suspend fun syncFarmReport(farmId: String): Result<Unit> = try {
        val dto = farmApi.getFarmReport(farmId)

        val risk = if (dto.droughtScore > 0.8 && dto.rainfallMm < 40) {
            "CRITICAL"
        } else if (dto.droughtScore > 0.5) {
            "MODERATE"
        } else {
            "LOW"
        }

        val entity = FarmReportEntity(
            reportId = "${dto.farmId}_latest",
            farmId = dto.farmId,
            ndviMean = dto.ndviMean,
            droughtScore = dto.droughtScore,
            rainfallMm = dto.rainfallMm,
            forecastScore = dto.forecastDroughtProb,
            riskLevel = risk,
            recommendation = if (risk == "CRITICAL") "Prepare for drought" else "Maintain current practices",
            generatedAt = System.currentTimeMillis(),
        )
        farmReportDao.insertFarmReport(entity)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFarmer(farmerId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmerDetailsDto> = try {
        Result.success(farmApi.getFarmer(farmerId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFarmerFarms(farmerId: String): Result<List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmSummaryDto>> = try {
        Result.success(farmApi.getFarmerFarms(farmerId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCarbonHistory(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonHistoryDto> = try {
        Result.success(farmApi.getCarbonHistory(farmId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAdvice(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.AdviceDto> = try {
        Result.success(farmApi.getAdvice(farmId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getWeather(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.WeatherDto> = try {
        Result.success(farmApi.getWeather(farmId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPolicy(farmerId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PolicyDto> = try {
        Result.success(farmApi.getPolicy(farmerId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun triggerStkPush(farmerId: String, phoneNumber: String, amountKes: Int): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushResponseDto> = try {
        val request = dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushRequestDto(
            farmerId = farmerId,
            phoneNumber = phoneNumber,
            amountKes = amountKes,
        )
        Result.success(farmApi.triggerStkPush(request))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPaymentStatus(checkoutId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PaymentStatusResponseDto> = try {
        Result.success(farmApi.getPaymentStatus(checkoutId))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addPractice(
        farmId: String,
        practice: dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto,
    ): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto> = try {
        Result.success(farmApi.addPractice(farmId, practice))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPractices(farmId: String): Result<List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto>> = try {
        Result.success(farmApi.getPractices(farmId))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
