package dev.korryr.shambaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                var selectedRole by remember { mutableStateOf<UserRole?>(null) }
                
                if (selectedRole == null) {
                    Scaffold { padding ->
                        Column(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Dev Environment Role Selector", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(onClick = { selectedRole = UserRole.Admin }) { Text("Log in as Admin") }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { selectedRole = UserRole.Agent }) { Text("Log in as Agent") }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { selectedRole = UserRole.Farmer }) { Text("Log in as Farmer") }
                        }
                    }
                } else {
                    ShambaGuardNavGraph(role = selectedRole!!)
                }
            }
        }
    }
}