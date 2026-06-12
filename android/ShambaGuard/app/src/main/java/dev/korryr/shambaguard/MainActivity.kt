package dev.korryr.shambaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.navigation.ShambaGuardNavGraph
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.theme.ShambaGuardTheme

import dev.korryr.shambaguard.core.datastore.SettingsManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var sessionManager: SessionManager

    @javax.inject.Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appThemeMode by settingsManager.appThemeFlow.collectAsStateWithLifecycle(initialValue = null)
            val appLanguage by settingsManager.appLanguageFlow.collectAsStateWithLifecycle(initialValue = null)
            val savedRole by sessionManager.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)

            if (appThemeMode == null || appLanguage == null || savedRole == null) {
                Box(modifier = Modifier.fillMaxSize())
                return@setContent
            }

            // Create a localized context for Compose
            val context = androidx.compose.ui.platform.LocalContext.current
            val localeTag = if (appLanguage == "SWA") "sw" else "en"
            val locale = java.util.Locale(localeTag)
            val configuration = android.content.res.Configuration(context.resources.configuration)
            configuration.setLocale(locale)
            val localizedContext = context.createConfigurationContext(configuration)

            // Wrap the Activity context to provide localized resources without breaking Hilt
            val wrapper = object : android.content.ContextWrapper(context) {
                override fun getResources(): android.content.res.Resources = localizedContext.resources
            }

            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalContext provides wrapper,
            ) {
                ShambaGuardTheme(appThemeMode = appThemeMode!!) {
                    val finalRole = try {
                        UserRole.valueOf(savedRole!!)
                    } catch (e: Exception) {
                        UserRole.Unauthenticated
                    }

                    ShambaGuardNavGraph(role = finalRole)
                }
            }
        }
    }
}
