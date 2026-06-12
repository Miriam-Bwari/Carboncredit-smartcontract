package dev.korryr.shambaguard.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.korryr.shambaguard.data.local.dao.SyncQueueDao
import timber.log.Timber

@HiltWorker
class FarmSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncQueueDao: SyncQueueDao,
    // TODO: Add SyncRepository here when created
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val pendingItems = syncQueueDao.getPendingQueue()

            if (pendingItems.isEmpty()) {
                Timber.d("No pending items to sync.")
                return Result.success()
            }

            pendingItems.forEach { item ->
                Timber.d("Syncing item: ${item.entityType}")
                // when (item.entityType) {
                //     "FARM" -> syncRepository.postFarm(item)
                //     "PRACTICE_LOG" -> syncRepository.postPractices(item)
                //     "EVIDENCE_PHOTO" -> syncRepository.uploadPhoto(item)
                // }

                // On success:
                syncQueueDao.deleteSyncItem(item.id)
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during sync")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
