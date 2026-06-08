package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entityType: String, // FARM | PRACTICE_LOG | EVIDENCE_PHOTO
    val entityId: String,
    val payloadJson: String, // Serialized payload to POST
    val retryCount: Int = 0,
    val createdAt: Long,
)
