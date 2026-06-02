package dev.korryr.shambaguard.ui.features.farmer.data.remote

import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FarmReportDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PracticeLogDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FarmApi {
    @POST("api/v1/farms")
    suspend fun registerFarm(@Body farm: FarmDto): FarmDto

    @GET("api/v1/farms/{farm_id}")
    suspend fun getFarm(@Path("farm_id") farmId: String): FarmDto

    @GET("api/v1/farms/{farm_id}/report")
    suspend fun getFarmReport(@Path("farm_id") farmId: String): FarmReportDto

    @PUT("api/v1/farms/{farm_id}/practices")
    suspend fun updatePractices(
        @Path("farm_id") farmId: String, 
        @Body practices: PracticeLogDto
    )
}
