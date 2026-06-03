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
}
