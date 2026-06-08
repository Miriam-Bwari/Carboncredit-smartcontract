package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.agent.presentation.AgentOnboardingUiState

@Composable
fun MapPolygonScreen(
    uiState: AgentOnboardingUiState,
    onUpdatePolygon: (List<LatLng>) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPractices: () -> Unit,
) {
    // Default location (e.g., Nairobi/Meru area)
    val defaultLocation = LatLng(-0.0236, 37.9062) // Meru approx
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Draw Farm Boundary",
                onBack = onNavigateBack,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    onUpdatePolygon(uiState.polygonPoints + latLng)
                },
            ) {
                if (uiState.polygonPoints.isNotEmpty()) {
                    Polygon(
                        points = uiState.polygonPoints,
                        fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        strokeColor = MaterialTheme.colorScheme.primary,
                        strokeWidth = 5f,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = { 
                            if (uiState.polygonPoints.isNotEmpty()) {
                                onUpdatePolygon(uiState.polygonPoints.dropLast(1))
                            }
                        },
                        enabled = uiState.polygonPoints.isNotEmpty(),
                    ) {
                        Text("Undo Point")
                    }
                    Button(
                        onClick = { onUpdatePolygon(emptyList()) },
                        enabled = uiState.polygonPoints.isNotEmpty(),
                    ) {
                        Text("Clear All")
                    }
                }

                ShambaButton(
                    text = "Next: Farm Practices",
                    onClick = onNavigateToPractices,
                    enabled = uiState.polygonPoints.size >= 3, // At least a triangle
                )
            }
        }
    }
}
