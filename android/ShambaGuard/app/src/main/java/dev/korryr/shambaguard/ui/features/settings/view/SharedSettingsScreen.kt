package dev.korryr.shambaguard.ui.features.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.core.datastore.AppThemeMode
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar
import dev.korryr.shambaguard.ui.features.settings.presentation.SharedSettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedSettingsScreen(
    uiState: SharedSettingsUiState,
    onThemeChanged: (AppThemeMode) -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onNavigateToLogin()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Settings",
                onBack = onNavigateBack,
                trailingIcon = {}, // Empty to hide location pin
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Theme row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = "App Theme",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = uiState.themeMode == AppThemeMode.SYSTEM,
                        onClick = { onThemeChanged(AppThemeMode.SYSTEM) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                        icon = { Icon(Icons.Rounded.PhoneAndroid, contentDescription = "System") }
                    ) {
                        Text("System")
                    }
                    SegmentedButton(
                        selected = uiState.themeMode == AppThemeMode.LIGHT,
                        onClick = { onThemeChanged(AppThemeMode.LIGHT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                        icon = { Icon(Icons.Rounded.LightMode, contentDescription = "Light") }
                    ) {
                        Text("Light")
                    }
                    SegmentedButton(
                        selected = uiState.themeMode == AppThemeMode.DARK,
                        onClick = { onThemeChanged(AppThemeMode.DARK) },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                        icon = { Icon(Icons.Rounded.DarkMode, contentDescription = "Dark") }
                    ) {
                        Text("Dark")
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Logout row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Log Out",
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Log Out",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                if (uiState.isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.error,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                    ) {
                        Text("Log Out")
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out? You will need your credentials to log back in.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                    ) {
                        Text(
                            text = "Log Out",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}
