package dev.korryr.shambaguard.ui.features.admin.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun FarmMapScreen(
    onNavigateBack: () -> Unit
) {
    val kenyaCenter = LatLng(0.0236, 37.9062)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kenyaCenter, 6f)
    }

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Farm Map (NDVI Overlay)",
                onBack = onNavigateBack
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
                cameraPositionState = cameraPositionState
            ) {
                // In a real implementation, we would iterate through all farms 
                // and draw polygons with colors based on their NDVI/Drought status
            }
        }
    }
}
