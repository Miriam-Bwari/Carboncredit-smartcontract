package dev.korryr.shambaguard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.korryr.shambaguard.data.local.dao.*
import dev.korryr.shambaguard.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        FarmEntity::class,
        PolicyEntity::class,
        PayoutEntity::class,
        FarmReportEntity::class,
        SyncQueueEntity::class,
        NotificationEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class ShambaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun farmDao(): FarmDao
    abstract fun policyDao(): PolicyDao
    abstract fun payoutDao(): PayoutDao
    abstract fun farmReportDao(): FarmReportDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun notificationDao(): NotificationDao
}
