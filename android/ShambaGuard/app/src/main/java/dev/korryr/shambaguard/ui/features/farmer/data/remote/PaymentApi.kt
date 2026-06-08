package dev.korryr.shambaguard.ui.features.farmer.data.remote

import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonCreditDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushRequestDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("api/payments/stk-push")
    suspend fun triggerStkPush(@Body request: StkPushRequestDto): StkPushResponseDto

    @GET("api/payments/status/{checkout_id}")
    suspend fun getPaymentStatus(@Path("checkout_id") checkoutId: String): dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PaymentStatusResponseDto
}

interface CarbonApi {
    @GET("api/v1/carbon/{farm_id}/credits")
    suspend fun getCarbonCredits(@Path("farm_id") farmId: String): List<CarbonCreditDto>
}
