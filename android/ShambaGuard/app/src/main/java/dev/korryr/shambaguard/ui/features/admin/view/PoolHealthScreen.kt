package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

import dev.korryr.shambaguard.ui.features.admin.presentation.PoolHealthUiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PoolHealthScreen(
    uiState: PoolHealthUiState,
    onNavigateBack: () -> Unit,
) {
    val health = uiState.poolHealth
    val format = NumberFormat.getNumberInstance(Locale.US)

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Pool Health",
                onBack = onNavigateBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (uiState.isLoading) {
                CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (health != null) {
                Text("Coverage Ratio Target: ${health.targetRatio.toInt()}%", style = MaterialTheme.typography.titleMedium)

                // Current Status
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Current Ratio: ${health.ratioPercentage}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (health.status == "HEALTHY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text("Status: ${health.status}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Pool Balance")
                    Text("KES ${format.format(health.poolBalance)}", style = MaterialTheme.typography.titleMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Coverage Liability")
                    Text("KES ${format.format(health.coverageLiability)}", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "If ratio drops below ${health.targetRatio.toInt()}%, new policy issuance will automatically pause.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                Text("Failed to load pool health.", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
