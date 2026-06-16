package dev.korryr.shambaguard.ui.features.onboarding

import androidx.annotation.DrawableRes
import dev.korryr.shambaguard.R

// Onboarding page data model
data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val illustrationRes: Int,
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Know drought is\ncoming 14 days\nearly",
        description = "Get precise satellite data and planting advice directly on your phone to protect your harvest.",
        illustrationRes = R.drawable.satellite_data,
    ),
    OnboardingPage(
        title = "Get paid automatically when drought hits",
        description = "Receive fast, automated M-Pesa payouts directly to your phone when conditions threaten your crops.",
        illustrationRes = R.drawable.onboard_2,
    ),
    OnboardingPage(
        title = "Earn income from protecting your land",
        description = "Generate carbon credits through sustainable practices and receive direct cash payments.",
        illustrationRes = R.drawable.onboard_3,
    ),
)
