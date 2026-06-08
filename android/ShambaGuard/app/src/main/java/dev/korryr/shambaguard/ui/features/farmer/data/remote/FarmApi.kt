package dev.korryr.shambaguard.ui.features.farmer.data.remote

import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmRegisterRequestDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmReportDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FarmApi {
    @POST("api/farms/register")
    suspend fun registerFarm(@Body farm: FarmRegisterRequestDto)

    @GET("api/v1/farms/{farm_id}")
    suspend fun getFarm(@Path("farm_id") farmId: String): FarmDto

    @GET("api/v1/farms/{farm_id}/report")
    suspend fun getFarmReport(@Path("farm_id") farmId: String): FarmReportDto

    @POST("api/farms/{farm_id}/practices")
    suspend fun addPractice(
        @Path("farm_id") farmId: String,
        @Body practice: dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto,
    ): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto

    @GET("api/farms/{farm_id}/practices")
    suspend fun getPractices(
        @Path("farm_id") farmId: String,
    ): List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogResponseDto>

    @GET("api/farmers/{farmer_id}")
    suspend fun getFarmer(@Path("farmer_id") farmerId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmerDetailsDto

    @GET("api/farms/farmer/{farmer_id}")
    suspend fun getFarmerFarms(@Path("farmer_id") farmerId: String): List<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmSummaryDto>

    @GET("api/carbon/history/{farm_id}")
    suspend fun getCarbonHistory(@Path("farm_id") farmId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonHistoryDto

    @GET("api/advice/{farm_id}")
    suspend fun getAdvice(@Path("farm_id") farmId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.AdviceDto

    @GET("api/weather/{farm_id}")
    suspend fun getWeather(@Path("farm_id") farmId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.WeatherDto

    @GET("api/payments/policy/{farmer_id}")
    suspend fun getPolicy(@Path("farmer_id") farmerId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PolicyDto
}
