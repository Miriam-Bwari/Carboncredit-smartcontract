package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val farmId: String,
    val farmerId: String,
    val agentId: String,
    val polygonJson: String, // Serialized GeoJSON polygon
    val areaHectares: Double,
    val region: String,
    val cropType: String,
    val tillageMethod: String,
    val treeCount: Int,
    val carbonStatus: String, // PENDING | VERIFIED | MINTED | RETIRED
    val syncStatus: String, // PENDING_SYNC | SYNCED | FAILED
    val lastSyncedAt: Long?,
    val createdAt: Long,
)
