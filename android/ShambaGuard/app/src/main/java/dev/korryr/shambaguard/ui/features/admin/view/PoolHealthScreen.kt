package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun PoolHealthScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Pool Health",
                onBack = onNavigateBack
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
            Text("Coverage Ratio Target: 150%", style = MaterialTheme.typography.titleMedium)
            
            // Current Status
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Ratio: 180%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Text("Status: HEALTHY", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Divider()
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Pool Balance")
                Text("KES 4,500,000", style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Coverage Liability")
                Text("KES 2,500,000", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "If ratio drops below 150%, new policy issuance will automatically pause.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
