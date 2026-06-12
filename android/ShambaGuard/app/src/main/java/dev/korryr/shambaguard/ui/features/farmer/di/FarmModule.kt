package dev.korryr.shambaguard.ui.features.farmer.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.korryr.shambaguard.ui.features.farmer.data.remote.CarbonApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.PaymentApi
import dev.korryr.shambaguard.ui.features.farmer.data.repository.CarbonRepositoryImpl
import dev.korryr.shambaguard.ui.features.farmer.data.repository.FarmRepositoryImpl
import dev.korryr.shambaguard.ui.features.farmer.data.repository.PaymentRepositoryImpl
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.CarbonRepository
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.PaymentRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FarmModule {

    @Binds
    @Singleton
    abstract fun bindFarmRepository(
        farmRepositoryImpl: FarmRepositoryImpl,
    ): FarmRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindCarbonRepository(
        carbonRepositoryImpl: CarbonRepositoryImpl,
    ): CarbonRepository

    companion object {
        @Provides
        @Singleton
        fun provideFarmApi(retrofit: Retrofit): FarmApi = retrofit.create(FarmApi::class.java)

        @Provides
        @Singleton
        fun providePaymentApi(retrofit: Retrofit): PaymentApi = retrofit.create(PaymentApi::class.java)

        @Provides
        @Singleton
        fun provideCarbonApi(retrofit: Retrofit): CarbonApi = retrofit.create(CarbonApi::class.java)
    }
}
