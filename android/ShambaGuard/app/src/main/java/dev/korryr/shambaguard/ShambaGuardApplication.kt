package dev.korryr.shambaguard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ShambaGuardApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Only plant debug logging tree in debug builds.
        // Release builds must never emit verbose logs — auth tokens, farm data,
        // and M-Pesa references could otherwise be exposed via logcat/ADB.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
