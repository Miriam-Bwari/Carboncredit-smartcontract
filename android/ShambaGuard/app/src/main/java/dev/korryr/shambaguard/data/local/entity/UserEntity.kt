package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val phone: String,
    val role: String,           // ADMIN | AGENT | FARMER
    val name: String,
    val nationalId: String?,    // Agents and farmers only
    val region: String?,
    val isApproved: Boolean,    // Agents require admin approval
    val createdAt: Long
)
