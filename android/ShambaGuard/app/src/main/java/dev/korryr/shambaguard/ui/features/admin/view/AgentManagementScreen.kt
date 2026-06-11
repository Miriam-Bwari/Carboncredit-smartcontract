package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

import androidx.compose.foundation.lazy.items
import dev.korryr.shambaguard.ui.features.admin.presentation.AgentManagementUiState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgentManagementScreen(
    uiState: AgentManagementUiState,
    onApprove: (String) -> Unit,
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

            if (uiState.isLoading) {
                item {
                    CircularWavyProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else if (uiState.pendingAgents.isEmpty()) {
                item {
                    Text("No pending agents.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(uiState.pendingAgents) { agent ->
                    AgentItemCard(
                        name = agent.fullName,
                        phone = agent.phoneNumber,
                        status = "PENDING",
                        onApprove = { onApprove(agent.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Active Agents", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Dummy Active list since backend doesn't currently return active agents in a separate endpoint yet
            items(2) { index ->
                AgentItemCard(name = "Active Agent $index", phone = "25479876543$index", status = "APPROVED", onApprove = {})
            }
        }
    }
}

@Composable
fun AgentItemCard(name: String, phone: String, status: String, onApprove: () -> Unit) {
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
                Button(onClick = onApprove) {
                    Text("Approve")
                }
            } else {
                Text(status, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
