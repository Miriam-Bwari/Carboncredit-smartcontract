package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTextField
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.agent.presentation.AgentOnboardingUiState

@Composable
fun FarmPracticesScreen(
    uiState: AgentOnboardingUiState,
    onUpdatePractices: (String, String, Int, String) -> Unit,
    onNavigateBack: () -> Unit,
    onFinishRegistration: () -> Unit,
) {
    var treeCountString by remember(uiState.treeCount) { mutableStateOf(uiState.treeCount.toString()) }

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Farm Practices",
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
            Text("Quarterly Practice Log", style = MaterialTheme.typography.titleMedium)

            ShambaTextField(
                value = uiState.cropType,
                onValueChange = { onUpdatePractices(it, uiState.tillageMethod, uiState.treeCount, uiState.irrigationSource) },
                label = "Primary Crop Type (e.g., Maize)",
            )

            ShambaTextField(
                value = treeCountString,
                onValueChange = { 
                    treeCountString = it
                    val count = it.toIntOrNull() ?: 0
                    onUpdatePractices(uiState.cropType, uiState.tillageMethod, count, uiState.irrigationSource) 
                },
                label = "Estimated Tree Count",
            )

            // In a real app, tillage and irrigation would be dropdowns or chips.
            Text("Tillage Method & Irrigation should be selected here.", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.weight(1f))

            ShambaButton(
                text = if (uiState.isSaving) "Saving..." else "Save Farmer Profile Offline",
                onClick = onFinishRegistration,
                enabled = uiState.cropType.isNotBlank() && !uiState.isSaving,
            )
        }
    }
}
