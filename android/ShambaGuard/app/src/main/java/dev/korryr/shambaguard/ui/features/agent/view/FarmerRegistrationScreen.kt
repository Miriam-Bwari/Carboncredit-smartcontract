package dev.korryr.shambaguard.ui.features.agent.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.shambaguard.sharedComposables.ShambaButton
import dev.korryr.shambaguard.sharedComposables.ShambaTextField
import dev.korryr.shambaguard.sharedComposables.ShambaTopBar

@Composable
fun FarmerRegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            ShambaTopBar(
                title = "Register Farmer",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShambaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name"
            )
            
            ShambaTextField(
                value = nationalId,
                onValueChange = { nationalId = it },
                label = "National ID"
            )
            
            ShambaTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number (e.g., 2547...)"
            )

            Spacer(modifier = Modifier.weight(1f))
            
            ShambaButton(
                text = "Next: Map Farm Polygon",
                onClick = onNavigateToMap,
                enabled = name.isNotBlank() && nationalId.isNotBlank() && phone.isNotBlank()
            )
        }
    }
}
