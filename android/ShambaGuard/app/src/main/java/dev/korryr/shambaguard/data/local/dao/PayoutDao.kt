package dev.korryr.shambaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.korryr.shambaguard.data.local.entity.PayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PayoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Query("SELECT * FROM payouts WHERE farmId = :farmId")
    fun getPayoutsByFarm(farmId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE policyId = :policyId")
    fun getPayoutsByPolicy(policyId: String): Flow<List<PayoutEntity>>
}
