package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Teal40

import dev.korryr.shambaguard.ui.features.admin.presentation.AdminHomeUiState

@Composable
fun AdminHomeScreen(
    uiState: AdminHomeUiState,
    onNavigateToAgents: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToPool: () -> Unit,
) {
    val stats = uiState.stats

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Admin Dashboard",
                onBack = null,
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Platform Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Stat Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                item {
                    AdminStatCard(
                        title = "Total Farmers",
                        value = stats?.totalFarmers?.toString() ?: "0",
                        icon = Icons.Filled.Groups,
                        color = Green40
                    )
                }
                item {
                    AdminStatCard(
                        title = "Active Policies",
                        value = stats?.activePolicies?.toString() ?: "0",
                        icon = Icons.Filled.VerifiedUser,
                        color = Teal40
                    )
                }
                item {
                    val poolKes = stats?.poolBalanceKes ?: 0.0
                    val formattedPool = if (poolKes >= 1_000_000) String.format("%.1fM", poolKes / 1_000_000) else poolKes.toInt().toString()
                    AdminStatCard(
                        title = "Pool Balance (KES)",
                        value = formattedPool,
                        icon = Icons.Filled.AccountBalanceWallet,
                        color = Color(0xFFE6A800)
                    )
                }
                item {
                    AdminStatCard(
                        title = "Pending Agents",
                        value = stats?.pendingAgents?.toString() ?: "0",
                        icon = Icons.Filled.PersonAdd,
                        color = Color(0xFFD32F2F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AdminActionCard(
                title = "Manage Field Agents",
                subtitle = "Approve or suspend agent accounts",
                icon = Icons.Filled.PersonAdd,
                onClick = onNavigateToAgents
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AdminActionCard(
                title = "View Farm Maps",
                subtitle = "Monitor polygons and NDVI heatmaps",
                icon = Icons.Filled.Map,
                onClick = onNavigateToMap
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AdminActionCard(
                title = "Pool Health Monitor",
                subtitle = "Check coverage ratio and liabilities",
                icon = Icons.Filled.AccountBalanceWallet,
                onClick = onNavigateToPool
            )
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AdminActionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
