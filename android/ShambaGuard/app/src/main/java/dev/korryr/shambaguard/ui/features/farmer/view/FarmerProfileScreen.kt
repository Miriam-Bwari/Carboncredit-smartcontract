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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerProfileUiState
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    uiState: FarmerProfileUiState,
    onLanguageSelected: (String) -> Unit,
    onPushNotifications: () -> Unit,
    onDroughtAlerts: () -> Unit,
    onBiometricToggled: () -> Unit,
    onChangePinClicked: () -> Unit,
    onPolicyDocsClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onThemeChanged: (dev.korryr.shambaguard.core.datastore.AppThemeMode) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar
        ShambaTopBar(onBack = null)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Avatar + name + verified badge
            ProfileHeader(uiState)

            // PREFERENCES section
            ProfileSection(title = "PREFERENCES") {
                // Theme Toggle
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.SYSTEM,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.SYSTEM) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                            icon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = "System") }
                        ) {
                            Text("System")
                        }
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.LIGHT,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.LIGHT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                            icon = { Icon(Icons.Filled.LightMode, contentDescription = "Light") }
                        ) {
                            Text("Light")
                        }
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.DARK,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.DARK) },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = "Dark") }
                        ) {
                            Text("Dark")
                        }
                    }
                }
                SettingsDivider()
                // Language toggle (ENG / SWA)
                LanguageRow(
                    selected = uiState.selectedLanguage,
                    onSelect = onLanguageSelected,
                )
                SettingsDivider()
                SwitchRow(
                    icon = Icons.Filled.Notifications,
                    label = "Push Notifications",
                    checked = uiState.pushNotificationsOn,
                    onToggle = onPushNotifications,
                )
                SettingsDivider()
                SwitchRow(
                    icon = Icons.Filled.WbSunny,
                    label = "Drought Alerts",
                    checked = uiState.droughtAlertsOn,
                    onToggle = onDroughtAlerts,
                )
            }

            // SECURITY section
            ProfileSection(title = "SECURITY") {
                SwitchRow(
                    icon = Icons.Filled.Fingerprint,
                    label = "Biometric Unlock",
                    checked = uiState.biometricEnabled,
                    onToggle = onBiometricToggled,
                )
                SettingsDivider()
                ChevronRow(
                    icon = Icons.Filled.Password,
                    label = "Change PIN",
                    onClick = onChangePinClicked,
                )
            }

            // LEGAL & SUPPORT section
            ProfileSection(title = "LEGAL & SUPPORT") {
                ChevronRow(
                    icon = Icons.Filled.Article,
                    label = "Policy Documents",
                    onClick = onPolicyDocsClicked,
                )
                SettingsDivider()
                ChevronRow(
                    icon = Icons.Filled.Shield,
                    label = "Privacy Policy",
                    onClick = onPrivacyPolicyClicked,
                )
            }

            // Sign Out button — outlined red style
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.error,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                    ),
                )
            }

            // App version
            Text(
                text = "Shamba Guard ${uiState.appVersion}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileHeader(uiState: FarmerProfileUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Avatar circle (placeholder illustration)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp),
            )
        }

        Text(
            text = uiState.farmerName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
        )

        Text(
            text = "ID: ${uiState.farmerId}  •  ${uiState.phone}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )

        if (uiState.isVerified) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Green95)
                    .border(1.dp, Green90, RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Green40,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Verified Farmer",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Green40,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

// Section wrapper with grey caps title and white card
@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp,
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)),
            content = content,
        )
    }
}

// Language segment row — ENG / SWA
@Composable
private fun LanguageRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Language,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = "Language",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
        // ENG / SWA segment control
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
        ) {
            listOf("ENG", "SWA").forEach { lang ->
                val isSelected = selected == lang
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { onSelect(lang) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        ),
                    )
                }
            }
        }
    }
}

// Row with switch toggle
@Composable
private fun SwitchRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = Green40,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )
    }
}

// Row with chevron (navigation)
@Composable
private fun ChevronRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 50.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
