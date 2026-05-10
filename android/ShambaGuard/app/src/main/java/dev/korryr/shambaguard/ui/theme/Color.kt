package dev.korryr.shambaguard.ui.theme

import androidx.compose.ui.graphics.Color

// ShambaGuard Brand Palette — raw tokens
// Named by hue + lightness so they are self-documenting.

// Forest greens
val Green10  = Color(0xFF002108) // Deepest forest — dark bg base
val Green20  = Color(0xFF003912) // Dark surface variant
val Green30  = Color(0xFF0D3B1A) // Splash background dark
val Green40  = Color(0xFF135223) // Primary dark (Light scheme primary)
val Green50  = Color(0xFF1B6B2E) // Brand green mid
val Green60  = Color(0xFF237A38) // Mid green
val Green70  = Color(0xFF2E9647) // Accent
val Green80  = Color(0xFF4AB862) // Light accent / container highlight
val Green90  = Color(0xFFB8EFC7) // Primary container (Light scheme)
val Green95  = Color(0xFFD4EDDA) // Surface tint light
val Green99  = Color(0xFFF0FBF2) // Near-white surface

// Onboarding card teal (the illustration card colour from the reference)
val Teal30   = Color(0xFF1F5F5B)
val Teal40   = Color(0xFF2D7E79) // Card bg teal
val Teal80   = Color(0xFF8FD4CE) // Teal container highlight
val Teal90   = Color(0xFFBBEDE9) // Teal surface tint

// Onboarding background — warm cream
val Cream98  = Color(0xFFF5F0E8) // Page background (matches reference)
val Cream99  = Color(0xFFFAF7F2)

// Semantic / neutral
val White    = Color(0xFFFFFFFF)
val Black    = Color(0xFF000000)
val Grey10   = Color(0xFF1A1C18)
val Grey30   = Color(0xFF44483D)
val Grey50   = Color(0xFF74796C)
val Grey80   = Color(0xFFC3C8BB)
val Grey90   = Color(0xFFE1E4D9)
val Grey99   = Color(0xFFF8FAF1)

// Alert / status
val ShambaRed   = Color(0xFFB00020)
val ShambaAmber = Color(0xFFF59E0B)

// Legacy aliases (kept so SplashScreen.kt compiles without changes)
val ShambaGreen900 = Green30
val ShambaGreen800 = Green40
val ShambaGreen700 = Green50
val ShambaGreen600 = Green60
val ShambaGreen500 = Green70
val ShambaGreen400 = Green80
val ShambaGreen100 = Green95
val ShambaRingLight = Color(0x1AFFFFFF)
val ShambaRingMid   = Color(0x26FFFFFF)
val ShambaOnDark    = White
val ShambaOnLight   = Green30

// Legacy Material defaults (kept so Theme.kt doesn't break)
val Purple80     = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80       = Color(0xFFEFB8C8)
val Purple40     = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40       = Color(0xFF7D5260)