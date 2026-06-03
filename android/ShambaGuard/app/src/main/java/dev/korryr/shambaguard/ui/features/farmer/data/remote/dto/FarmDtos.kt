package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FarmRegisterRequestDto(
    @SerializedName("farmer_id") val farmerId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("boundary_coords") val boundaryCoords: List<List<Double>>,
    @SerializedName("soil_type") val soilType: String,
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("county") val county: String
)

// Matches backend FarmResponse schema
data class FarmDto(
    @SerializedName("id") val farmId: String,
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("name") val name: String,
    @SerializedName("boundary_coords") val boundaryCoords: List<List<Double>>,
    @SerializedName("area_hectares") val areaHectares: Double,
    @SerializedName("soil_type") val soilType: String,
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("county") val county: String
)

data class PracticeLogDto(
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("tillage_method") val tillageMethod: String,
    @SerializedName("tree_count") val treeCount: Int,
    @SerializedName("irrigation_source") val irrigationSource: String
)

data class FarmReportDto(
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("ndvi_mean") val ndviMean: Double,
    @SerializedName("drought_score") val droughtScore: Double,
    @SerializedName("rainfall_mm") val rainfallMm: Double,
    @SerializedName("forecast_drought_prob") val forecastDroughtProb: Double
)
