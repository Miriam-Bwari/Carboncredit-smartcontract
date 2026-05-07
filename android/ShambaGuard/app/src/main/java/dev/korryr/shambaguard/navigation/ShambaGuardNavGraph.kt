package dev.korryr.shambaguard.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.korryr.shambaguard.ui.features.splash.SplashScreen

enum class UserRole {
    Admin, Agent, Farmer, Unauthenticated
}

@Composable
fun ShambaGuardNavGraph(
    modifier: Modifier = Modifier,
    role: UserRole = UserRole.Farmer // Defaulting to Farmer for development until Auth is done
) {
    // 1. Determine starting key based on role
    val initialKey = remember(role) {
        when (role) {
            UserRole.Admin -> AdminHomeKey
            UserRole.Agent -> AgentHomeKey
            UserRole.Farmer -> FarmerHomeKey
            UserRole.Unauthenticated -> LoginKey
        }
    }

    // 2. Determine tabs based on role
    val tabs = remember(role) {
        when (role) {
            UserRole.Admin -> BottomTab.adminTabs
            UserRole.Agent -> BottomTab.agentTabs
            UserRole.Farmer -> BottomTab.farmerTabs
            UserRole.Unauthenticated -> emptyList()
        }
    }

    // ── Back stack – always starts on the splash screen ───
    val backStack = remember(role) { mutableStateListOf<Any>(SplashKey) }
    val currentKey = backStack.lastOrNull()

    // Show the bottom bar only when one of the root tabs is on top
    val rootKeys = tabs.map { it.key }.toSet()
    val showBottomBar = currentKey in rootKeys && tabs.isNotEmpty()

    // Handle system back: pop unless we're at the root
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    tabs = tabs,
                    currentKey = currentKey,
                    onTabSelected = { key ->
                        // Switch root tab: keep only that tab on the stack
                        backStack.clear()
                        backStack.add(key)
                    }
                )
            }
        }
    ) { innerPadding ->

        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {

                // Splash screen
                entry<SplashKey> { 
                    SplashScreen(
                        onSplashComplete = {
                            // Clear splash from back stack and navigate to home based on role
                            backStack.clear()
                            backStack.add(initialKey)
                        }
                    ) 
                }

                // Auth screens
                entry<LoginKey> { PlaceholderScreen("Login") }

                // Admin screens
                entry<AdminHomeKey> { PlaceholderScreen("Admin Dashboard") }
                entry<AdminMapKey> { PlaceholderScreen("Admin Farm Map") }
                entry<AdminAgentsKey> { PlaceholderScreen("Admin Agents Management") }

                // Agent screens
                entry<AgentHomeKey> { PlaceholderScreen("Agent Dashboard") }
                entry<AgentFarmersKey> { PlaceholderScreen("Agent Farmers Management") }
                entry<AgentSyncKey> { PlaceholderScreen("Agent Sync Status") }

                // Farmer screens
                entry<FarmerHomeKey> { PlaceholderScreen("Farmer Dashboard") }
                entry<FarmerPolicyKey> { PlaceholderScreen("Farmer Policy Details") }
                entry<FarmerPayoutsKey> { PlaceholderScreen("Farmer Payout History") }
            }
        )
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}

@Composable
fun PlaceholderScreenWithAction(title: String, buttonText: String, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
        androidx.compose.material3.Button(onClick = onClick) {
            Text(text = buttonText)
        }
    }
}
