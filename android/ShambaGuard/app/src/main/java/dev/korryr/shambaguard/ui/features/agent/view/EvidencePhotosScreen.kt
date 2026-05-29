package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun EvidencePhotosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPractices: () -> Unit
) {
    // Placeholder for CameraX Preview
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Capture Evidence",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CameraX Preview Area",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Requires physical device to capture photos and embed EXIF GPS data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            ShambaButton(
                text = "Next: Farm Practices",
                onClick = onNavigateToPractices
            )
        }
    }
}
