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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.biometric.BiometricManager
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerProfileUiState
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.Green95
import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FarmerProfileScreen(
    uiState: FarmerProfileUiState,
    onLanguageSelected: (String) -> Unit,
    onPushNotifications: (Boolean) -> Unit,
    onDroughtAlerts: (Boolean) -> Unit,
    onBiometricToggled: () -> Unit,
    onChangePinClicked: () -> Unit,
    onPolicyDocsClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onThemeChanged: (dev.korryr.shambaguard.core.datastore.AppThemeMode) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    // Force switches OFF if the permission is missing at the OS level (Android 13+)
    LaunchedEffect(notificationPermissionState?.status) {
        if (notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
            if (uiState.pushNotificationsOn) onPushNotifications(false)
            if (uiState.droughtAlertsOn) onDroughtAlerts(false)
        }
    }
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
            ProfileSection(title = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.preferences)) {
                // Theme Toggle
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.app_theme),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.SYSTEM,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.SYSTEM) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                            icon = { Icon(Icons.Filled.PhoneAndroid, contentDescription = "System") },
                        ) {
                            Text(androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.theme_system))
                        }
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.LIGHT,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.LIGHT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                            icon = { Icon(Icons.Filled.LightMode, contentDescription = "Light") },
                        ) {
                            Text(androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.theme_light))
                        }
                        SegmentedButton(
                            selected = uiState.themeMode == dev.korryr.shambaguard.core.datastore.AppThemeMode.DARK,
                            onClick = { onThemeChanged(dev.korryr.shambaguard.core.datastore.AppThemeMode.DARK) },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                            icon = { Icon(Icons.Filled.DarkMode, contentDescription = "Dark") },
                        ) {
                            Text(androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.theme_dark))
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
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.push_notifications),
                    checked = uiState.pushNotificationsOn,
                    onToggle = {
                        if (it && notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        } else {
                            onPushNotifications(it)
                        }
                    },
                )
                SettingsDivider()
                SwitchRow(
                    icon = Icons.Filled.WbSunny,
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.drought_alerts),
                    checked = uiState.droughtAlertsOn,
                    onToggle = {
                        if (it && notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        } else {
                            onDroughtAlerts(it)
                        }
                    },
                )
            }

            // SECURITY section
            ProfileSection(title = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.security)) {
                val context = LocalContext.current
                val canAuthenticate = remember {
                    val bm = BiometricManager.from(context)
                    val result = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    result == BiometricManager.BIOMETRIC_SUCCESS
                }

                SwitchRow(
                    icon = Icons.Filled.Fingerprint,
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.biometric_unlock),
                    checked = uiState.biometricEnabled && canAuthenticate,
                    enabled = canAuthenticate,
                    onToggle = { onBiometricToggled() },
                )
                SettingsDivider()
                ChevronRow(
                    icon = Icons.Filled.Password,
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.change_pin),
                    onClick = onChangePinClicked,
                )
            }

            // LEGAL & SUPPORT section
            ProfileSection(title = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.legal_and_support)) {
                ChevronRow(
                    icon = Icons.Filled.Article,
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.policy_documents),
                    onClick = onPolicyDocsClicked,
                )
                SettingsDivider()
                ChevronRow(
                    icon = Icons.Filled.Shield,
                    label = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.privacy_policy),
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
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.log_out),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                    ),
                )
            }

            // App version
            Text(
                text = "${androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.app_version)} ${uiState.appVersion}",
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
                    text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.profile_verified_farmer),
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
            text = androidx.compose.ui.res.stringResource(dev.korryr.shambaguard.R.string.profile_language),
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
    enabled: Boolean = true,
    onToggle: (Boolean) -> Unit,
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
            enabled = enabled,
            onCheckedChange = { onToggle(it) },
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
