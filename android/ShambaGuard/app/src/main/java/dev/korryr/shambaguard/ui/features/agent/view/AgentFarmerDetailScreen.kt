package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import dev.korryr.shambaguard.ui.features.agent.presentation.FarmerListItem
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green90

@Composable
fun AgentFarmerDetailScreen(
    farmer: FarmerListItem,
    onBack: () -> Unit,
    onLogPractice: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ShambaTopBar(title = farmer.name, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // Farmer avatar + name header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Green10),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = farmer.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Green90,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    Column {
                        Text(
                            text = farmer.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(Modifier.height(4.dp))
                        val syncColor = if (farmer.syncStatus == "SYNCED") Color(0xFF4CAF50) else Color(0xFFFF9800)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = syncColor.copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = farmer.syncStatus,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = syncColor,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            // Farm details
            SectionCard(title = "Farm Details", icon = Icons.Filled.Agriculture) {
                DetailRow("Crop Type", farmer.cropType)
                DetailRow("Farm Area", "${farmer.areaHectares} hectares")
                DetailRow(
                    "Policy Status",
                    farmer.policyStatus,
                    valueColor = if (farmer.policyStatus == "ACTIVE") {
                        Color(0xFF4CAF50)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }

            // Evidence photos notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Photo evidence capture coming soon",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Log practice button
            Button(
                onClick = onLogPractice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green10),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Green90)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Log Practice",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Green90,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = valueColor)
    }
}
