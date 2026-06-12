package dev.korryr.shambaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_reports")
data class FarmReportEntity(
    @PrimaryKey val reportId: String,
    val farmId: String,
    val ndviMean: Double,
    val rainfallMm: Double,
    val droughtScore: Double,
    val forecastScore: Double,
    val riskLevel: String, // LOW | MODERATE | HIGH | CRITICAL
    val recommendation: String, // e.g., "Plant cowpeas instead of maize"
    val generatedAt: Long,
)
