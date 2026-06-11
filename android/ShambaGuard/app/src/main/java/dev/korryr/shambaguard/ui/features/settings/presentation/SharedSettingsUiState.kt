package dev.korryr.shambaguard.ui.features.settings.presentation

import dev.korryr.shambaguard.core.datastore.AppThemeMode

data class SharedSettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isLoggingOut: Boolean = false,
    val logoutSuccess: Boolean = false
)
