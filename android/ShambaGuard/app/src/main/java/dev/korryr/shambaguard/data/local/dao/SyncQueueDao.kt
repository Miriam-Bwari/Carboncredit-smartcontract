package dev.korryr.shambaguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.korryr.shambaguard.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun getPendingQueue(): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :itemId")
    suspend fun deleteSyncItem(itemId: Int)

    @Query("DELETE FROM sync_queue")
    suspend fun deleteAll()
}
