package dev.korryr.shambaguard.ui.features.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.navigation.UserRole
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.ui.features.auth.presentation.LoginUiState

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onPhoneChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit
) {
    LaunchedEffect(uiState.successRole) {
        uiState.successRole?.let { role ->
            onLoginSuccess(role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Shamba Guard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your farm, watched from space. Protected by code.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.phone,
            onValueChange = onPhoneChanged,
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && !uiState.isOtpSent
        )

        if (uiState.isOtpSent) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.otp,
                onValueChange = onOtpChanged,
                label = { Text("Enter OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            ShambaButton(
                text = if (uiState.isOtpSent) "Verify OTP & Login" else "Send OTP",
                onClick = { if (uiState.isOtpSent) onVerifyOtp() else onSendOtp() }
            )
        }
    }
}
