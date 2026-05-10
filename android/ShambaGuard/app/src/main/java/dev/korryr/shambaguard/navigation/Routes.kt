package dev.korryr.shambaguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Auth / Onboarding
@Serializable data object SplashKey
@Serializable data object OnboardingKey
@Serializable data object LoginKey

// Admin Keys 
@Serializable data object AdminHomeKey
@Serializable data object AdminMapKey
@Serializable data object AdminAgentsKey

//  Agent Keys
@Serializable data object AgentHomeKey
@Serializable data object AgentFarmersKey
@Serializable data object AgentSyncKey

// Farmer Keys
@Serializable data object FarmerHomeKey
@Serializable data object FarmerPolicyKey
@Serializable data object FarmerPayoutsKey

//  Bottom nav tab metadata 
sealed class BottomTab(
    val title: String,
    val key: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // Admin Tabs
    data object AdminHome : BottomTab("Home", AdminHomeKey, Icons.Filled.Home, Icons.Outlined.Home)
    data object AdminMap : BottomTab("Map", AdminMapKey, Icons.Filled.Map, Icons.Outlined.Map)
    data object AdminAgents : BottomTab("Agents", AdminAgentsKey, Icons.Filled.Groups, Icons.Outlined.Groups)

    // Agent Tabs
    data object AgentHome : BottomTab("Home", AgentHomeKey, Icons.Filled.Home, Icons.Outlined.Home)
    data object AgentFarmers : BottomTab("Farmers", AgentFarmersKey, Icons.Filled.Person, Icons.Outlined.Person)
    data object AgentSync : BottomTab("Sync", AgentSyncKey, Icons.Filled.Sync, Icons.Outlined.Sync)

    // Farmer Tabs
    data object FarmerHome : BottomTab("Home", FarmerHomeKey, Icons.Filled.Home, Icons.Outlined.Home)
    data object FarmerPolicy : BottomTab("Policy", FarmerPolicyKey, Icons.Filled.Description, Icons.Outlined.Description)
    data object FarmerPayouts : BottomTab("Payouts", FarmerPayoutsKey, Icons.Filled.Payments, Icons.Outlined.Payments)

    companion object {
        val adminTabs = listOf(AdminHome, AdminMap, AdminAgents)
        val agentTabs = listOf(AgentHome, AgentFarmers, AgentSync)
        val farmerTabs = listOf(FarmerHome, FarmerPolicy, FarmerPayouts)
    }
}
