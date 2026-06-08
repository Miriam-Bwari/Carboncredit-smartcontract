package dev.korryr.shambaguard.ui.features.farmer.domain.repository

import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonCreditDto

interface PaymentRepository {
    suspend fun triggerStkPush(farmerId: String, phoneNumber: String, amountKes: Int): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushResponseDto>
    suspend fun getPaymentStatus(checkoutId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PaymentStatusResponseDto>
}

interface CarbonRepository {
    suspend fun getCarbonCredits(farmId: String): Result<List<CarbonCreditDto>>
}
