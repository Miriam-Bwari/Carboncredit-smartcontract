package dev.korryr.shambaguard.ui.features.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// DataStore instance — one per app, created via extension
private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "shamba_onboarding",
)

private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

// ViewModel — exposes whether onboarding has been seen
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    /** True once the user has completed or skipped onboarding. */
    val onboardingCompleted = context.onboardingDataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null, // null = loading, not yet read from disk
        )

    /** Call when the user taps "Get Started" or "Skip". */
    fun markOnboardingDone() {
        viewModelScope.launch {
            context.onboardingDataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETED] = true
            }
        }
    }
}
