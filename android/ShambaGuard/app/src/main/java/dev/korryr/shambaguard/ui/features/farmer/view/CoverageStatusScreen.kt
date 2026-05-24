package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.ui.features.farmer.presentation.CoverageHistoryItem
import dev.korryr.shambaguard.ui.features.farmer.presentation.CoverageStatusUiState
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.ShambaRed
import dev.korryr.shambaguard.ui.theme.White

// Coverage Status screen — second screen in DROUGHT tab flow.
// Shows active policy details, real-time trigger metrics, and payout history.
@Composable
fun CoverageStatusScreen(
    uiState: CoverageStatusUiState,
    onBack:  () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar (matches EarlyWarningScreen)
        ShambaTopBar(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text  = "Coverage Status",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onBackground,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Real-time monitoring of your weather index policy triggers.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                    ),
                )
            }
            ActivePolicyCard(uiState)
            CurrentTriggersSection(uiState)
            HistorySection(uiState.history)
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Active policy card

@Composable
private fun ActivePolicyCard(uiState: CoverageStatusUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Shield + ACTIVE badge row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Green95),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.Security,
                    contentDescription = null,
                    tint               = Green40,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(10.dp))
            // "ACTIVE" pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Green95)
                    .border(1.dp, Green90, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text  = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color      = Green40,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    ),
                )
            }
        }

        // Policy name + validity
        Text(
            text  = uiState.policyName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Text(
            text  = "Valid through ${uiState.validThrough}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )

        // Installment dots
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text  = "Premium Installments",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.premiumInstallments.forEach { paid ->
                    if (paid) {
                        Box(
                            modifier         = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Green95),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.CheckCircle,
                                contentDescription = "Paid",
                                tint               = Green40,
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                        )
                    }
                }
            }
        }
    }
}

// Current triggers section

@Composable
private fun CurrentTriggersSection(uiState: CoverageStatusUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section header
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = "Current Triggers",
                modifier = Modifier.weight(1f),
                style    = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Text(
                text  = "Updated ${uiState.triggersLastUpdated}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        // Rainfall trigger card
        TriggerCard(
            icon         = Icons.Filled.WaterDrop,
            iconTint     = Green40,
            iconBg       = Green95,
            label        = "Cumulative Rainfall",
            displayValue = "${uiState.rainfallMm.toInt()} mm",
            value        = uiState.rainfallMm,
            maxValue     = uiState.rainfallMaxMm,
            triggerValue = uiState.rainfallTriggerMm,
            fillColor    = Green40,
            triggerColor = ShambaRed,
            triggerLabel = "Trigger: < ${uiState.rainfallTriggerMm.toInt()}mm",
            rangeStart   = "0",
            rangeEnd     = uiState.rainfallMaxMm.toInt().toString(),
        )

        // NDVI trigger card
        val ndviBarColor = Color(0xFF7B5800)
        TriggerCard(
            icon         = Icons.Filled.Eco,
            iconTint     = Color(0xFF7B5800),
            iconBg       = Color(0xFFFFF0CC),
            label        = "Vegetation Health (NDVI)",
            displayValue = String.format("%.2f", uiState.ndviValue),
            value        = uiState.ndviValue,
            maxValue     = 1.0f,
            triggerValue = uiState.ndviWarning,
            fillColor    = ndviBarColor,
            triggerColor = ShambaAmber,
            triggerLabel = "Warning: < ${String.format("%.2f", uiState.ndviWarning)}",
            rangeStart   = "0.0",
            rangeEnd     = "1.0",
        )
    }
}

@Composable
private fun TriggerCard(
    icon:         ImageVector,
    iconTint:     Color,
    iconBg:       Color,
    label:        String,
    displayValue: String,
    value:        Float,
    maxValue:     Float,
    triggerValue: Float,
    fillColor:    Color,
    triggerColor: Color,
    triggerLabel: String,
    rangeStart:   String,
    rangeEnd:     String,
) {
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Icon + label
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = iconTint,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        // Large value
        Text(
            text  = displayValue,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onSurface,
            ),
        )

        // Range labels
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = rangeStart,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Text(
                text  = rangeEnd,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        // Progress bar with threshold marker + label drawn on canvas
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
        ) {
            val barH    = 8.dp.toPx()
            val barTop  = 4.dp.toPx()
            val radius  = barH / 2f
            val progress = (value / maxValue).coerceIn(0f, 1f)
            val triggerFraction = (triggerValue / maxValue).coerceIn(0f, 1f)
            val triggerX = size.width * triggerFraction

            // Track
            drawRoundRect(
                color        = Color.LightGray.copy(alpha = 0.35f),
                topLeft      = Offset(0f, barTop),
                size         = Size(size.width, barH),
                cornerRadius = CornerRadius(radius),
            )
            // Fill
            val fillW = size.width * progress
            if (fillW > 0f) {
                drawRoundRect(
                    color        = fillColor,
                    topLeft      = Offset(0f, barTop),
                    size         = Size(fillW, barH),
                    cornerRadius = CornerRadius(radius),
                )
            }
            // Trigger marker line
            drawLine(
                color       = triggerColor,
                start       = Offset(triggerX, barTop - 2.dp.toPx()),
                end         = Offset(triggerX, barTop + barH + 2.dp.toPx()),
                strokeWidth = 1.5.dp.toPx(),
            )
            // Trigger label
            val labelLayout = textMeasurer.measure(
                text  = triggerLabel,
                style = TextStyle(
                    color      = triggerColor,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            drawText(
                textLayoutResult = labelLayout,
                topLeft = Offset(
                    x = (triggerX - labelLayout.size.width / 2f)
                        .coerceIn(0f, size.width - labelLayout.size.width),
                    y = barTop + barH + 4.dp.toPx(),
                ),
            )
        }
    }
}

// History section

@Composable
private fun HistorySection(history: List<CoverageHistoryItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text  = "History",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            history.forEachIndexed { index, item ->
                HistoryRow(item = item)
                if (index < history.lastIndex) {
                    HorizontalDivider(
                        modifier  = Modifier.padding(start = 48.dp),
                        thickness = 0.5.dp,
                        color     = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(item: CoverageHistoryItem) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Icon — green check for payout, grey circle for other events
        if (item.isPayout) {
            Box(
                modifier         = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Green95),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = Green40,
                    modifier           = Modifier.size(20.dp),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Spacer(Modifier.height(2.dp))
            val meta = if (item.detail.isNotEmpty()) "${item.date} • ${item.detail}" else item.date
            Text(
                text  = meta,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            // Payout amount in green
            item.amount?.let { amount ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = amount,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = Green40,
                    ),
                )
            }
        }
    }
}
