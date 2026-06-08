package dev.korryr.shambaguard.ui.features.auth.view

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun FarmBoundaryScreen(
    uiState: FarmBoundaryUiState,
    canSave: Boolean,
    onMapTapped: (LatLng) -> Unit,
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

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_CENTER, DEFAULT_ZOOM)
    }

    val mapProperties = remember(uiState.mapType) {
        MapProperties(
            mapType = when (uiState.mapType) {
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN -> com.google.maps.android.compose.MapType.TERRAIN
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID -> com.google.maps.android.compose.MapType.HYBRID
                else -> com.google.maps.android.compose.MapType.SATELLITE
            },
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
        )
    }

    val estimatedAcres = uiState.estimatedAreaAcres()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // Top bar with progress
            FarmBoundaryTopBar(
                currentStep = STEP2_CURRENT,
                totalSteps = STEP2_TOTAL,
                progress = progress,
                onBack = onBack,
                onToggleLayer = onToggleLayer,
            )

            // Map fills remaining space
            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    onMapClick = onMapTapped,
                ) {
                    // Draw dashed outline as we go
                    if (uiState.points.size >= 2) {
                        Polyline(
                            points = uiState.points,
                            color = PolygonGreen,
                            width = 6f,
                            pattern = listOf(Dash(20f), Gap(10f)),
                        )
                    }

                    // Filled polygon once closed (>= 3 points)
                    if (uiState.points.size >= 3) {
                        Polygon(
                            points = uiState.points,
                            strokeColor = PolygonGreen,
                            strokeWidth = 6f,
                            fillColor = PolygonFill,
                            geodesic = true,
                        )
                    }

                    // Corner markers
                    uiState.points.forEach { point ->
                        Marker(
                            state = MarkerState(position = point),
                        )
                    }
                }

                // Floating instruction card
                InstructionCard(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                )

                // Area label in the polygon centre
                if (estimatedAcres != null) {
                    AreaLabel(
                        acres = estimatedAcres,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                // Undo button
                if (uiState.points.isNotEmpty()) {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 140.dp, end = 16.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = stringResource(R.string.farm_step2_undo),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // Hint shown until 3 points are placed
                if (uiState.points.size < 3) {
                    Text(
                        text = stringResource(R.string.farm_step2_min_points_hint),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 140.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(50),
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }

            // Bottom confirm sheet
            ConfirmBoundarySheet(
                estimatedAcres = estimatedAcres,
                canSave = canSave,
                onSave = onSave,
            )
        }
    }
}

// Floating card that explains what the user should do on the map
@Composable
private fun InstructionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(R.string.farm_step2_instruction_title),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(
                    text = stringResource(R.string.farm_step2_instruction_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

// Green pill label shown at the polygon centre with the estimated acreage
@Composable
private fun AreaLabel(acres: Double, modifier: Modifier = Modifier) {
    val formatted = stringResource(R.string.farm_step2_area_acres_format, acres)
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(50))
            .background(PolygonGreen, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "▲ $formatted",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

// Bottom white sheet with confirm info and save CTA
@Composable
private fun ConfirmBoundarySheet(
    estimatedAcres: Double?,
    canSave: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                // Left: heading + district
                Column {
                    Text(
                        text = stringResource(R.string.farm_step2_confirm_heading),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nyeri South District",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }

                // Right: estimated size
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.farm_step2_estimated_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                    Text(
                        text = if (estimatedAcres != null) {
                            stringResource(R.string.farm_step2_area_acres_format, estimatedAcres)
                        } else {
                            "— ac"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ShambaButton(
                text = stringResource(R.string.farm_step2_save_cta),
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
        }
    }
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
