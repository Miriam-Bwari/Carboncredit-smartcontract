package dev.korryr.shambaguard.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmBoundaryViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmPracticesViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.RegistrationViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.RoleSelectionViewModel
import dev.korryr.shambaguard.ui.features.auth.view.FarmBoundaryScreen
import dev.korryr.shambaguard.ui.features.auth.view.FarmPracticesScreen
import dev.korryr.shambaguard.ui.features.auth.view.RegistrationStep1Screen
import dev.korryr.shambaguard.ui.features.auth.view.RoleSelectionScreen
import dev.korryr.shambaguard.ui.features.farmer.presentation.DroughtViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerDashboardViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.PolicyViewModel
import dev.korryr.shambaguard.ui.features.farmer.view.DroughtInsightsScreen
import dev.korryr.shambaguard.ui.features.farmer.view.EarlyWarningScreen
import dev.korryr.shambaguard.ui.features.farmer.view.FarmerDashboardScreen
import dev.korryr.shambaguard.ui.features.farmer.view.PolicyScreen
import dev.korryr.shambaguard.ui.features.onboarding.OnboardingScreen
import dev.korryr.shambaguard.ui.features.onboarding.OnboardingViewModel
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

    // Back stack — always starts on the splash screen
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
                    val onboardingVm: OnboardingViewModel = hiltViewModel()
                    val onboardingDone by onboardingVm.onboardingCompleted
                        .collectAsStateWithLifecycle()

                    SplashScreen(
                        onSplashComplete = {
                            backStack.clear()
                            when (onboardingDone) {
                                // Still loading from disk — wait for the LaunchedEffect below
                                null -> Unit
                                // First launch — show onboarding
                                false -> backStack.add(OnboardingKey)
                                // Returning user — go straight to home
                                true -> backStack.add(initialKey)
                            }
                        }
                    )

                    // Safety: if the DataStore value resolves after the splash timer fires,
                    // navigate immediately from Splash.
                    LaunchedEffect(onboardingDone) {
                        if (onboardingDone != null && backStack.lastOrNull() == SplashKey) {
                            backStack.clear()
                            if (onboardingDone == false) {
                                backStack.add(OnboardingKey)
                            } else {
                                backStack.add(initialKey)
                            }
                        }
                    }
                }

                // Onboarding
                entry<OnboardingKey> {
                    val onboardingVm: OnboardingViewModel = hiltViewModel()

                    OnboardingScreen(
                        onFinish = {
                            onboardingVm.markOnboardingDone()
                            backStack.clear()
                            backStack.add(RoleSelectionKey)
                        }
                    )
                }

                // Role Selection
                entry<RoleSelectionKey> {
                    val vm: RoleSelectionViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    RoleSelectionScreen(
                        uiState = state,
                        onRoleSelected = vm::onRoleSelected,
                        onContinue = {
                            // Navigate to registration once a role is chosen
                            backStack.add(RegistrationKey)
                        },
                    )
                }

                // Step 1: Personal Details
                entry<RegistrationKey> {
                    val vm: RegistrationViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    val errorNameEmpty = stringResource(R.string.reg_error_full_name_empty)
                    val errorIdInvalid = stringResource(R.string.reg_error_national_id_invalid)
                    val errorPhoneInvalid = stringResource(R.string.reg_error_phone_invalid)

                    RegistrationStep1Screen(
                        uiState = state,
                        onFullNameChanged = vm::onFullNameChanged,
                        onNationalIdChanged = vm::onNationalIdChanged,
                        onMpesaPhoneChanged = vm::onMpesaPhoneChanged,
                        onNextStep = {
                            if (vm.validateStep1(
                                    errorNameEmpty,
                                    errorIdInvalid,
                                    errorPhoneInvalid
                                )
                            ) {
                                backStack.add(FarmBoundaryKey)
                            }
                        },
                        onBack = { backStack.removeLastOrNull() },
                    )
                }

                // Step 2: Draw farm polygon
                entry<FarmBoundaryKey> {
                    val vm: FarmBoundaryViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    FarmBoundaryScreen(
                        uiState = state,
                        canSave = vm.canSave(),
                        onMapTapped = vm::onMapTapped,
                        onUndo = vm::onUndoLastPoint,
                        onToggleLayer = vm::onToggleMapType,
                        onSave = { backStack.add(FarmPracticesKey) },
                        onBack = { backStack.removeLastOrNull() },
                    )
                }

                // Step 3: Farm practices
                entry<FarmPracticesKey> {
                    val vm: FarmPracticesViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    FarmPracticesScreen(
                        uiState = state,
                        canComplete = vm.canComplete(),
                        onCropToggled = vm::onCropToggled,
                        onMethodSelected = vm::onMethodSelected,
                        onWaterSelected = vm::onWaterSelected,
                        onIncrementTrees = vm::onIncrementTrees,
                        onDecrementTrees = vm::onDecrementTrees,
                        onComplete = {
                            // Step 3 complete — go directly to PolicyScreen (Option A)
                            // so the farmer selects a coverage tier before reaching the dashboard
                            backStack.add(FarmerPolicyKey)
                        },
                        onBack = { backStack.removeLastOrNull() },
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
                entry<FarmerHomeKey> {
                    val vm: FarmerDashboardViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    FarmerDashboardScreen(
                        uiState = state,
                        onSeeInsights = { backStack.add(FarmerDroughtKey) },
                        onViewPolicy = { backStack.add(FarmerPolicyKey) },
                        onViewCarbon = { backStack.add(FarmerCarbonKey) },
                    )
                }
                entry<FarmerDroughtKey> {
                    // Scope ViewModel to the NavGraph if we want it shared, or to this entry.
                    // Since it's a tab, we'll scope it normally here.
                    val vm: DroughtViewModel = hiltViewModel()
                    val state by vm.warningState.collectAsStateWithLifecycle()

                    EarlyWarningScreen(
                        uiState = state,
                        onFullAnalysis = { backStack.add(FarmerDroughtInsightsKey) }
                    )
                }
                entry<FarmerMyFarmKey> { PlaceholderScreen("My Farm — Practices & Map") }
                entry<FarmerCarbonKey> { PlaceholderScreen("Carbon Credits") }
                entry<FarmerProfileKey> { PlaceholderScreen("Farmer Profile") }
                // Non-tab screens — navigated from dashboard or tabs
                entry<FarmerDroughtInsightsKey> {
                    val vm: DroughtViewModel = hiltViewModel()
                    val state by vm.insightsState.collectAsStateWithLifecycle()

                    DroughtInsightsScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<FarmerPolicyKey> {
                    val vm: PolicyViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    PolicyScreen(
                        uiState = state,
                        onTierSelected = vm::onTierSelected,
                        onPayWithMpesa = vm::onPayWithMpesa,
                        onPaymentDone = { backStack.removeLastOrNull() },
                    )
                }
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
