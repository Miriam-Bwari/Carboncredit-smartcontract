package dev.korryr.shambaguard.ui.features.auth.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaButtonType

// Shown immediately after an agent successfully submits their registration.
// Admin must approve the account before the agent can log in.

@Composable
fun AgentPendingScreen(
    onGoToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Hourglass icon badge
            Box(
                modifier         = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.HourglassTop,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(40.dp),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text      = stringResource(R.string.agent_pending_title),
                style     = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onBackground,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text      = stringResource(R.string.agent_pending_subtitle),
                style     = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.secondary,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text      = stringResource(R.string.agent_pending_body),
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text      = stringResource(R.string.agent_pending_contact_hint),
                style     = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(40.dp))

            ShambaButton(
                text     = stringResource(R.string.agent_pending_go_to_login),
                onClick  = onGoToLogin,
                type     = ShambaButtonType.Outlined,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                ),
            )
        }
    }
}
