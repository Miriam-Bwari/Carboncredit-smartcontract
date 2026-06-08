package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import com.google.gson.annotations.SerializedName
import dev.korryr.shambaguard.core.network.GeoJsonPolygonDto

data class FarmRegisterRequestDto(
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("name") val name: String,
    @SerializedName("boundary_coords") val boundaryCoords: GeoJsonPolygonDto,
    @SerializedName("soil_type") val soilType: String,
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("county") val county: String,
)

// Matches backend FarmResponse schema
data class FarmDto(
    @SerializedName("id") val farmId: String,
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("name") val name: String,
    @SerializedName("boundary_coords") val boundaryCoords: GeoJsonPolygonDto,
    @SerializedName("area_hectares") val areaHectares: Double,
    @SerializedName("soil_type") val soilType: String,
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("county") val county: String,
)

data class PracticeLogDto(
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("tillage_method") val tillageMethod: String,
    @SerializedName("tree_count") val treeCount: Int,
    @SerializedName("irrigation_source") val irrigationSource: String,
)

data class PracticeLogResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("tillage_method") val tillageMethod: String,
    @SerializedName("tree_count") val treeCount: Int,
    @SerializedName("irrigation_source") val irrigationSource: String,
    @SerializedName("created_at") val createdAt: String,
)

data class FarmReportDto(
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("ndvi_mean") val ndviMean: Double,
    @SerializedName("drought_score") val droughtScore: Double,
    @SerializedName("rainfall_mm") val rainfallMm: Double,
    @SerializedName("forecast_drought_prob") val forecastDroughtProb: Double,
)

data class FarmSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("area_hectares") val areaHectares: Double,
    @SerializedName("crop_type") val cropType: String,
)

data class WeatherDto(
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("rainfall_mm") val rainfallMm: Float,
    @SerializedName("rainfall_delta_percent") val rainfallDeltaPercent: Float,
)

data class PolicyDto(
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("expiry_date") val expiryDate: String?,
)

data class AdviceDto(
    @SerializedName("ndvi_score") val ndviScore: Float,
    @SerializedName("farm_health") val farmHealth: String,
    @SerializedName("recommendations") val recommendations: List<String>,
)

data class CarbonRecordDto(
    @SerializedName("date") val date: String,
    @SerializedName("ndvi") val ndvi: Float,
    @SerializedName("carbon_kg") val carbonKg: Float,
    @SerializedName("credits") val credits: Float,
    @SerializedName("verified") val verified: Boolean,
)

data class CarbonHistoryDto(
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("total_credits") val totalCredits: Float,
    @SerializedName("total_carbon_kg") val totalCarbonKg: Float,
    @SerializedName("scans") val scans: Int,
    @SerializedName("records") val records: List<CarbonRecordDto>,
)

data class FarmerDetailsDto(
    @SerializedName("id") val id: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("county") val county: String,
)
