package dev.korryr.shambaguard.ui.features.agent.view

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.agent.presentation.AgentDashboardUiState
import dev.korryr.shambaguard.ui.features.agent.presentation.RecentRegistration
import dev.korryr.shambaguard.ui.features.agent.presentation.RegistrationStatus
import dev.korryr.shambaguard.ui.theme.Green10
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green95
import dev.korryr.shambaguard.ui.theme.White

private val OfflineBg    = Color(0xFFFFF3E0)
private val OfflineIcon  = Color(0xFFE65100)
private val OfflineText  = Color(0xFF5D3A00)
private val ActiveBg     = Color(0xFFE8F5EA)
private val ActiveText   = Color(0xFF2E7D32)
private val QueuedBg     = Color(0xFFFFF8E1)
private val QueuedText   = Color(0xFF8C6800)
private val DraftBg      = Color(0xFFF5F5F5)
private val DraftText    = Color(0xFF616161)
private val PendingSyncRed = Color(0xFFD32F2F)

@Composable
fun AgentDashboardScreen(
    uiState:         AgentDashboardUiState,
    onRegisterFarmer:() -> Unit,
    onSyncNow:       () -> Unit,
    onFilterToggled: () -> Unit,
    onFarmerClicked: (String) -> Unit,
    modifier:        Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background),
        ) {
            ShambaTopBar(onBack = null)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(Modifier.height(4.dp))

                // Offline banner — only shown when there are pending registrations
                if (uiState.offlinePending > 0) {
                    OfflineBanner(pending = uiState.offlinePending, onSyncNow = onSyncNow)
                }

                // Stats section
                StatsSection(uiState)

                // Recent registrations
                RecentRegistrationsSection(
                    registrations = uiState.recentRegistrations,
                    onFilter      = onFilterToggled,
                    onFarmerClick = onFarmerClicked,
                )

                // Space for the FAB
                Spacer(Modifier.height(72.dp))
            }
        }

        // "+ Register Farmer" pill FAB
        Row(
            modifier          = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(50))
                .background(Green10)
                .clickable(onClick = onRegisterFarmer)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = Icons.Filled.Add,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text  = "Register Farmer",
                style = MaterialTheme.typography.labelLarge.copy(
                    color      = White,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

// Amber offline/sync banner
@Composable
private fun OfflineBanner(pending: Int, onSyncNow: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(OfflineBg)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector        = Icons.Filled.CloudOff,
            contentDescription = null,
            tint               = OfflineIcon,
            modifier           = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text     = "Offline – $pending registrations queued",
            modifier = Modifier.weight(1f),
            style    = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color      = OfflineText,
            ),
        )
        Text(
            text     = "Sync\nNow",
            modifier = Modifier.clickable(onClick = onSyncNow),
            style    = MaterialTheme.typography.labelMedium.copy(
                color          = OfflineIcon,
                fontWeight     = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

// Three stat cards
@Composable
private fun StatsSection(uiState: AgentDashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Row: Farmers Registered | Pending Syncs
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier   = Modifier.weight(1f),
                label      = "Farmers Registered",
                value      = "${uiState.farmersRegistered}",
                valueColor = Green40,
            )
            StatCard(
                modifier   = Modifier.weight(1f),
                label      = "Pending Syncs",
                value      = "${uiState.pendingSyncs}",
                valueColor = PendingSyncRed,
            )
        }
        // Full-width: New This Month
        StatCard(
            modifier   = Modifier.fillMaxWidth(),
            label      = "New This Month",
            value      = "${uiState.newThisMonth}",
            valueColor = Green40,
        )
    }
}

@Composable
private fun StatCard(
    modifier:   Modifier,
    label:      String,
    value:      String,
    valueColor: Color,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = valueColor,
                fontSize   = 36.sp,
            ),
        )
    }
}

// Recent registrations list with filter button
@Composable
private fun RecentRegistrationsSection(
    registrations: List<RecentRegistration>,
    onFilter:      () -> Unit,
    onFarmerClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header row
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = "Recent Registrations",
                modifier = Modifier.weight(1f),
                style    = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Row(
                modifier          = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onFilter)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text  = "Filter",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }

        // Farmer rows in a card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        ) {
            registrations.forEachIndexed { index, reg ->
                FarmerRow(
                    registration = reg,
                    onClick      = { onFarmerClick(reg.id) },
                )
                if (index < registrations.lastIndex) {
                    HorizontalDivider(
                        modifier  = Modifier.padding(start = 72.dp),
                        thickness = 0.5.dp,
                        color     = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmerRow(
    registration: RecentRegistration,
    onClick:      () -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar circle
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Filled.Person,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(26.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        // Name + county
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = registration.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text  = registration.county,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }

        // Status badge + sync text
        Column(horizontalAlignment = Alignment.End) {
            val (bg, textColor, label) = when (registration.status) {
                RegistrationStatus.ACTIVE  -> Triple(ActiveBg,  ActiveText,  "ACTIVE")
                RegistrationStatus.QUEUED  -> Triple(QueuedBg,  QueuedText,  "QUEUED")
                RegistrationStatus.DRAFT   -> Triple(DraftBg,   DraftText,   "DRAFT")
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(bg)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color         = textColor,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                    ),
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text  = registration.syncText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
