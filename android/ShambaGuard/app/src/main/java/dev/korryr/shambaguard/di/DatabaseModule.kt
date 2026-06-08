package dev.korryr.shambaguard.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.korryr.shambaguard.data.local.ShambaDatabase
import dev.korryr.shambaguard.data.local.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShambaDatabase(
        @ApplicationContext context: Context,
    ): ShambaDatabase = Room.databaseBuilder(
        context,
        ShambaDatabase::class.java,
        "shamba_database",
    ).build()

    @Provides
    fun provideUserDao(database: ShambaDatabase): UserDao = database.userDao()

    @Provides
    fun provideFarmDao(database: ShambaDatabase): FarmDao = database.farmDao()

    @Provides
    fun providePolicyDao(database: ShambaDatabase): PolicyDao = database.policyDao()

    @Provides
    fun providePayoutDao(database: ShambaDatabase): PayoutDao = database.payoutDao()

    @Provides
    fun provideFarmReportDao(database: ShambaDatabase): FarmReportDao = database.farmReportDao()

    @Provides
    fun provideSyncQueueDao(database: ShambaDatabase): SyncQueueDao = database.syncQueueDao()
}
