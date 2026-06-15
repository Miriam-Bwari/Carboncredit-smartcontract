package dev.korryr.shambaguard.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    DARK,
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "shamba_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val APP_THEME = stringPreferencesKey("app_theme")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val APP_BIOMETRIC_ENABLED = booleanPreferencesKey("app_biometric_enabled")
        private val APP_PUSH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("app_push_notifications_enabled")
        private val APP_DROUGHT_ALERTS_ENABLED = booleanPreferencesKey("app_drought_alerts_enabled")
    }

    val biometricEnabledFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[APP_BIOMETRIC_ENABLED] ?: false
    }

    val pushNotificationsFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[APP_PUSH_NOTIFICATIONS_ENABLED] ?: false
    }

    val droughtAlertsFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[APP_DROUGHT_ALERTS_ENABLED] ?: false
    }

    val appThemeFlow: Flow<AppThemeMode> = context.settingsDataStore.data.map { preferences ->
        val themeName = preferences[APP_THEME] ?: AppThemeMode.SYSTEM.name
        try {
            AppThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    val appLanguageFlow: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[APP_LANGUAGE] ?: "ENG"
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_THEME] = mode.name
        }
    }

    suspend fun setLanguage(lang: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = lang
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_PUSH_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDroughtAlertsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_DROUGHT_ALERTS_ENABLED] = enabled
        }
    }
}
