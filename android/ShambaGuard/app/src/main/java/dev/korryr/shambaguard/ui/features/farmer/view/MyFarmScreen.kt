package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaButtonType
import dev.korryr.shambaguard.sharedComposables.ShambaTextField
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmPractice
import dev.korryr.shambaguard.ui.features.farmer.presentation.MyFarmUiState
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

private val SoilBrown = Color(0xFF7B5800)
private val SoilBrownBg = Color(0xFFFFF0CC)
private val CarbonBadgeBg = Color(0xFFFFF0CC)
private val CarbonBadgeFg = Color(0xFF5C4000)

@Composable
fun MyFarmScreen(
    uiState: MyFarmUiState,
    onBack: () -> Unit,
    onAddPractice: () -> Unit,
    onSubmitPractice: (tillage: String, trees: String, irrigation: String) -> Unit,
    onDismissDialog: () -> Unit,
    onViewOnMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background),
        ) {
            ShambaTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                FarmMapCard(uiState, onViewOnMap)
                LandHealthSection(uiState)
                PracticeLogSection(uiState.practices)
                Spacer(Modifier.height(72.dp)) // space for FAB
            }
        }

        // Floating Action Button — dark green rounded square with text
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Green10)
                .clickable(onClick = onAddPractice)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_add_practice),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = White,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }

    if (uiState.showAddPracticeDialog) {
        AddPracticeDialog(
            isSubmitting = uiState.isSubmittingPractice,
            onSubmit = onSubmitPractice,
            onDismiss = onDismissDialog,
        )
    }
}

// Farm map card with Canvas terrain placeholder + plot info row
@Composable
private fun FarmMapCard(uiState: MyFarmUiState, onViewOnMap: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
    ) {
        // Map with farm polygon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        ) {
            val cameraPositionState = rememberCameraPositionState()
            var mapLoaded by remember { mutableStateOf(false) }

            // Automatically frame the polygon when the coords are loaded
            LaunchedEffect(uiState.polygonCoords, mapLoaded) {
                if (uiState.polygonCoords.isNotEmpty() && mapLoaded) {
                    val boundsBuilder = LatLngBounds.Builder()
                    uiState.polygonCoords.forEach { boundsBuilder.include(it) }
                    try {
                        val bounds = boundsBuilder.build()
                        // 50px padding around the bounding box
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                    } catch (e: Exception) {
                        // ignore if building bounds fails (e.g. less than 2 points)
                    }
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = MapType.SATELLITE),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = false, // Static preview map
                    zoomGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    rotationGesturesEnabled = false,
                ),
                onMapLoaded = { mapLoaded = true },
            ) {
                if (uiState.polygonCoords.isNotEmpty()) {
                    Polygon(
                        points = uiState.polygonCoords,
                        fillColor = Color(0xFF4CAF50).copy(alpha = 0.4f),
                        strokeColor = Color(0xFF4CAF50),
                        strokeWidth = 5f,
                    )
                }
            }

            // "Plot Alpha" badge overlay bottom-left
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Terrain,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = uiState.plotName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        // Plot info row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_acres_format, uiState.farmAcres),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(
                    text = uiState.activeCrop,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
            // View on map icon button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Green95),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_view_on_map),
                    tint = Green40,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

// Land health — two gauge cards + soil carbon bar
@Composable
private fun LandHealthSection(uiState: MyFarmUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_land_health),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        // Two gauge cards side by side
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // NDVI gauge
            GaugeCard(
                modifier = Modifier.weight(1f),
                value = uiState.ndviScore,
                maxValue = 1.0f,
                displayText = String.format("%.1f", uiState.ndviScore),
                arcColor = Green40,
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_ndvi_score),
                status = uiState.ndviStatus,
                statusColor = Green40,
            )
            // Veg Cover gauge
            GaugeCard(
                modifier = Modifier.weight(1f),
                value = uiState.vegCoverPercent / 100f,
                maxValue = 1.0f,
                displayText = "${uiState.vegCoverPercent.toInt()}%",
                arcColor = Color(0xFFD32F2F),
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_veg_cover),
                status = uiState.vegCoverStatus,
                statusColor = MaterialTheme.colorScheme.onSurface,
            )
        }
        // Soil carbon progress card
        SoilCarbonCard(uiState)
    }
}

// Circular arc gauge card
@Composable
private fun GaugeCard(
    modifier: Modifier,
    value: Float,
    maxValue: Float,
    displayText: String,
    arcColor: Color,
    label: String,
    status: String,
    statusColor: Color,
) {
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Canvas(modifier = Modifier.size(90.dp)) {
            val stroke = 10.dp.toPx()
            val padding = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val arcTopLeft = Offset(padding, padding)
            val startAngle = 135f
            val totalSweep = 270f
            val valueSweep = totalSweep * (value / maxValue).coerceIn(0f, 1f)

            // Background track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // Value arc
            if (valueSweep > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = startAngle,
                    sweepAngle = valueSweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
            // Center text
            val layout = textMeasurer.measure(
                text = displayText,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = arcColor,
                ),
            )
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    x = center.x - layout.size.width / 2f,
                    y = center.y - layout.size.height / 2f,
                ),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = statusColor,
            ),
        )
    }
}

// Soil carbon level card with progress bar
@Composable
private fun SoilCarbonCard(uiState: MyFarmUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SoilBrownBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = null,
                    tint = SoilBrown,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_soil_carbon_level),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Text(
                text = "${uiState.soilCarbonPercent}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = SoilBrown,
                ),
            )
        }
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((uiState.soilCarbonPercent / uiState.soilCarbonMax).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoilBrown),
            )
        }
        Text(
            text = uiState.soilCarbonChange,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Green40,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

// Practice log timeline
@Composable
private fun PracticeLogSection(practices: List<FarmPractice>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_practice_log),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Column {
            practices.forEachIndexed { index, practice ->
                PracticeRow(
                    practice = practice,
                    isFirst = index == 0,
                    isLast = index == practices.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun PracticeRow(
    practice: FarmPractice,
    isFirst: Boolean,
    isLast: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline column: dot + line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp),
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isFirst) Green40 else MaterialTheme.colorScheme.outlineVariant),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Practice card
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (practice.hasImage) {
                            Color(0xFF2D1B00)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (practice.hasImage) {
                    // Compost thumbnail placeholder — dark soil look
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(Color(0xFF1A0F00))
                        drawCircle(Color(0xFF3D2000).copy(alpha = 0.8f), radius = size.minDimension * 0.3f, center = Offset(size.width * 0.4f, size.height * 0.5f))
                        drawCircle(Color(0xFF5C3000).copy(alpha = 0.6f), radius = size.minDimension * 0.2f, center = Offset(size.width * 0.65f, size.height * 0.35f))
                    }
                    Icon(
                        imageVector = Icons.Filled.Compost,
                        contentDescription = null,
                        tint = ShambaAmber.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Grass,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = practice.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = practice.date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                Spacer(Modifier.height(6.dp))
                // Carbon badge — amber/brown pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CarbonBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = CarbonBadgeFg,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = practice.carbonBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CarbonBadgeFg,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun AddPracticeDialog(
    isSubmitting: Boolean,
    onSubmit: (tillage: String, trees: String, irrigation: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var tillageMethod by remember { mutableStateOf("") }
    var treeCount by remember { mutableStateOf("") }
    var irrigationSource by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_log_new_practice), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ShambaTextField(
                    value = tillageMethod,
                    onValueChange = { tillageMethod = it },
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_tillage_label),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                ShambaTextField(
                    value = treeCount,
                    onValueChange = { treeCount = it },
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_trees_label),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                ShambaTextField(
                    value = irrigationSource,
                    onValueChange = { irrigationSource = it },
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_irrigation_label),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            if (isSubmitting) {
                CircularWavyProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
            } else {
                ShambaButton(
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_submit),
                    onClick = { onSubmit(tillageMethod, treeCount, irrigationSource) },
                    enabled = tillageMethod.isNotBlank(),
                )
            }
        },
        dismissButton = {
            ShambaButton(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.my_farm_cancel),
                onClick = onDismiss,
                enabled = !isSubmitting,
                type = ShambaButtonType.Text,
            )
        },
    )
}
