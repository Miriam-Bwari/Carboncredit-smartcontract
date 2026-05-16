package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.features.farmer.presentation.DroughtRisk
import dev.korryr.shambaguard.ui.features.farmer.presentation.EarlyWarningUiState
import dev.korryr.shambaguard.ui.features.farmer.presentation.ForecastDay
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.Green99
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

// EarlyWarningScreen — the DROUGHT bottom-nav tab.
// Shows current 14-day forecast + crop recommendation.
// "Full Analysis" drills into DroughtInsightsScreen.
@Composable
fun EarlyWarningScreen(
    uiState:          EarlyWarningUiState,
    onFullAnalysis:   () -> Unit,
    modifier:         Modifier = Modifier,
) {
    val (riskColor, riskBg) = riskColors(uiState.currentRisk)

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            EarlyWarningHeader(
                farmName   = uiState.farmName,
                farmRegion = uiState.farmRegion,
                risk       = uiState.currentRisk,
                riskColor  = riskColor,
                riskBg     = riskBg,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 14-day forecast bar chart
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text  = stringResource(R.string.drought_forecast_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onBackground,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = stringResource(R.string.drought_forecast_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                ForecastBarChart(
                    forecast   = uiState.forecast,
                    riskColor  = riskColor,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Crop recommendation card
            CropRecommendationCard(
                crop        = uiState.recommendedCrop,
                reason      = uiState.recommendedCropReason,
                swahili     = uiState.recommendedCropSwahili,
                modifier    = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Full analysis CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Green10)
                    .clickable(onClick = onFullAnalysis)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Filled.WbSunny,
                        contentDescription = null,
                        tint               = White,
                        modifier           = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text  = stringResource(R.string.drought_full_analysis_cta),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color      = White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Header: farm name, region, big risk pill
@Composable
private fun EarlyWarningHeader(
    farmName:  String,
    farmRegion: String,
    risk:      DroughtRisk,
    riskColor: Color,
    riskBg:    Color,
    modifier:  Modifier = Modifier,
) {
    val riskLabel = when (risk) {
        DroughtRisk.CRITICAL -> "CRITICAL RISK"
        DroughtRisk.HIGH     -> "HIGH RISK"
        DroughtRisk.MODERATE -> "MODERATE RISK"
        DroughtRisk.LOW      -> "LOW RISK"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(riskBg)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text  = stringResource(R.string.drought_screen_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onBackground,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector        = Icons.Filled.LocationOn,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text  = "$farmName — $farmRegion",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big status pill
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(riskColor.copy(alpha = 0.1f))
                .border(1.dp, riskColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(riskColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.Warning,
                    contentDescription = null,
                    tint               = riskColor,
                    modifier           = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text  = riskLabel,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = riskColor,
                    ),
                )
                Text(
                    text  = stringResource(R.string.drought_alert_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

// 14-day forecast bar chart — each bar is a day's drought risk score
@Composable
private fun ForecastBarChart(
    forecast:  List<ForecastDay>,
    riskColor: Color,
    modifier:  Modifier = Modifier,
) {
    val maxBarHeight = 80.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.Bottom,
            ) {
                forecast.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier            = Modifier.weight(1f),
                    ) {
                        // Risk % label above bar
                        Text(
                            text  = "${(day.riskScore * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color      = riskColor,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 9.sp,
                            ),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Bar
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(maxBarHeight * day.riskScore)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    riskColor.copy(alpha = 0.6f + (day.riskScore * 0.4f))
                                ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Day label
                        Text(
                            text  = day.dayLabel.replace("Day ", "D"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize  = 9.sp,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(riskColor),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text  = stringResource(R.string.drought_legend_risk),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

// Crop recommendation card
@Composable
private fun CropRecommendationCard(
    crop:     String,
    reason:   String,
    swahili:  String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Green99)
            .border(1.dp, Green90, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Green95),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.Eco,
                    contentDescription = null,
                    tint               = Green40,
                    modifier           = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text  = stringResource(R.string.drought_crop_rec_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color      = Green40,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text  = crop,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text  = reason,
            style = MaterialTheme.typography.bodySmall.copy(
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Kiswahili tip row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Green95)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector        = Icons.Filled.TipsAndUpdates,
                contentDescription = null,
                tint               = ShambaAmber,
                modifier           = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text  = "\"$swahili\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    color      = Green40,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                ),
            )
        }
    }
}

// Returns (riskColor, riskBgColor) for a given DroughtRisk level
@Composable
private fun riskColors(risk: DroughtRisk): Pair<Color, Color> = when (risk) {
    DroughtRisk.CRITICAL, DroughtRisk.HIGH -> Pair(Color(0xFFB00020), Color(0xFFFFF0F0))
    DroughtRisk.MODERATE                   -> Pair(ShambaAmber,       Color(0xFFFFFBF0))
    DroughtRisk.LOW                        -> Pair(Green40,            Green99)
}
