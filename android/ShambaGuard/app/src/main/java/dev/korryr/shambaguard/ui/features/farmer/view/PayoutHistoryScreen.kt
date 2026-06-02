package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun PayoutHistoryScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Payout History",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Automated payouts are verified via satellite and logged immutably on the Polygon blockchain.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Dummy List
            items(3) { index ->
                PayoutItemCard(
                    amount = "KES 8,000",
                    date = "2026-04-1${index}T06:14:00Z",
                    txHash = "0x7a2...${index}f3",
                    ipfsCid = "QmXyZ...${index}a1"
                )
            }
        }
    }
}

@Composable
fun PayoutItemCard(amount: String, date: String, txHash: String, ipfsCid: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Drought Trigger", style = MaterialTheme.typography.titleMedium)
                Text(amount, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text("Date: $date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Divider()
            Text("Polygon Tx: $txHash", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
            Text("IPFS Report: $ipfsCid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}
