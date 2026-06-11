package dev.korryr.shambaguard.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "shamba_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val APP_THEME = stringPreferencesKey("app_theme")
    }

    val appThemeFlow: Flow<AppThemeMode> = context.settingsDataStore.data.map { preferences ->
        val themeName = preferences[APP_THEME] ?: AppThemeMode.SYSTEM.name
        try {
            AppThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_THEME] = mode.name
        }
    }
}
