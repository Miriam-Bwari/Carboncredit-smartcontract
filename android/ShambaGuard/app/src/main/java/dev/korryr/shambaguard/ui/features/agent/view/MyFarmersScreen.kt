package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.ui.features.agent.presentation.FarmerListItem
import dev.korryr.shambaguard.ui.features.agent.presentation.MyFarmersUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyFarmersScreen(
    uiState: MyFarmersUiState,
    onRegisterNewFarmer: () -> Unit,
    onLogPractices: (String) -> Unit,
    onAddEvidence: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRegisterNewFarmer,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Register Farmer")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            } else if (uiState.farmers.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No farmers registered yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "My Registered Farmers",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.farmers) { farmer ->
                        FarmerCard(
                            farmer = farmer,
                            onLogPractices = { onLogPractices(farmer.farmId) },
                            onAddEvidence = { onAddEvidence(farmer.farmId) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.padding(40.dp)) // space for FAB
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerCard(
    farmer: FarmerListItem,
    onLogPractices: () -> Unit,
    onAddEvidence: () -> Unit
) {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateStr = formatter.format(Date(farmer.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = farmer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                val syncColor = if (farmer.syncStatus == "SYNCED") Color(0xFF4CAF50) else Color(0xFFFF9800)
                Text(
                    text = farmer.syncStatus,
                    style = MaterialTheme.typography.labelSmall,
                    color = syncColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = "Policy: ${farmer.policyStatus}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (farmer.policyStatus == "ACTIVE") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.padding(2.dp))
            
            Text(
                text = "Crop: ${farmer.cropType} • ${farmer.areaHectares} ha",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Registered: $dateStr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onLogPractices) {
                    Text("Log Practice")
                }
                TextButton(onClick = onAddEvidence) {
                    Text("Add Evidence")
                }
            }
        }
    }
}
