package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FarmDto(
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("agent_id") val agentId: String,
    @SerializedName("polygon") val polygon: String,
    @SerializedName("area_hectares") val areaHectares: Double,
    @SerializedName("region") val region: String
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
