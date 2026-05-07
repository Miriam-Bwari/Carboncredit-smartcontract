package dev.korryr.shambaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.korryr.shambaguard.navigation.ShambaGuardNavGraph
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.ui.theme.ShambaGuardTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShambaGuardTheme {
                // TODO: Read actual role from DataStore encrypted session once Auth flow is built.
                // For now, hardcoded to Farmer to unblock feature development.
                // ShambaGuardNavGraph owns its own Scaffold — do NOT wrap it in another Scaffold here.
                ShambaGuardNavGraph(role = UserRole.Farmer)
            }
        }
    }
}