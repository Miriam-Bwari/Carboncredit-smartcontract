package dev.korryr.shambaguard.sharedComposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Compact top bar shared across all non-home farmer tab screens.
// Height: 8dp padding + 36dp icon = ~52dp vs the 68dp of a full IconButton row.
// Pass onBack = null to hide the back arrow (e.g., on root tab screens).
@Composable
fun ShambaTopBar(
    title: String = "Habari, Shamba Guard",
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable () -> Unit = {
        Spacer(Modifier.size(48.dp))
    },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back button — 36dp clickable icon, no minimum touch-target enforcement
        if (onBack != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(6.dp),
            )
        } else {
            Spacer(Modifier.size(36.dp))
        }

        // Centered title
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            ),
        )

        // Trailing icon slot — default to the location pin
        Box(
            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            trailingIcon()
        }
    }
}
