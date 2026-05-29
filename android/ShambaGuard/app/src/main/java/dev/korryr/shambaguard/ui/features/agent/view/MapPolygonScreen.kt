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

@Composable
fun MapPolygonScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEvidence: () -> Unit
) {
    // Default location (e.g., Nairobi/Meru area)
    val defaultLocation = LatLng(-0.0236, 37.9062) // Meru approx
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    
    val polygonPoints = remember { mutableStateListOf<LatLng>() }

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Draw Farm Boundary",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    polygonPoints.add(latLng)
                }
            ) {
                if (polygonPoints.isNotEmpty()) {
                    Polygon(
                        points = polygonPoints.toList(),
                        fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        strokeColor = MaterialTheme.colorScheme.primary,
                        strokeWidth = 5f
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (polygonPoints.isNotEmpty()) polygonPoints.removeLast() },
                        enabled = polygonPoints.isNotEmpty()
                    ) {
                        Text("Undo Point")
                    }
                    Button(
                        onClick = { polygonPoints.clear() },
                        enabled = polygonPoints.isNotEmpty()
                    ) {
                        Text("Clear All")
                    }
                }
                
                ShambaButton(
                    text = "Save & Capture Evidence",
                    onClick = onNavigateToEvidence,
                    enabled = polygonPoints.size >= 3 // At least a triangle
                )
            }
        }
    }
}
