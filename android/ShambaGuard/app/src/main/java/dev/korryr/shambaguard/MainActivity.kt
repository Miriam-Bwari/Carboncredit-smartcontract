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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShambaGuardTheme {
                val savedRole by sessionManager.userRoleFlow.collectAsState(initial = null)

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
