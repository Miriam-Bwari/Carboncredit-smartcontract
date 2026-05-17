package dev.korryr.shambaguard.ui.features.farmer.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.features.farmer.presentation.PaymentState
import dev.korryr.shambaguard.ui.features.farmer.presentation.PolicyTier
import dev.korryr.shambaguard.ui.features.farmer.presentation.PolicyUiState
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.Green99
import dev.korryr.shambaguard.ui.theme.ShambaAmber
import dev.korryr.shambaguard.ui.theme.White

// PolicyScreen — Chagua Bima / Choose Your Coverage
// Accessible via the Farmer "Policy" bottom-nav tab AND immediately after Step 3 registration.
@Composable
fun PolicyScreen(
    uiState: PolicyUiState,
    onTierSelected: (PolicyTier) -> Unit,
    onPayWithMpesa: () -> Unit,
    onPaymentDone: () -> Unit,       // Called when success state is acknowledged
    modifier: Modifier = Modifier,
) {
    // Animate screen entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Navigate away once payment succeeds (after short delay for user to see the state)
    LaunchedEffect(uiState.paymentState) {
        if (uiState.paymentState is PaymentState.Success) {
            kotlinx.coroutines.delay(1_500L)
            onPaymentDone()
        }
    }

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
            // Screen header
            PolicyHeader()

            // Tier cards — scrollable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                PolicyTier.entries.forEach { tier ->
                    TierCard(
                        tier = tier,
                        isSelected = uiState.selectedTier == tier,
                        onClick = { onTierSelected(tier) },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // M-Pesa payment footer
            MpesaPaymentFooter(
                isEnabled = uiState.selectedTier != null,
                paymentState = uiState.paymentState,
                onPayWithMpesa = onPayWithMpesa,
            )
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun PolicyHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.policy_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.policy_screen_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

// ─── Tier Card ────────────────────────────────────────────────────────────────

@Composable
private fun TierCard(
    tier: PolicyTier,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate card elevation and border when selected
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 1.dp,
        animationSpec = tween(200),
        label = "CardElevation_${tier.name}",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected && !tier.isPremiumDark) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "BorderWidth_${tier.name}",
    )

    // Dark card (Nguvu) vs light card (Msingi / Imara)
    val cardBg by animateColorAsState(
        targetValue = when {
            tier.isPremiumDark -> Green10
            isSelected -> White
            else -> Green99
        },
        animationSpec = tween(200),
        label = "CardBg_${tier.name}",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            tier.isPremiumDark -> Green10
            isSelected -> Green40
            else -> Green95
        },
        animationSpec = tween(200),
        label = "BorderColor_${tier.name}",
    )

    Box(modifier = modifier.fillMaxWidth()) {

        // Card body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            // Tier name row
            TierNameRow(tier = tier, isSelected = isSelected)

            Spacer(modifier = Modifier.height(8.dp))

            // Premium amount
            PremiumAmountText(tier = tier)

            Spacer(modifier = Modifier.height(16.dp))

            // Feature bullets
            tier.features.forEach { feature ->
                FeatureRow(
                    text = feature,
                    isDarkCard = tier.isPremiumDark,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Select / Selected button
            TierSelectButton(
                tier = tier,
                isSelected = isSelected,
                onClick = onClick,
            )
        }

        // "RECOMMENDED" badge — floats on top of Imara card
        if (tier.isRecommended) {
            RecommendedBadge(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp),
            )
        }
    }
}

// Tier name + sub-label row
@Composable
private fun TierNameRow(
    tier: PolicyTier,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val nameColor = if (tier.isPremiumDark) White
    else MaterialTheme.colorScheme.onSurface
    val subColor = if (tier.isPremiumDark) White.copy(alpha = 0.7f)
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = tier.nameKiswahili,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = nameColor,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "(${tier.nameEnglish})",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = subColor,
            ),
        )
    }
}

// "KES 150 /mo" — large price display
@Composable
private fun PremiumAmountText(
    tier: PolicyTier,
    modifier: Modifier = Modifier,
) {
    val textColor = if (tier.isPremiumDark) White else MaterialTheme.colorScheme.onSurface

    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = textColor,
                )
            ) { append("KES ${tier.premiumKes}") }
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (tier.isPremiumDark) White.copy(0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            ) { append(" /mo") }
        },
        modifier = modifier,
    )
}

// Single feature bullet row with green check icon
@Composable
private fun FeatureRow(
    text: String,
    isDarkCard: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconTint = if (isDarkCard) Green90 else Green40
    val textColor = if (isDarkCard) White.copy(alpha = 0.9f)
    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textColor,
                lineHeight = 20.sp,
            ),
        )
    }
}

// "Select Plan" / "✓ Selected" action button inside each card
@Composable
private fun TierSelectButton(
    tier: PolicyTier,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && !tier.isPremiumDark -> Green40
            tier.isPremiumDark && isSelected -> White.copy(alpha = 0.15f)
            tier.isPremiumDark -> White.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "BtnBg_${tier.name}",
    )
    val textColor = when {
        isSelected && !tier.isPremiumDark -> White
        tier.isPremiumDark -> White
        else -> Green40
    }
    val borderColor = when {
        tier.isPremiumDark -> White.copy(alpha = 0.3f)
        else -> Green40
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = if (isSelected) stringResource(R.string.policy_tier_selected)
            else stringResource(R.string.policy_tier_select),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 14.sp,
            ),
        )
    }
}

// Amber "RECOMMENDED" pill badge — floats on top center of Imara card
@Composable
private fun RecommendedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ShambaAmber)
            .padding(horizontal = 14.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(10.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.policy_recommended_badge),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                ),
            )
        }
    }
}

// ─── M-Pesa Footer ───────────────────────────────────────────────────────────

@Composable
private fun MpesaPaymentFooter(
    isEnabled: Boolean,
    paymentState: PaymentState,
    onPayWithMpesa: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = paymentState is PaymentState.Loading
    val isSuccess = paymentState is PaymentState.Success

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // M-Pesa STK Push button
        MpesaButton(
            isEnabled = isEnabled && !isLoading && !isSuccess,
            isLoading = isLoading,
            isSuccess = isSuccess,
            onClick = onPayWithMpesa,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Secure payment hint
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.policy_secure_payment_hint),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

// The full-width M-Pesa branded pill button
@Composable
private fun MpesaButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    isSuccess: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSuccess -> Color(0xFF2E9447)  // Brighter success green
            !isEnabled -> MaterialTheme.colorScheme.surfaceVariant
            else -> Green40
        },
        animationSpec = tween(300),
        label = "MpesaBtnBg",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .then(
                if (isEnabled && !isLoading) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.policy_payment_processing),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = White,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            isSuccess -> {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.policy_payment_success),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = White,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            else -> {
                // M-Pesa pill label
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(White)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.policy_mpesa_label),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Green40,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.policy_mpesa_cta),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isEnabled) White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                )
            }
        }
    }
}
