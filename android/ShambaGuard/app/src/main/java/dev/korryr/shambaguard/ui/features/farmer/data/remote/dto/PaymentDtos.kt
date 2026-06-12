package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StkPushRequestDto(
    @SerializedName("farmer_id") val farmerId: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("amount_kes") val amountKes: Int,
)

data class StkPushResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("checkout_id") val checkoutId: String?,
    @SerializedName("merchant_id") val merchantId: String?,
)

data class PaymentStatusResponseDto(
    @SerializedName("checkout_id") val checkoutId: String,
    @SerializedName("status") val status: String,
    @SerializedName("amount_kes") val amountKes: Int,
    @SerializedName("mpesa_reference") val mpesaReference: String?,
)

data class CarbonCreditDto(
    @SerializedName("token_id") val tokenId: String,
    @SerializedName("farm_id") val farmId: String,
    @SerializedName("carbon_tonnes") val carbonTonnes: Double,
    @SerializedName("status") val status: String, // MINTED, RETIRED
    @SerializedName("issued_at") val issuedAt: Long,
)
