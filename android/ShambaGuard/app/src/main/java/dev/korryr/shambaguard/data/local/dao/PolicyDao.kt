package dev.korryr.shambaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.korryr.shambaguard.data.local.entity.PolicyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PolicyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: PolicyEntity)

    @Query("SELECT * FROM policies WHERE farmId = :farmId")
    fun getPolicyByFarm(farmId: String): Flow<PolicyEntity?>

    @Query("SELECT * FROM policies WHERE farmerId = :farmerId")
    fun getPoliciesByFarmer(farmerId: String): Flow<List<PolicyEntity>>

    @Query("SELECT * FROM policies WHERE farmId = :farmId")
    suspend fun getPolicyByFarmSync(farmId: String): PolicyEntity?
}
