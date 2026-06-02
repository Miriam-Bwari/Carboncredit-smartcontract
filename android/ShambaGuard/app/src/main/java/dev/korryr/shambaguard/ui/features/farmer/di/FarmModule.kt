package dev.korryr.shambaguard.ui.features.farmer.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.repository.FarmRepositoryImpl
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FarmModule {

    @Binds
    @Singleton
    abstract fun bindFarmRepository(
        farmRepositoryImpl: FarmRepositoryImpl
    ): FarmRepository

    companion object {
        @Provides
        @Singleton
        fun provideFarmApi(retrofit: Retrofit): FarmApi {
            return retrofit.create(FarmApi::class.java)
        }
    }
}
