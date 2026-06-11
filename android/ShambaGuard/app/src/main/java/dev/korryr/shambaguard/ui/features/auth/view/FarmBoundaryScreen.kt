package dev.korryr.shambaguard.ui.features.auth.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmBoundaryUiState
import dev.korryr.shambaguard.ui.features.auth.presentation.estimatedAreaAcres

// Step 2 of 3 — farm polygon drawing screen. Pure UI, no logic.

private const val STEP2_CURRENT = 2
private const val STEP2_TOTAL = 3

// Default map center: Nyeri, Kenya
private val DEFAULT_CENTER = LatLng(-0.4167, 36.9500)
private const val DEFAULT_ZOOM = 15f

// Brand green used on the polygon overlay
private val PolygonGreen = Color(0xFF2E9647)
private val PolygonFill = Color(0x332E9647)

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun FarmBoundaryScreen(
    uiState: FarmBoundaryUiState,
    canSave: Boolean,
    onMapTapped: (LatLng) -> Unit,
    onCameraMoved: (LatLng) -> Unit,
    onUndo: () -> Unit,
    onToggleLayer: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = STEP2_CURRENT.toFloat() / STEP2_TOTAL.toFloat(),
        animationSpec = tween(600),
        label = "Step2Progress",
    )

    val estimatedAcres = uiState.points.estimatedAreaAcres()

    dev.korryr.shambaguard.sharedComposables.ShambaPolygonMap(
        polygonPoints = uiState.points,
        currentRegionName = uiState.currentRegionName,
        mapType = uiState.mapType,
        estimatedAcres = estimatedAcres,
        canSave = canSave,
        saveCtaText = stringResource(R.string.farm_step2_save_cta),
        topBar = {
            FarmBoundaryTopBar(
                currentStep = STEP2_CURRENT,
                totalSteps = STEP2_TOTAL,
                progress = progress,
                onBack = onBack,
                onToggleLayer = onToggleLayer,
            )
        },
        onMapTapped = onMapTapped,
        onCameraMoved = onCameraMoved,
        onUndo = onUndo,
        onToggleMapType = onToggleLayer,
        onSave = onSave,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    )
}



// Top bar reused from Step 1 pattern — back, step label, progress bar, layer toggle
@Composable
private fun FarmBoundaryTopBar(
    currentStep: Int,
    totalSteps: Int,
    progress: Float,
    onBack: () -> Unit,
    onToggleLayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back arrow
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.reg_back_content_description),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            // Centred step label
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.reg_step_indicator, currentStep, totalSteps),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }

            // Layer toggle button
            IconButton(onClick = onToggleLayer) {
                Icon(
                    imageVector = Icons.Filled.Layers,
                    contentDescription = stringResource(R.string.farm_step2_layer_toggle),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
        )
    }
}
