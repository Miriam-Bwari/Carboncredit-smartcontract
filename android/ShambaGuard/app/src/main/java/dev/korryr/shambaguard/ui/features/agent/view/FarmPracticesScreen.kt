package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTextField
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun FarmPracticesScreen(
    onNavigateBack: () -> Unit,
    onFinishRegistration: () -> Unit,
) {
    var cropType by remember { mutableStateOf("") }
    var treeCount by remember { mutableStateOf("") }

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
                value = cropType,
                onValueChange = { cropType = it },
                label = "Primary Crop Type (e.g., Maize)",
            )

            ShambaTextField(
                value = treeCount,
                onValueChange = { treeCount = it },
                label = "Estimated Tree Count",
            )

            // In a real app, tillage and irrigation would be dropdowns or chips.
            Text("Tillage Method & Irrigation should be selected here.", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.weight(1f))

            ShambaButton(
                text = "Complete Registration & Queue Sync",
                onClick = onFinishRegistration,
                enabled = cropType.isNotBlank() && treeCount.isNotBlank(),
            )
        }
    }
}
