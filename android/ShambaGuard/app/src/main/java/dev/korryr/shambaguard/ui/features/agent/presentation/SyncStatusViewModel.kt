package dev.korryr.shambaguard.ui.features.agent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.data.local.dao.FarmDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SyncStatusUiState(
    val pendingFarmsCount: Int = 0,
    val pendingPhotosCount: Int = 0
)

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val farmDao: FarmDao,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    // Observe the database reactively. Whenever a farm is added or updated, this recalculates.
    val uiState: StateFlow<SyncStatusUiState> = farmDao.getAllFarms()
        .map { farms ->
            val pendingFarms = farms.count { it.syncStatus == "PENDING_SYNC" }
            SyncStatusUiState(
                pendingFarmsCount = pendingFarms,
                pendingPhotosCount = 0 // Photos placeholder for now
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyncStatusUiState()
        )

    fun forceSync() {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<dev.korryr.shambaguard.ui.features.agent.data.worker.FarmSyncWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
    }
}
