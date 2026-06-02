package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey val payoutId: String,
    val policyId: String,
    val farmId: String,
    val amountKes: Int,
    val txHash: String,         // Polygon transaction hash
    val ipfsCid: String,        // IPFS verification report link
    val triggeredAt: Long,
    val mpesaRef: String?       // Daraja transaction reference
)
