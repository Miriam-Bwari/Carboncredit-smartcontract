package dev.korryr.shambaguard.ui.features.farmer.data.repository

import dev.korryr.shambaguard.ui.features.farmer.data.remote.CarbonApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.PaymentApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonCreditDto
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushRequestDto
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.CarbonRepository
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi,
) : PaymentRepository {
    override suspend fun triggerStkPush(farmerId: String, phoneNumber: String, amountKes: Int): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.StkPushResponseDto> = try {
        val request = StkPushRequestDto(
            farmerId = farmerId,
            phoneNumber = phoneNumber,
            amountKes = amountKes,
        )
        Result.success(paymentApi.triggerStkPush(request))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPaymentStatus(checkoutId: String): Result<dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.PaymentStatusResponseDto> = try {
        Result.success(paymentApi.getPaymentStatus(checkoutId))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

class CarbonRepositoryImpl @Inject constructor(
    private val carbonApi: CarbonApi,
) : CarbonRepository {
    override suspend fun getCarbonCredits(farmId: String): Result<List<CarbonCreditDto>> = try {
        val response = carbonApi.getCarbonCredits(farmId)
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
