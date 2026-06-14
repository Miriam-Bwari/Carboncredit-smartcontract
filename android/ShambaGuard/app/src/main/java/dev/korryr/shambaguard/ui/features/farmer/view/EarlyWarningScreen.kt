package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.farmer.presentation.DroughtRisk
import dev.korryr.shambaguard.ui.features.farmer.presentation.EarlyWarningUiState
import dev.korryr.shambaguard.ui.features.farmer.presentation.RainfallDay
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.ShambaAmber

// Heat-map overlay colours only — these are data-encoding colours that intentionally
// don't follow the brand palette (red = critical drought, green = healthy vegetation).
// Everything else uses MaterialTheme.colorScheme.*
private val HeatGreen = Color(0xFF1C3A1A)
private val HeatYellow = Color(0xFFFFAA00)
private val HeatOrange = Color(0xFFFF6600)
private val HeatRed = Color(0xFFCC1100)

@Composable
fun EarlyWarningScreen(
    uiState: EarlyWarningUiState,
    onBack: () -> Unit,
    onFullAnalysis: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar
        ShambaTopBar(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            DroughtAlertBanner(uiState)
            FarmStressMapCard(
                lastUpdated = uiState.mapLastUpdated,
                polygonPoints = uiState.polygonPoints,
                currentRisk = uiState.currentRisk,
            )
            RainfallForecastSection(uiState.rainfallForecast)
            AIRecommendationCard(uiState)
            CoverageCard(uiState)
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Drought alert banner — uses error colour role from the theme so it
// automatically adapts between light (deep red) and dark (lighter red) schemes.
@Composable
private fun DroughtAlertBanner(uiState: EarlyWarningUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                // error → errorContainer gives a subtle red gradient that works
                // in both light and dark mode without any hardcoded hex values.
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.errorContainer,
                    ),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Column {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                // tertiary = amber in the ShambaGuard scheme (harvest/alert accent)
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(
                    dev.korryr.shambaguard.R.string.drought_farm_health_title,
                    uiState.farmHealthValue,
                ),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    // onError is guaranteed to be legible on the error background
                    color = MaterialTheme.colorScheme.onError,
                    lineHeight = 32.sp,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = uiState.alertBody,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onError.copy(alpha = 0.9f),
                    lineHeight = 20.sp,
                ),
            )
            Spacer(Modifier.height(14.dp))
            // AI confidence badge — primaryContainer gives the dark green pill
            // in light mode and the deep forest green in dark mode automatically.
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(
                        dev.korryr.shambaguard.R.string.drought_ai_confidence,
                        uiState.aiConfidence,
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

// Farm stress heatmap card
@Composable
private fun FarmStressMapCard(
    lastUpdated: String,
    polygonPoints: List<LatLng>,
    currentRisk: DroughtRisk,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(0.0500, 37.6494), // Default to Meru
            14f,
        )
    }

    LaunchedEffect(polygonPoints) {
        if (polygonPoints.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            polygonPoints.forEach { builder.include(it) }
            val bounds = builder.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Satellite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_farm_stress_map),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp,
                ),
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(
                        dev.korryr.shambaguard.R.string.drought_updated_label,
                        lastUpdated,
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = MapType.SATELLITE),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = false,
                    myLocationButtonEnabled = false,
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    rotationGesturesEnabled = false,
                ),
            ) {
                if (polygonPoints.isNotEmpty()) {
                    val riskColor = when (currentRisk) {
                        DroughtRisk.CRITICAL -> HeatRed.copy(alpha = 0.5f)
                        DroughtRisk.HIGH -> HeatOrange.copy(alpha = 0.5f)
                        DroughtRisk.MODERATE -> HeatYellow.copy(alpha = 0.5f)
                        DroughtRisk.LOW -> HeatGreen.copy(alpha = 0.5f)
                    }
                    Polygon(
                        points = polygonPoints,
                        fillColor = riskColor,
                        // White stroke keeps the farm boundary crisp against any map tile
                        strokeColor = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3f,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendDot(
                color = Color(0xFF2E9447),
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_legend_healthy),
            )
            LegendDot(
                color = HeatYellow,
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_legend_stressed),
            )
            LegendDot(
                color = HeatRed,
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_legend_critical),
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

// 7-day rainfall forecast horizontal strip
@Composable
private fun RainfallForecastSection(forecast: List<RainfallDay>) {
    Column {
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_7_day_forecast),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(forecast) { day -> RainfallDayCard(day) }
        }
    }
}

@Composable
private fun RainfallDayCard(day: RainfallDay) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.day,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            ),
        )
        Spacer(Modifier.height(6.dp))
        Icon(
            imageVector = if (day.hasRain) Icons.Filled.WaterDrop else Icons.Filled.WbSunny,
            contentDescription = null,
            // Rain → secondary (teal), Sun → tertiary (amber) — both theme-aware
            tint = if (day.hasRain) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${day.rainfallMm}mm",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            ),
        )
    }
}

// AI recommendation card — uses tertiaryContainer (warm amber-tinted dark in both schemes)
// instead of the hardcoded AiCardBg = Color(0xFF3D2D05)
@Composable
private fun AIRecommendationCard(uiState: EarlyWarningUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.TipsAndUpdates,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_ai_recommendation),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_ai_strategy),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 32.sp,
            ),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = uiState.aiCropBody,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                lineHeight = 22.sp,
            ),
        )
    }
}

// Coverage / policy card
@Composable
private fun CoverageCard(uiState: EarlyWarningUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_coverage_active),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_policy_will_pay),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = androidx.compose.ui.res.stringResource(
                dev.korryr.shambaguard.R.string.drought_kes_format,
                "%,d".format(uiState.payoutKes),
            ),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            ),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = uiState.payoutCondition,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            ),
        )
    }
}
