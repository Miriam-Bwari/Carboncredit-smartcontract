package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import dev.korryr.shambaguard.sharedComposables.ShambaPolygonMap
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.agent.presentation.AgentOnboardingUiState
import dev.korryr.shambaguard.ui.features.auth.presentation.estimatedAreaAcres

@Composable
fun MapPolygonScreen(
    uiState: AgentOnboardingUiState,
    onUpdatePolygon: (List<LatLng>) -> Unit,
    onCameraMoved: (LatLng) -> Unit,
    onToggleMapType: () -> Unit,
    onUndo: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPractices: () -> Unit,
) {
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Draw Farm Boundary",
                onBack = onNavigateBack,
            )
        },
    ) { padding ->
        val estimatedAcres = uiState.polygonPoints.estimatedAreaAcres()

        ShambaPolygonMap(
            polygonPoints = uiState.polygonPoints,
            currentRegionName = uiState.currentRegionName,
            mapType = uiState.mapType,
            estimatedAcres = estimatedAcres,
            canSave = uiState.polygonPoints.size >= 3,
            saveCtaText = "Next: Farm Practices",
            topBar = {}, // Handled by Scaffold topBar here
            onMapTapped = { latLng -> onUpdatePolygon(uiState.polygonPoints + latLng) },
            onCameraMoved = onCameraMoved,
            onUndo = onUndo,
            onToggleMapType = onToggleMapType,
            onSave = onNavigateToPractices,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}
