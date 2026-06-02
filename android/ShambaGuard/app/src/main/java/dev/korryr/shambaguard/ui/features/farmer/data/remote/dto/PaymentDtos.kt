package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StkPushRequestDto(
    @SerializedName("phone") val phone: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("tier") val tier: Int
)

data class StkPushResponseDto(
    @SerializedName("checkout_request_id") val checkoutRequestId: String,
    @SerializedName("merchant_request_id") val merchantRequestId: String,
    @SerializedName("customer_message") val customerMessage: String
)

data class CarbonCreditDto(
    @SerializedName("token_id") val tokenId: String,
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("carbon_tonnes") val carbonTonnes: Double,
    @SerializedName("status") val status: String, // MINTED, RETIRED
    @SerializedName("issued_at") val issuedAt: Long
)
