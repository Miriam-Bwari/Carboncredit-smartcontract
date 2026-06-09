package dev.korryr.shambaguard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ShambaGuardApplication :
    Application(),
    Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Only plant debug logging tree in debug builds.
        // Release builds must never emit verbose logs — auth tokens, farm data,
        // and M-Pesa references could otherwise be exposed via logcat/ADB.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = androidx.work.PeriodicWorkRequestBuilder<dev.korryr.shambaguard.ui.features.agent.data.worker.FarmSyncWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FarmSyncWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest,
        )
    }
}
