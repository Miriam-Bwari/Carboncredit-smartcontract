package dev.korryr.shambaguard.ui.features.farmer.domain.repository

import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.CarbonCreditDto

interface PaymentRepository {
    suspend fun payPremium(phone: String, amount: Int, farmId: String, tier: Int): Result<String>
}

interface CarbonRepository {
    suspend fun getCarbonCredits(farmId: String): Result<List<CarbonCreditDto>>
}
