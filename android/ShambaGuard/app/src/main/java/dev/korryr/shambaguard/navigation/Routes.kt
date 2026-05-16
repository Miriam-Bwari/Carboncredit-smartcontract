package dev.korryr.shambaguard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Auth / Onboarding routes
@Serializable data object SplashKey
@Serializable data object OnboardingKey
@Serializable data object RoleSelectionKey
@Serializable data object RegistrationKey   // Step 1: Personal details
@Serializable data object FarmBoundaryKey   // Step 2: Draw farm polygon
@Serializable data object FarmPracticesKey  // Step 3: Farm practices
@Serializable data object LoginKey

// Admin routes
@Serializable data object AdminHomeKey
@Serializable data object AdminMapKey
@Serializable data object AdminAgentsKey

// Agent routes
@Serializable data object AgentHomeKey
@Serializable data object AgentFarmersKey
@Serializable data object AgentSyncKey

// Farmer routes
@Serializable data object FarmerHomeKey      // Dashboard — HOME tab
@Serializable data object FarmerDroughtKey   // Early warning — DROUGHT tab
@Serializable data object FarmerMyFarmKey    // Farm management — MY FARM tab
@Serializable data object FarmerCarbonKey    // Carbon credits — CARBON tab
@Serializable data object FarmerProfileKey   // Profile — PROFILE tab

// Still accessible (navigated from dashboard / policy card) but not a bottom tab:
@Serializable data object FarmerPolicyKey    // Coverage selection / payment
@Serializable data object FarmerPayoutsKey   // Payout history

// Bottom-nav tab metadata
sealed class BottomTab(
    val title:         String,
    val key:           Any,
    val selectedIcon:  ImageVector,
    val unselectedIcon: ImageVector,
) {
    // Admin tabs (3 tabs)
    data object AdminHome   : BottomTab("Home",   AdminHomeKey,   Icons.Filled.Home,        Icons.Outlined.Home)
    data object AdminMap    : BottomTab("Map",    AdminMapKey,    Icons.Filled.Map,         Icons.Outlined.Map)
    data object AdminAgents : BottomTab("Agents", AdminAgentsKey, Icons.Filled.Groups,      Icons.Outlined.Groups)

    // Agent tabs (3 tabs)
    data object AgentHome    : BottomTab("Home",    AgentHomeKey,    Icons.Filled.Home,   Icons.Outlined.Home)
    data object AgentFarmers : BottomTab("Farmers", AgentFarmersKey, Icons.Filled.Groups, Icons.Outlined.Groups)
    data object AgentSync    : BottomTab("Sync",    AgentSyncKey,    Icons.Filled.Sync,   Icons.Outlined.Sync)

    // Farmer tabs — 5 tabs matching the dashboard mockup
    data object FarmerHome    : BottomTab("Home",    FarmerHomeKey,    Icons.Filled.Home,        Icons.Outlined.Home)
    data object FarmerDrought : BottomTab("Drought", FarmerDroughtKey, Icons.Filled.WbSunny,     Icons.Outlined.WbSunny)
    data object FarmerMyFarm  : BottomTab("My Farm", FarmerMyFarmKey,  Icons.Filled.Agriculture,  Icons.Outlined.Agriculture)
    data object FarmerCarbon  : BottomTab("Carbon",  FarmerCarbonKey,  Icons.Filled.Eco,          Icons.Outlined.Eco)
    data object FarmerProfile : BottomTab("Profile", FarmerProfileKey, Icons.Filled.Person,       Icons.Outlined.Person)

    companion object {
        val adminTabs  = listOf(AdminHome, AdminMap, AdminAgents)
        val agentTabs  = listOf(AgentHome, AgentFarmers, AgentSync)
        val farmerTabs = listOf(FarmerHome, FarmerDrought, FarmerMyFarm, FarmerCarbon, FarmerProfile)
    }
}
