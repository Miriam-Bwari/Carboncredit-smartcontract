package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "policies")
data class PolicyEntity(
    @PrimaryKey val policyId: String,
    val farmId: String,
    val farmerId: String,
    val tier: Int,              // 1, 2, or 3
    val premiumKes: Int,        // 50, 150, or 400
    val coverageKes: Int,       // 2000, 8000, or 25000
    val status: String,         // ACTIVE | EXPIRED | PENDING_PAYMENT
    val activatedAt: Long?,
    val expiresAt: Long?
)
