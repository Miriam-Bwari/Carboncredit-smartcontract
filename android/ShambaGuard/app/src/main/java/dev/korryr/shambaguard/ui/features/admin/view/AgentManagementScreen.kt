package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun AgentManagementScreen(
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Manage Agents",
                onBack = onNavigateBack,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text("Pending Approvals", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Dummy List
            items(2) { index ->
                AgentItemCard(name = "Agent $index", phone = "25471234567$index", status = "PENDING")
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Active Agents", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            }

            items(5) { index ->
                AgentItemCard(name = "Active Agent $index", phone = "25479876543$index", status = "APPROVED")
            }
        }
    }
}

@Composable
fun AgentItemCard(name: String, phone: String, status: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(phone, style = MaterialTheme.typography.bodyMedium)
            }
            if (status == "PENDING") {
                Button(onClick = { /* Approve */ }) {
                    Text("Approve")
                }
            } else {
                Text(status, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
