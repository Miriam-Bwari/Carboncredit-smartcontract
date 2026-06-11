package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.features.farmer.presentation.ActivityItem
import dev.korryr.shambaguard.ui.features.farmer.presentation.ActivityType
import dev.korryr.shambaguard.ui.features.farmer.presentation.DroughtRisk
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerDashboardUiState
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.Green99
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

// Farmer Dashboard — Home screen. Pure UI, no logic.
@Composable
fun FarmerDashboardScreen(
    uiState: FarmerDashboardUiState,
    onSeeInsights: () -> Unit,
    onViewPolicy: () -> Unit,
    onViewCarbon: () -> Unit,
    onRegisterFarm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // 1. App bar greeting
            DashboardTopBar(
                farmerName = uiState.farmerName,
                farmName = uiState.farmName,
                farmRegion = uiState.farmRegion,
            )

            if (uiState.hasFarm) {
                // 2. Drought alert card (main risk indicator)
                Spacer(modifier = Modifier.height(4.dp))
                DroughtAlertCard(
                    risk = uiState.droughtRisk,
                    ndviScore = uiState.ndviScore,
                    rainfallMm = uiState.rainfallMm,
                    rainfallDelta = uiState.rainfallDelta,
                    onSeeInsights = onSeeInsights,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. At a Glance — two side-by-side cards
                SectionHeading(
                    text = stringResource(R.string.dashboard_at_a_glance),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GlanceCard(
                        icon = Icons.Filled.CheckCircle,
                        iconTint = Green40,
                        iconBgColor = Green95,
                        title = stringResource(R.string.dashboard_policy_status),
                        valueLine1 = if (uiState.policyActive) {
                            stringResource(R.string.dashboard_policy_active)
                        } else {
                            stringResource(R.string.dashboard_policy_inactive)
                        },
                        valueLine1Color = if (uiState.policyActive) {
                            Green40
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        valueLine2 = stringResource(
                            R.string.dashboard_policy_expiry,
                            uiState.policyExpiry,
                        ),
                        onClick = onViewPolicy,
                        modifier = Modifier.weight(1f),
                    )
                    GlanceCard(
                        icon = Icons.Filled.Eco,
                        iconTint = Green40,
                        iconBgColor = Green95,
                        title = stringResource(R.string.dashboard_carbon),
                        valueLine1 = "${uiState.carbonTonnes} tonnes",
                        valueLine1Color = Green40,
                        valueLine2 = stringResource(R.string.dashboard_carbon_claim),
                        onClick = onViewCarbon,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 4. Recent Activity
                SectionHeading(
                    text = stringResource(R.string.dashboard_recent_activity),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    uiState.recentActivity.forEachIndexed { index, item ->
                        ActivityRow(
                            item = item,
                            isLast = index == uiState.recentActivity.lastIndex,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                // Empty State
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Green95),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Green40,
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Welcome to Shamba Guard!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        ),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "To unlock satellite drought monitoring, carbon tracking, and insurance policies, we need to know where your farm is located.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    dev.korryr.shambaguard.sharedComposables.ShambaButton(
                        text = "Register My Farm",
                        onClick = onRegisterFarm,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(
    farmerName: String,
    farmName: String,
    farmRegion: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // "Habari, Mary 👋"
            Text(
                text = buildAnnotatedString {
                    append("Habari, $farmerName ")
                    withStyle(SpanStyle(fontSize = 22.sp)) { append("👋") }
                },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
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
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "$farmName — $farmRegion",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }

        // Satellite thumbnail placeholder — green grid representing aerial farm view
        SatelliteThumbnail()
    }
}

// Satellite farm view — small rounded square with a stylised aerial look
@Composable
private fun SatelliteThumbnail(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    listOf(Green10, Green40),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Inner grid lines suggest aerial field divisions
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(White.copy(alpha = 0.25f)),
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.Terrain,
            contentDescription = stringResource(R.string.dashboard_satellite_desc),
            tint = White.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp),
        )
    }
}

// ─── Drought Alert Card ───────────────────────────────────────────────────────

private val RiskRed = Color(0xFFB00020)
private val RiskRedBg = Color(0xFFFFF0F0)
private val RiskRedContainer = Color(0xFFFFDADB)

@Composable
private fun DroughtAlertCard(
    risk: DroughtRisk,
    ndviScore: Float,
    rainfallMm: Int,
    rainfallDelta: Int,
    onSeeInsights: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate NDVI progress bar from 0 → actual on composition
    var progressVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { progressVisible = true }
    val progress by animateFloatAsState(
        targetValue = if (progressVisible) ndviScore else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 200),
        label = "NdviProgress",
    )

    val (cardBg, alertBadgeBg, progressColor) = when (risk) {
        DroughtRisk.CRITICAL, DroughtRisk.HIGH -> Triple(RiskRedBg, RiskRedContainer, RiskRed)
        DroughtRisk.MODERATE -> Triple(Color(0xFFFFF8E1), Color(0xFFFFECB3), ShambaAmber)
        DroughtRisk.LOW -> Triple(Green99, Green90, Green40)
    }
    val riskLabel = when (risk) {
        DroughtRisk.CRITICAL -> stringResource(R.string.dashboard_risk_critical)
        DroughtRisk.HIGH -> stringResource(R.string.dashboard_risk_high)
        DroughtRisk.MODERATE -> stringResource(R.string.dashboard_risk_moderate)
        DroughtRisk.LOW -> stringResource(R.string.dashboard_risk_low)
    }
    val alertLabel = when (risk) {
        DroughtRisk.CRITICAL, DroughtRisk.HIGH -> stringResource(R.string.dashboard_drought_alert)
        DroughtRisk.MODERATE -> stringResource(R.string.dashboard_drought_watch)
        DroughtRisk.LOW -> stringResource(R.string.dashboard_conditions_normal)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, progressColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        // Top row: risk badge + alert chip
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Warning icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(progressColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = progressColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = riskLabel,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = progressColor,
                ),
                modifier = Modifier.weight(1f),
            )

            // Alert badge chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(alertBadgeBg)
                    .border(1.dp, progressColor.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = alertLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics row: NDVI + Rainfall
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // NDVI score
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_ndvi_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = progressColor,
                            ),
                        ) { append(String.format("%.2f", ndviScore)) }
                        withStyle(
                            SpanStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) { append(" / 1.0") }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = progressColor,
                    trackColor = progressColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round,
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(72.dp)
                    .background(progressColor.copy(alpha = 0.2f)),
            )

            // Rainfall
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_rainfall_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) { append("$rainfallMm") }
                        withStyle(
                            SpanStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) { append(" mm") }
                    },
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$rainfallDelta% from average",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = progressColor,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "See Insights & Actions" CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(progressColor)
                .clickable(onClick = onSeeInsights)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.dashboard_insights_cta),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                ),
            )
        }
    }
}

// ─── At a Glance Card ─────────────────────────────────────────────────────────

@Composable
private fun GlanceCard(
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    title: String,
    valueLine1: String,
    valueLine1Color: Color,
    valueLine2: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Value
        Text(
            text = valueLine1,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = valueLine1Color,
            ),
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Sub-value / link
        Text(
            text = valueLine2,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Green40,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

// ─── Recent Activity ──────────────────────────────────────────────────────────

@Composable
private fun ActivityRow(
    item: ActivityItem,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val (iconVector, iconTint, iconBg) = when (item.type) {
        ActivityType.DROUGHT_ALERT -> Triple(
            Icons.Filled.Warning,
            Color(0xFFB00020),
            Color(0xFFFFDADB),
        )

        ActivityType.PAYOUT -> Triple(Icons.Filled.CheckCircle, Green40, Green95)
        ActivityType.CARBON -> Triple(Icons.Filled.Eco, Color(0xFF5D6B29), Color(0xFFE8F0C8))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Icon badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.timeAgo,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                ),
            )
        }
    }

    // Divider between items (not after last)
    if (!isLast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 48.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        )
    }
}

// ─── Section Heading ─────────────────────────────────────────────────────────

@Composable
private fun SectionHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = modifier,
    )
}
