package dev.korryr.shambaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.korryr.shambaguard.data.local.entity.FarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity)

    @Query("SELECT * FROM farms WHERE farmId = :farmId")
    fun getFarmById(farmId: String): Flow<FarmEntity?>

    @Query("SELECT * FROM farms WHERE agentId = :agentId")
    fun getFarmsByAgent(agentId: String): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE farmerId = :farmerId")
    fun getFarmsByFarmer(farmerId: String): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE agentId = :agentId")
    suspend fun getFarmsByAgentSync(agentId: String): List<FarmEntity>

    @Query("UPDATE farms SET syncStatus = :status, lastSyncedAt = :timestamp WHERE farmId = :farmId")
    suspend fun updateSyncStatus(farmId: String, status: String, timestamp: Long)

    @Query("SELECT * FROM farms WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getPendingFarmsSync(): List<FarmEntity>

    @Query("DELETE FROM farms")
    suspend fun deleteAll()
}
