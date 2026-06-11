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

            if (appThemeMode == null) {
                Box(modifier = Modifier.fillMaxSize())
                return@setContent
            }

            ShambaGuardTheme(appThemeMode = appThemeMode!!) {
                val savedRole by sessionManager.userRoleFlow.collectAsStateWithLifecycle(initialValue = null)

                // Show a blank box for the split-second DataStore takes to emit its first value
                if (savedRole == null) {
                    Box(modifier = Modifier.fillMaxSize())
                    return@ShambaGuardTheme
                }

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
