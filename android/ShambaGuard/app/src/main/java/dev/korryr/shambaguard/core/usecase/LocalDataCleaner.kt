package dev.korryr.shambaguard.core.usecase

import dev.korryr.shambaguard.data.local.dao.FarmDao
import dev.korryr.shambaguard.data.local.dao.FarmReportDao
import dev.korryr.shambaguard.data.local.dao.PayoutDao
import dev.korryr.shambaguard.data.local.dao.PolicyDao
import dev.korryr.shambaguard.data.local.dao.SyncQueueDao
import dev.korryr.shambaguard.data.local.dao.UserDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wipes all user-specific local data from the Room database on logout,
 * preventing data bleed between different accounts on the same device.
 */
@Singleton
class LocalDataCleaner @Inject constructor(
    private val farmDao: FarmDao,
    private val userDao: UserDao,
    private val policyDao: PolicyDao,
    private val payoutDao: PayoutDao,
    private val farmReportDao: FarmReportDao,
    private val syncQueueDao: SyncQueueDao,
) {
    suspend fun clearAll() {
        farmDao.deleteAll()
        userDao.deleteAll()
        policyDao.deleteAll()
        payoutDao.deleteAll()
        farmReportDao.deleteAll()
        syncQueueDao.deleteAll()
    }
}
