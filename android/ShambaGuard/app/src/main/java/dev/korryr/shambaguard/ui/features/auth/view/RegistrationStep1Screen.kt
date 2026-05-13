package dev.korryr.shambaguard.ui.features.auth.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTextField
import dev.korryr.shambaguard.ui.features.auth.presentation.RegistrationStep1UiState

// Step 1 of 3 — Personal Details screen. Pure UI, no logic.

private const val TOTAL_STEPS = 3
private const val CURRENT_STEP = 1

@Composable
fun RegistrationStep1Screen(
    uiState: RegistrationStep1UiState,
    onFullNameChanged: (String) -> Unit,
    onNationalIdChanged: (String) -> Unit,
    onMpesaPhoneChanged: (String) -> Unit,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate content in on first composition
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    // Animate progress bar value
    val progress by animateFloatAsState(
        targetValue = CURRENT_STEP.toFloat() / TOTAL_STEPS.toFloat(),
        animationSpec = tween(durationMillis = 600),
        label = "StepProgress",
    )

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

            // Top bar
            RegistrationTopBar(
                currentStep = CURRENT_STEP,
                totalSteps  = TOTAL_STEPS,
                progress    = progress,
                onBack      = onBack,
            )

            // Scrollable form body
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(400)) + slideInVertically(
                    animationSpec  = tween(400),
                    initialOffsetY = { it / 8 },
                ),
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    // Section heading
                    Text(
                        text  = stringResource(R.string.reg_step1_heading),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onBackground,
                        ),
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle / instruction
                    Text(
                        text  = stringResource(R.string.reg_step1_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color      = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                        ),
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Full name
                    ShambaTextField(
                        value         = uiState.fullName,
                        onValueChange = onFullNameChanged,
                        label         = stringResource(R.string.reg_full_name_label),
                        isError       = uiState.fullNameError != null,
                        errorMessage  = uiState.fullNameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction      = ImeAction.Next,
                        ),
                        colors = shambaFieldColors(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // National ID
                    ShambaTextField(
                        value         = uiState.nationalId,
                        onValueChange = onNationalIdChanged,
                        label         = stringResource(R.string.reg_national_id_label),
                        isError       = uiState.nationalIdError != null,
                        errorMessage  = uiState.nationalIdError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction    = ImeAction.Next,
                        ),
                        colors = shambaFieldColors(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // M-Pesa phone
                    ShambaTextField(
                        value         = uiState.mpesaPhone,
                        onValueChange = onMpesaPhoneChanged,
                        label         = stringResource(R.string.reg_mpesa_phone_label),
                        isError       = uiState.mpesaPhoneError != null,
                        errorMessage  = uiState.mpesaPhoneError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction    = ImeAction.Done,
                        ),
                        trailingIcon  = { MpesaBadge() },
                        colors        = shambaFieldColors(),
                    )

                    // M-Pesa helper hint
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Circle,
                            contentDescription = null,
                            tint   = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(8.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text  = stringResource(R.string.reg_mpesa_helper),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            // Bottom CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                ShambaButton(
                    text      = stringResource(R.string.reg_next_step_cta),
                    onClick   = onNextStep,
                    enabled   = !uiState.isLoading,
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

// Top bar: back arrow, centred title + step label, progress bar
@Composable
private fun RegistrationTopBar(
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
            // Back arrow
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.reg_back_content_description),
                    tint               = MaterialTheme.colorScheme.primary,
                )
            }

            // Title + step indicator centred in remaining space
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text  = stringResource(R.string.reg_screen_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text  = stringResource(R.string.reg_step_indicator, currentStep, totalSteps),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            // Spacer balances layout so the title stays centred
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Progress bar
        LinearProgressIndicator(
            progress      = { progress },
            modifier      = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color         = MaterialTheme.colorScheme.primary,
            trackColor    = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap     = StrokeCap.Round,
        )
    }
}

// Branded green pill shown as trailing icon inside the M-Pesa field
@Composable
private fun MpesaBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = stringResource(R.string.reg_mpesa_badge),
            style = MaterialTheme.typography.labelSmall.copy(
                color      = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 10.sp,
            ),
        )
    }
}

// Shared branded colours for all outlined text fields on this screen
@Composable
private fun shambaFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor    = MaterialTheme.colorScheme.primary,
    cursorColor          = MaterialTheme.colorScheme.primary,
)
