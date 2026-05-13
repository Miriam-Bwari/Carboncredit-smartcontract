package dev.korryr.shambaguard.ui.features.auth.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmPracticesUiState

// Step 3 of 3 — Farm Practices screen. Pure UI, no logic.

private const val STEP3_CURRENT = 3
private const val STEP3_TOTAL   = 3

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FarmPracticesScreen(
    uiState:          FarmPracticesUiState,
    canComplete:      Boolean,
    onCropToggled:    (String) -> Unit,
    onMethodSelected: (String) -> Unit,
    onWaterSelected:  (String) -> Unit,
    onIncrementTrees: () -> Unit,
    onDecrementTrees: () -> Unit,
    onComplete:       () -> Unit,
    onBack:           () -> Unit,
    modifier:         Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue   = STEP3_CURRENT.toFloat() / STEP3_TOTAL.toFloat(),
        animationSpec = tween(600),
        label         = "Step3Progress",
    )

    // Chip option lists — keys must match string resource names used below
    val crops   = listOf(
        stringResource(R.string.crop_maize),
        stringResource(R.string.crop_beans),
        stringResource(R.string.crop_cowpeas),
        stringResource(R.string.crop_sorghum),
        stringResource(R.string.crop_millet),
        stringResource(R.string.crop_cassava),
    )
    val methods = listOf(
        stringResource(R.string.method_agroforestry),
        stringResource(R.string.method_no_till),
        stringResource(R.string.method_cover_cropping),
        stringResource(R.string.method_conventional),
    )
    val waterSources = listOf(
        stringResource(R.string.water_rain_fed),
        stringResource(R.string.water_irrigation),
        stringResource(R.string.water_borehole),
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // Top bar
            PracticesTopBar(
                currentStep = STEP3_CURRENT,
                totalSteps  = STEP3_TOTAL,
                progress    = progress,
                onBack      = onBack,
            )

            // Scrollable form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text  = stringResource(R.string.practices_heading),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onBackground,
                    ),
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text  = stringResource(R.string.practices_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Carbon credit tip card
                CarbonTipCard()

                Spacer(modifier = Modifier.height(28.dp))

                // Crop type chips (multi-select)
                SectionLabel(stringResource(R.string.practices_crop_section))
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    crops.forEach { crop ->
                        SelectionChip(
                            label      = crop,
                            isSelected = crop in uiState.selectedCrops,
                            onClick    = { onCropToggled(crop) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Farming method chips (single-select)
                SectionLabel(stringResource(R.string.practices_method_section))
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    methods.forEach { method ->
                        SelectionChip(
                            label      = method,
                            isSelected = uiState.selectedMethod == method,
                            onClick    = { onMethodSelected(method) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Water source chips (single-select)
                SectionLabel(stringResource(R.string.practices_water_section))
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    waterSources.forEach { source ->
                        SelectionChip(
                            label      = source,
                            isSelected = uiState.selectedWater == source,
                            onClick    = { onWaterSelected(source) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tree count stepper
                SectionLabel(stringResource(R.string.practices_trees_section))
                Spacer(modifier = Modifier.height(10.dp))
                TreeCountStepper(
                    count       = uiState.treeCount,
                    onIncrement = onIncrementTrees,
                    onDecrement = onDecrementTrees,
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Bottom CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                ShambaButton(
                    text      = stringResource(R.string.practices_complete_cta),
                    onClick   = onComplete,
                    enabled   = canComplete && !uiState.isSubmitting,
                    modifier  = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                    ),
                )
            }
        }
    }
}

// Green tip card shown below the subtitle
@Composable
private fun CarbonTipCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector        = Icons.Filled.Eco,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text  = stringResource(R.string.practices_carbon_tip),
            style = MaterialTheme.typography.bodySmall.copy(
                color      = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
            ),
        )
    }
}

// Bold section label used before each chip group
@Composable
private fun SectionLabel(text: String) {
    Text(
        text  = text,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

// Single chip — filled when selected, outlined when not
@Composable
private fun SelectionChip(
    label:      String,
    isSelected: Boolean,
    onClick:    () -> Unit,
    modifier:   Modifier = Modifier,
) {
    val bgColor     = if (isSelected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurface
    val borderColor  = if (isSelected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color      = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            ),
        )
    }
}

// +/- stepper for tree count
@Composable
private fun TreeCountStepper(
    count:       Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier:    Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text     = stringResource(R.string.practices_trees_count_label),
            style    = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.weight(1f),
        )

        // Decrement button
        IconButton(
            onClick  = onDecrement,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector        = Icons.Filled.Remove,
                contentDescription = stringResource(R.string.practices_decrement_trees),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp),
            )
        }

        // Count display
        Text(
            text  = count.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .width(40.dp),
        )

        // Increment button
        IconButton(
            onClick  = onIncrement,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector        = Icons.Filled.Add,
                contentDescription = stringResource(R.string.practices_increment_trees),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp),
            )
        }
    }
}

// Top bar: back arrow, centred step label, progress bar
@Composable
private fun PracticesTopBar(
    currentStep: Int,
    totalSteps:  Int,
    progress:    Float,
    onBack:      () -> Unit,
    modifier:    Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.reg_back_content_description),
                    tint               = MaterialTheme.colorScheme.primary,
                )
            }

            // Centred step label
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text  = stringResource(R.string.reg_step_indicator, currentStep, totalSteps),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }

            // Spacer keeps label centred
            Spacer(modifier = Modifier.size(48.dp))
        }

        LinearProgressIndicator(
            progress   = { progress },
            modifier   = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color      = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap  = StrokeCap.Round,
        )
    }
}
