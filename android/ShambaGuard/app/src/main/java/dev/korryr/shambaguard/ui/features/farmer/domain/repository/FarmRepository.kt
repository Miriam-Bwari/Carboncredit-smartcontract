package dev.korryr.shambaguard.ui.features.farmer.domain.repository

import dev.korryr.shambaguard.data.local.entity.FarmEntity
import dev.korryr.shambaguard.data.local.entity.FarmReportEntity
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    // Offline-first Flow observing local DB
    fun getFarm(farmId: String): Flow<FarmEntity?>
    
    // Remote fetch & save to DB
    suspend fun syncFarm(farmId: String): Result<Unit>
    
    // Send local created farm to remote
    suspend fun pushPendingFarm(farmEntity: FarmEntity): Result<Unit>
    
    fun getFarmReport(farmId: String): Flow<FarmReportEntity?>
    suspend fun syncFarmReport(farmId: String): Result<Unit>
    
    // Remote dashboard data fetching
    suspend fun getFarmer(farmerId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmerDetailsDto>
    suspend fun getFarmerFarms(farmerId: String): Result<List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmSummaryDto>>
    suspend fun getCarbonHistory(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonHistoryDto>
    suspend fun getAdvice(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.AdviceDto>
    suspend fun getWeather(farmId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.WeatherDto>
    suspend fun getPolicy(farmerId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PolicyDto>
    
    suspend fun addPractice(farmId: String, practice: dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto>
    suspend fun getPractices(farmId: String): Result<List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto>>
}
