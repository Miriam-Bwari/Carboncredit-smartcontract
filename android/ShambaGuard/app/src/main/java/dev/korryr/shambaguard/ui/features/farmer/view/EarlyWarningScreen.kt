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
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.farmer.presentation.EarlyWarningUiState
import dev.korryr.shambaguard.ui.features.farmer.presentation.RainfallDay
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

// Specific colors for this screen
private val AlertRedStart = Color(0xFFB5182A)
private val AlertRedEnd = Color(0xFFCC3045)
private val AiCardBg = Color(0xFF3D2D05)
private val ConfidenceBadge = Color(0xFF1A3A12)
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
            FarmStressMapCard(lastUpdated = uiState.mapLastUpdated)
            RainfallForecastSection(uiState.rainfallForecast)
            AIRecommendationCard(uiState)
            CoverageCard(uiState)
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Red gradient alert banner
@Composable
private fun DroughtAlertBanner(uiState: EarlyWarningUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(listOf(AlertRedStart, AlertRedEnd)),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Column {
            // Warning icon
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = ShambaAmber,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.height(8.dp))
            // Bilingual title
            Text(
                text = "${uiState.alertTitleSwahili}\n${uiState.alertTitleEnglish}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    lineHeight = 32.sp,
                ),
            )
            Spacer(Modifier.height(8.dp))
            // Body
            Text(
                text = uiState.alertBody,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = White.copy(alpha = 0.9f),
                    lineHeight = 20.sp,
                ),
            )
            Spacer(Modifier.height(14.dp))
            // Confidence badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ConfidenceBadge)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "MVUA AI: ${uiState.aiConfidence}% confident",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

// Farm stress heatmap card
@Composable
private fun FarmStressMapCard(lastUpdated: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
    ) {
        // Header row
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
                text = "Farm Stress\nMap",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp,
                ),
                modifier = Modifier.weight(1f),
            )
            // "Updated Today" chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Updated\n$lastUpdated",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Heatmap canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp),
        ) {
            // Base — dark forest green
            drawRect(HeatGreen)
            drawRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF1E4020), Color(0xFF264A25), Color(0xFF1C3A1A)),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                ),
            )

            // Stressed blob 1 — yellow-orange (left side, matches mockup)
            val b1 = Offset(size.width * 0.22f, size.height * 0.52f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HeatYellow.copy(alpha = 0.85f), HeatOrange.copy(alpha = 0.5f), Color.Transparent),
                    center = b1,
                    radius = size.minDimension * 0.38f,
                ),
                radius = size.minDimension * 0.38f,
                center = b1,
            )

            // Critical blob — red (center, matching mockup)
            val b2 = Offset(size.width * 0.46f, size.height * 0.46f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HeatRed.copy(alpha = 0.9f), HeatOrange.copy(alpha = 0.4f), Color.Transparent),
                    center = b2,
                    radius = size.minDimension * 0.28f,
                ),
                radius = size.minDimension * 0.28f,
                center = b2,
            )

            // Smaller stressed blob — upper right area
            val b3 = Offset(size.width * 0.72f, size.height * 0.28f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HeatYellow.copy(alpha = 0.6f), Color.Transparent),
                    center = b3,
                    radius = size.minDimension * 0.18f,
                ),
                radius = size.minDimension * 0.18f,
                center = b3,
            )

            // Grid lines
            val gridColor = White.copy(alpha = 0.12f)
            val gridStroke = 1f
            for (i in 0..5) {
                val x = size.width * i / 5f
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), gridStroke)
            }
            for (i in 0..4) {
                val y = size.height * i / 4f
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), gridStroke)
            }

            // Crosshair — vertical (right side)
            val crossX = size.width * 0.74f
            drawLine(White.copy(alpha = 0.65f), Offset(crossX, 0f), Offset(crossX, size.height), strokeWidth = 1.5f, cap = StrokeCap.Round)
            // Crosshair — horizontal
            val crossY = size.height * 0.50f
            drawLine(White.copy(alpha = 0.65f), Offset(0f, crossY), Offset(size.width, crossY), strokeWidth = 1.5f, cap = StrokeCap.Round)
        }

        // Legend row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendDot(color = Color(0xFF2E9447), label = "Healthy")
            LegendDot(color = HeatYellow, label = "Stressed")
            LegendDot(color = HeatRed, label = "Critical")
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
            text = "7-Day Rainfall Forecast",
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
            tint = if (day.hasRain) Color(0xFF3A7BD5) else ShambaAmber,
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

// Dark olive AI recommendation card
@Composable
private fun AIRecommendationCard(uiState: EarlyWarningUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AiCardBg)
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        // "AI RECOMMENDATION" label
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.TipsAndUpdates,
                contentDescription = null,
                tint = ShambaAmber,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "AI RECOMMENDATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ShambaAmber,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
            )
        }
        Spacer(Modifier.height(10.dp))
        // Crop title
        Text(
            text = uiState.aiCropTitle,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = White,
                lineHeight = 32.sp,
            ),
        )
        Spacer(Modifier.height(10.dp))
        // Body
        Text(
            text = uiState.aiCropBody,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = White.copy(alpha = 0.85f),
                lineHeight = 22.sp,
            ),
        )
    }
}

// White coverage / policy card
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
        // Green shield icon in green circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Green90),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = Green40,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        // "COVERAGE ACTIVE"
        Text(
            text = "COVERAGE ACTIVE",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your policy will pay",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Spacer(Modifier.height(6.dp))
        // KES amount
        Text(
            text = "KES ${"%,d".format(uiState.payoutKes)}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Green40,
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
