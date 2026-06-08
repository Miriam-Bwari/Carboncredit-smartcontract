package dev.korryr.shambaguard.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.features.admin.view.AdminHomeScreen
import dev.korryr.shambaguard.ui.features.admin.view.AgentManagementScreen
import dev.korryr.shambaguard.ui.features.admin.view.FarmMapScreen
import dev.korryr.shambaguard.ui.features.admin.view.PoolHealthScreen
import dev.korryr.shambaguard.ui.features.agent.presentation.AgentDashboardViewModel
import dev.korryr.shambaguard.ui.features.agent.view.AgentDashboardScreen
import dev.korryr.shambaguard.ui.features.agent.view.SyncStatusScreen
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmBoundaryViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.FarmPracticesViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.LoginViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.RegistrationViewModel
import dev.korryr.shambaguard.ui.features.auth.presentation.RoleSelectionViewModel
import dev.korryr.shambaguard.ui.features.auth.view.AgentPendingScreen
import dev.korryr.shambaguard.ui.features.auth.view.FarmBoundaryScreen
import dev.korryr.shambaguard.ui.features.auth.view.FarmPracticesScreen
import dev.korryr.shambaguard.ui.features.auth.view.LoginScreen
import dev.korryr.shambaguard.ui.features.auth.view.RegistrationStep1Screen
import dev.korryr.shambaguard.ui.features.auth.view.RoleSelectionScreen
import dev.korryr.shambaguard.ui.features.farmer.presentation.CarbonViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.DroughtViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerDashboardViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.FarmerProfileViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.MyFarmViewModel
import dev.korryr.shambaguard.ui.features.farmer.presentation.PolicyViewModel
import dev.korryr.shambaguard.ui.features.farmer.view.CarbonScreen
import dev.korryr.shambaguard.ui.features.farmer.view.CoverageStatusScreen
import dev.korryr.shambaguard.ui.features.farmer.view.DroughtInsightsScreen
import dev.korryr.shambaguard.ui.features.farmer.view.EarlyWarningScreen
import dev.korryr.shambaguard.ui.features.farmer.view.FarmerDashboardScreen
import dev.korryr.shambaguard.ui.features.farmer.view.FarmerProfileScreen
import dev.korryr.shambaguard.ui.features.farmer.view.MyFarmScreen
import dev.korryr.shambaguard.ui.features.farmer.view.PayoutHistoryScreen
import dev.korryr.shambaguard.ui.features.farmer.view.PolicyScreen
import dev.korryr.shambaguard.ui.features.onboarding.OnboardingScreen
import dev.korryr.shambaguard.ui.features.onboarding.OnboardingViewModel
import dev.korryr.shambaguard.ui.features.splash.SplashScreen
import kotlinx.coroutines.delay

enum class UserRole {
    Admin, Agent, Farmer, Unauthenticated
}

@Composable
fun ShambaGuardNavGraph(
    modifier: Modifier = Modifier,
    role: UserRole = UserRole.Unauthenticated, // Always start at Login; use dev bypass to skip
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

    // Safe replacement: replaces all entries with a single new root key,
    // but never lets the list become momentarily empty (NavDisplay crash guard).
    fun navigateTo(key: Any) {
        if (backStack.isEmpty()) {
            backStack.add(key)
        } else {
            val size = backStack.size
            backStack.add(key)          // add new root first
            repeat(size) { backStack.removeAt(0) } // then remove old entries from front
        }
    }

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
                        navigateTo(key)
                    },
                )
            }
        },
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

                    SplashScreen(
                        onSplashComplete = {}, // Navigation is driven by the LaunchedEffect below
                    )

                    // Single authoritative navigator: wait for DataStore AND the splash
                    // animation (2800 ms). Whichever finishes last wins.
                    // Hard timeout at 3500 ms guards against a frozen DataStore.
                    LaunchedEffect(Unit) {
                        val splashMs = 2800L
                        val timeoutMs = 3500L
                        val start = System.currentTimeMillis()

                        // Wait until onboardingDone resolves OR timeout
                        var elapsed = 0L
                        while (onboardingVm.onboardingCompleted.value == null && elapsed < timeoutMs) {
                            delay(50)
                            elapsed = System.currentTimeMillis() - start
                        }

                        // Ensure splash animation had its full time
                        val remaining = splashMs - elapsed
                        if (remaining > 0) delay(remaining)

                        // onboardingDone: null (timed out) → treat as first-launch
                        when (onboardingVm.onboardingCompleted.value) {
                            true -> navigateTo(initialKey)
                            else -> navigateTo(OnboardingKey)
                        }
                    }

                }

                // Onboarding
                entry<OnboardingKey> {
                    val onboardingVm: OnboardingViewModel = hiltViewModel()

                    OnboardingScreen(
                        onFinish = {
                            onboardingVm.markOnboardingDone()
                            navigateTo(RoleSelectionKey)
                        },
                    )
                }

                // Role Selection
                entry<RoleSelectionKey> {
                    val vm: RoleSelectionViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    RoleSelectionScreen(
                        uiState = state,
                        onRoleSelected = vm::onRoleSelected,
                        onContinue = { backStack.add(RegistrationKey) },
                    )
                }

                // Account creation (Farmer + Agent share this screen)
                entry<RegistrationKey> {
                    val roleVm: RoleSelectionViewModel = hiltViewModel()
                    val roleState by roleVm.uiState.collectAsStateWithLifecycle()
                    val selectedAppRole = roleState.selectedRole
                        ?: dev.korryr.shambaguard.ui.features.auth.presentation.AppUserRole.Farmer

                    val vm: RegistrationViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    val errName = stringResource(R.string.reg_error_full_name_empty)
                    val errPhone = stringResource(R.string.reg_error_phone_invalid)
                    val errCounty = stringResource(R.string.reg_error_county_empty)
                    val errShort = stringResource(R.string.reg_error_password_short)
                    val errMismatch = stringResource(R.string.reg_error_password_mismatch)

                    // Navigate on successful registration
                    androidx.compose.runtime.LaunchedEffect(state.successId) {
                        if (state.successId != null) {
                            vm.onNavigationConsumed()
                            backStack.removeLastOrNull()
                            when (selectedAppRole) {
                                dev.korryr.shambaguard.ui.features.auth.presentation.AppUserRole.Farmer ->
                                    backStack.add(FarmBoundaryKey)

                                dev.korryr.shambaguard.ui.features.auth.presentation.AppUserRole.Agent ->
                                    backStack.add(AgentPendingKey)
                            }
                        }
                    }

                    RegistrationStep1Screen(
                        uiState = state,
                        role = selectedAppRole,
                        onFullNameChanged = vm::onFullNameChanged,
                        onPhoneChanged = vm::onPhoneChanged,
                        onCountyChanged = vm::onCountyChanged,
                        onPasswordChanged = vm::onPasswordChanged,
                        onConfirmPasswordChanged = vm::onConfirmPasswordChanged,
                        onTogglePasswordVisibility = vm::onTogglePasswordVisibility,
                        onToggleConfirmPasswordVisibility = vm::onToggleConfirmPasswordVisibility,
                        onCreateAccount = {
                            vm.register(
                                role = selectedAppRole,
                                errorNameEmpty = errName,
                                errorPhoneInvalid = errPhone,
                                errorCountyEmpty = errCounty,
                                errorPasswordShort = errShort,
                                errorPasswordMismatch = errMismatch,
                            )
                        },
                        onSignInClicked = { navigateTo(LoginKey) },
                        onBack = { backStack.removeLastOrNull() },
                    )
                }

                // Agent waiting for admin approval
                entry<AgentPendingKey> {
                    AgentPendingScreen(
                        onGoToLogin = { navigateTo(LoginKey) },
                    )
                }

                // Farm setup Step 1: Draw farm polygon (Farmers only)
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

                // Farm setup Step 2: Farm practices (Farmers only)
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
                        onComplete = { backStack.add(FarmerPolicyKey) },
                        onBack = { backStack.removeLastOrNull() },
                    )
                }

                // Login
                entry<LoginKey> {
                    val vm: LoginViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    val errRequired = stringResource(R.string.login_error_fields_required)

                    LoginScreen(
                        uiState = state,
                        onPhoneChanged = vm::onPhoneChanged,
                        onPasswordChanged = vm::onPasswordChanged,
                        onRoleToggled = vm::onRoleToggled,
                        onTogglePasswordVisibility = vm::onTogglePasswordVisibility,
                        onLogin = { vm.login(errRequired) },
                        onLoginSuccess = { loggedInRole ->
                            vm.onNavigationConsumed()
                            val homeKey = when (loggedInRole) {
                                UserRole.Admin -> AdminHomeKey
                                UserRole.Agent -> AgentHomeKey
                                else -> FarmerHomeKey
                            }
                            navigateTo(homeKey)
                        },
                        onSignUpClicked = { navigateTo(RoleSelectionKey) },
                    )

                    // ─────────────────────────────────────────────────────────
                    // DEV BYPASS — compiled out in release/production builds
                    // ─────────────────────────────────────────────────────────
                    if (dev.korryr.shambaguard.BuildConfig.DEBUG) {
                        DevBypassPanel(
                            onBypass = { selectedRole ->
                                vm.devBypass(selectedRole)
                            }
                        )
                    }
                }


                // Admin screens
                entry<AdminHomeKey> {
                    AdminHomeScreen(
                        onNavigateToAgents = { backStack.add(AdminAgentsKey) },
                        onNavigateToMap = { backStack.add(AdminMapKey) },
                        onNavigateToPool = { backStack.add(AdminPoolKey) },
                    )
                }
                entry<AdminMapKey> { FarmMapScreen(onNavigateBack = { backStack.removeLastOrNull() }) }
                entry<AdminAgentsKey> { AgentManagementScreen(onNavigateBack = { backStack.removeLastOrNull() }) }
                entry<AdminPoolKey> { PoolHealthScreen(onNavigateBack = { backStack.removeLastOrNull() }) }

                // Agent screens
                entry<AgentHomeKey> {
                    val vm: AgentDashboardViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    AgentDashboardScreen(
                        uiState = state,
                        onRegisterFarmer = { backStack.add(RegistrationKey) },
                        onSyncNow = vm::onSyncNow,
                        onFilterToggled = vm::onFilterToggled,
                        onFarmerClicked = { /* AgentFarmerDetailKey(it) — Week 5 */ },
                    )
                }
                entry<AgentFarmersKey> { PlaceholderScreen(role, "Agent Farmers Management") }
                entry<AgentSyncKey> { SyncStatusScreen(onNavigateBack = { backStack.removeLastOrNull() }) }

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
                    val vm: DroughtViewModel = hiltViewModel()
                    val state by vm.warningState.collectAsStateWithLifecycle()

                    EarlyWarningScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() },
                        onFullAnalysis = { backStack.add(FarmerDroughtInsightsKey) },
                    )
                }
                entry<FarmerMyFarmKey> {
                    val vm: MyFarmViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    MyFarmScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() },
                        onAddPractice = { vm.onShowAddPracticeDialog(true) },
                        onSubmitPractice = { tillage, trees, irrigation -> 
                            vm.submitPractice(tillage, trees, irrigation) 
                        },
                        onDismissDialog = { vm.onShowAddPracticeDialog(false) },
                        onViewOnMap = {},
                    )
                }
                entry<FarmerCarbonKey> {
                    val vm: CarbonViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    CarbonScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() },
                        onSellCredits = {},
                        onViewAllEarnings = {},
                    )
                }
                entry<FarmerProfileKey> {
                    val vm: FarmerProfileViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    FarmerProfileScreen(
                        uiState = state,
                        onLanguageSelected = vm::onLanguageSelected,
                        onPushNotifications = vm::onPushNotificationsToggled,
                        onDroughtAlerts = vm::onDroughtAlertsToggled,
                        onBiometricToggled = vm::onBiometricToggled,
                        onChangePinClicked = {},
                        onPolicyDocsClicked = {},
                        onPrivacyPolicyClicked = {},
                        onSignOut = { navigateTo(LoginKey) },
                    )
                }
                // Non-tab screens — navigated from dashboard or tabs
                entry<FarmerDroughtInsightsKey> {
                    val vm: DroughtViewModel = hiltViewModel()
                    val state by vm.insightsState.collectAsStateWithLifecycle()

                    DroughtInsightsScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
                entry<FarmerCoverageKey> {
                    val vm: DroughtViewModel = hiltViewModel()
                    val state by vm.coverageState.collectAsStateWithLifecycle()

                    CoverageStatusScreen(
                        uiState = state,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
                entry<FarmerPolicyKey> {
                    val vm: PolicyViewModel = hiltViewModel()
                    val state by vm.uiState.collectAsStateWithLifecycle()

                    PolicyScreen(
                        uiState = state,
                        onTierSelected = vm::onTierSelected,
                        onPayWithMpesa = vm::onPayWithMpesa,
                        onPaymentDone = { navigateTo(initialKey) },
                    )
                }
                entry<FarmerPayoutsKey> { PayoutHistoryScreen(onNavigateBack = { backStack.removeLastOrNull() }) }
            },
        )
    }
}


@Composable
fun DevBypassPanel(onBypass: (UserRole) -> Unit) {
    val amber = Color(0xFFFFC107)
    val bgColor = Color(0xFF1A1200)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            border = BorderStroke(1.dp, amber.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "⚠  DEV BYPASS — DEBUG ONLY",
                    style = MaterialTheme.typography.labelMedium,
                    color = amber,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        Triple("🌾", "Farmer", UserRole.Farmer),
                        Triple("🛡", "Agent",  UserRole.Agent),
                        Triple("🔑", "Admin",  UserRole.Admin),
                    ).forEach { (icon, label, role) ->
                        Button(
                            onClick = { onBypass(role) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = amber.copy(alpha = 0.15f),
                                contentColor   = amber
                            ),
                            border = BorderStroke(1.dp, amber.copy(alpha = 0.4f)),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = icon, style = MaterialTheme.typography.titleMedium)
                                Text(text = label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(role: UserRole, title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val roleColor = when (role) {
            UserRole.Admin -> Color(0xFFE53935)
            UserRole.Agent -> Color(0xFF1E88E5)
            UserRole.Farmer -> Color(0xFF43A047)
            else -> Color.Gray
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = roleColor.copy(alpha = 0.1f)),
            border = BorderStroke(2.dp, roleColor),
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Role: ${role.name.uppercase()}",
                    style = MaterialTheme.typography.titleLarge,
                    color = roleColor,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = "This screen is currently under construction.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreenWithAction(title: String, buttonText: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = onClick) {
            Text(text = buttonText)
        }
    }
}
