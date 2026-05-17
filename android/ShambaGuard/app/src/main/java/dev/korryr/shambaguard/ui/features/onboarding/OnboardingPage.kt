package dev.korryr.shambaguard.ui.features.onboarding

import androidx.annotation.DrawableRes
import dev.korryr.shambaguard.R

// Onboarding page data model
data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val illustrationRes: Int,
    val cardLabel: String,     // small all-caps label inside the teal card
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Know drought is\ncoming 14 days\nearly",
        description = "Get precise satellite data and planting advice directly on your phone to protect your harvest.",
        illustrationRes = R.drawable.ic_onboard_drought,
        cardLabel = "SATELLITE DATA",
    ),
    OnboardingPage(
        title = "Monitor your soil\nhealth in real\ntime",
        description = "Track moisture levels and crop health with NDVI alerts before problems reach your field.",
        illustrationRes = R.drawable.ic_onboard_soil,
        cardLabel = "SOIL MONITORING",
    ),
    OnboardingPage(
        title = "Connect with\ncertified agri-\nagents near you",
        description = "Get expert advisory and insurance support from verified agents in your local area.",
        illustrationRes = R.drawable.ic_onboard_agent,
        cardLabel = "AGENT NETWORK",
    ),
)
