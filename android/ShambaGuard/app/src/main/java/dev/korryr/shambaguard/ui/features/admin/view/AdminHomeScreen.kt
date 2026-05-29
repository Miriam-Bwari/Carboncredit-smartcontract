package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun AdminHomeScreen(
    onNavigateToAgents: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToPool: () -> Unit
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Admin Dashboard",
                onBack = null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AdminStatCard(title = "Total Farmers", value = "1,240")
            AdminStatCard(title = "Pool Balance (KES)", value = "4,500,000")
            AdminStatCard(title = "Active Policies", value = "850")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onNavigateToAgents, modifier = Modifier.fillMaxWidth()) {
                Text("Manage Agents (2 Pending)")
            }
            Button(onClick = onNavigateToMap, modifier = Modifier.fillMaxWidth()) {
                Text("View Farm Maps")
            }
            Button(onClick = onNavigateToPool, modifier = Modifier.fillMaxWidth()) {
                Text("Pool Health")
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
