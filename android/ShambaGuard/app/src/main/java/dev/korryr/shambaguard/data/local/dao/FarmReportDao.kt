package dev.korryr.shambaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.korryr.shambaguard.data.local.entity.FarmReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmReport(report: FarmReportEntity)

    @Query("SELECT * FROM farm_reports WHERE farmId = :farmId ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestReportForFarm(farmId: String): Flow<FarmReportEntity?>

    @Query("DELETE FROM farm_reports")
    suspend fun deleteAll()
}
