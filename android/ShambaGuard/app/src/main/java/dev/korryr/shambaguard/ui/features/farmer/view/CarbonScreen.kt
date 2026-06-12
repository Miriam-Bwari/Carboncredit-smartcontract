package dev.korryr.shambaguard.ui.features.farmer.view

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.farmer.presentation.CarbonEarning
import dev.korryr.shambaguard.ui.features.farmer.presentation.CarbonUiState
import dev.korryr.shambaguard.ui.features.farmer.presentation.PipelineStatus
import dev.korryr.shambaguard.ui.features.farmer.presentation.PipelineStep
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

private val SellButtonBg = Color(0xFFFDF0D5)
private val ImpactTreeBg = Color(0xFFE8F5EA)
private val ImpactCarBg = Color(0xFFFFEBEE)
private val ImpactCarRed = Color(0xFFD32F2F)
private val EarningCircle = Color(0xFFE0E0E0)
private val CompletedBg = Color(0xFFE8F5EA)
private val CompletedText = Color(0xFF2E7D32)

@Composable
fun CarbonScreen(
    uiState: CarbonUiState,
    onBack: () -> Unit,
    onSellCredits: () -> Unit,
    onViewAllEarnings: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            if (uiState.satelliteDataUnavailable) {
                SatellitePendingBanner()
            } else {
                CarbonBanner(uiState, onSellCredits)
                MintingPipelineSection(uiState.pipelineSteps)
                RealImpactSection(uiState)
                EarningsHistorySection(uiState.earnings, onViewAllEarnings)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SatellitePendingBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Satellite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_scan_pending_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_scan_pending_body),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

// Dark green banner with credits + sell button
@Composable
private fun CarbonBanner(uiState: CarbonUiState, onSellCredits: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF0A2E14), Green10)),
            ),
    ) {
        // Decorative large leaf in top-right corner
        Icon(
            imageVector = Icons.Filled.Eco,
            contentDescription = null,
            tint = White.copy(alpha = 0.06f),
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-10).dp),
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_available_credits),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Green90,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_tonnes_co2e, uiState.carbonTonnes),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    fontSize = 32.sp,
                ),
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = ShambaAmber,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_kes_approx, "%,d".format(uiState.kesEquivalent)),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ShambaAmber,
                    ),
                )
            }
            Spacer(Modifier.height(16.dp))
            // Sell Credits Now button — cream background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SellButtonBg)
                    .clickable(onClick = onSellCredits)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_sell_credits_now),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3D2E00),
                    ),
                )
            }
        }
    }
}

// Minting pipeline with vertical connecting line
@Composable
private fun MintingPipelineSection(steps: List<PipelineStep>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_minting_pipeline),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            steps.forEachIndexed { index, step ->
                PipelineRow(step = step, isLast = index == steps.lastIndex)
            }
        }
    }
}

@Composable
private fun PipelineRow(step: PipelineStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Left column: icon + connecting line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp),
        ) {
            Spacer(Modifier.height(14.dp))
            // Status indicator
            when (step.status) {
                PipelineStatus.DONE -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Green40),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                PipelineStatus.ACTIVE -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ShambaAmber),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                PipelineStatus.PENDING -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface),
                    )
                }
            }
            // Connecting line (not on last item)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(
                            when (step.status) {
                                PipelineStatus.DONE -> Green40.copy(alpha = 0.4f)
                                else -> MaterialTheme.colorScheme.outlineVariant
                            },
                        ),
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        // Text content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp, bottom = if (isLast) 12.dp else 4.dp),
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = when (step.status) {
                        PipelineStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
            Text(
                text = step.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

// Real impact — two side-by-side cards
@Composable
private fun RealImpactSection(uiState: CarbonUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_real_impact),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Trees card
            ImpactCard(
                modifier = Modifier.weight(1f),
                bgColor = ImpactTreeBg,
                iconBg = Green95,
                icon = Icons.Filled.Park,
                iconTint = Green40,
                value = "${uiState.treesEquivalent}",
                valueColor = Green40,
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_trees_equivalent),
            )
            // Cars / km card
            ImpactCard(
                modifier = Modifier.weight(1f),
                bgColor = ImpactCarBg,
                iconBg = Color(0xFFFFCDD2),
                icon = Icons.Filled.DirectionsCar,
                iconTint = ImpactCarRed,
                value = "${uiState.kmNotDriven}",
                valueColor = ImpactCarRed,
                label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_km_not_driven),
            )
        }
    }
}

@Composable
private fun ImpactCard(
    modifier: Modifier,
    bgColor: Color,
    iconBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    valueColor: Color,
    label: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp),
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = valueColor,
            ),
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
            ),
        )
    }
}

// Earnings history
@Composable
private fun EarningsHistorySection(
    earnings: List<CarbonEarning>,
    onViewAll: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_earnings_history),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_view_all),
                modifier = Modifier.clickable(onClick = onViewAll),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Green40,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        ) {
            earnings.forEachIndexed { index, earning ->
                EarningRow(earning)
                if (index < earnings.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 64.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun EarningRow(earning: CarbonEarning) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // M-Pesa "M" circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(EarningCircle),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "M",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = earning.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Text(
                text = earning.date,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_plus_kes, "%,d".format(earning.amountKes)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Spacer(Modifier.height(4.dp))
            if (earning.completed) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CompletedBg)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.carbon_completed),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CompletedText,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }
    }
}
