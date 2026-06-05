package dev.korryr.shambaguard.ui.features.agent.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.korryr.shambaguard.ui.features.agent.data.remote.AgentApi
import dev.korryr.shambaguard.ui.features.agent.data.repository.AgentRepositoryImpl
import dev.korryr.shambaguard.ui.features.agent.domain.repository.AgentRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AgentModule {

    @Binds
    @Singleton
    abstract fun bindAgentRepository(
        agentRepositoryImpl: AgentRepositoryImpl
    ): AgentRepository

    companion object {
        @Provides
        @Singleton
        fun provideAgentApi(retrofit: Retrofit): AgentApi {
            return retrofit.create(AgentApi::class.java)
        }
    }
}
