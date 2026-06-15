package dev.korryr.shambaguard.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.korryr.shambaguard.ui.features.admin.data.repository.NetworkAdminRepository
import dev.korryr.shambaguard.ui.features.admin.domain.repository.AdminRepository
import dev.korryr.shambaguard.ui.features.notifications.data.repository.NotificationRepositoryImpl
import dev.korryr.shambaguard.ui.features.notifications.domain.repository.NotificationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        networkAdminRepository: NetworkAdminRepository,
    ): AdminRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
