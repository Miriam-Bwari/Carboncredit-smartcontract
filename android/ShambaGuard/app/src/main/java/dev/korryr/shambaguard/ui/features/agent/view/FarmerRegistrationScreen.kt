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
fun FarmerRegistrationScreen(
    uiState: AgentOnboardingUiState,
    onUpdateDetails: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Farmer Details",
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
            ShambaTextField(
                value = uiState.name,
                onValueChange = { onUpdateDetails(it, uiState.nationalId, uiState.phone) },
                label = "Full Name",
            )

            ShambaTextField(
                value = uiState.nationalId,
                onValueChange = { onUpdateDetails(uiState.name, it, uiState.phone) },
                label = "National ID",
            )

            ShambaTextField(
                value = uiState.phone,
                onValueChange = { onUpdateDetails(uiState.name, uiState.nationalId, it) },
                label = "Phone Number (e.g., 2547...)",
            )

            Spacer(modifier = Modifier.weight(1f))

            ShambaButton(
                text = "Next: Map Farm Polygon",
                onClick = onNavigateToMap,
                enabled = uiState.name.isNotBlank() && uiState.nationalId.isNotBlank() && uiState.phone.isNotBlank(),
            )
        }
    }
}
