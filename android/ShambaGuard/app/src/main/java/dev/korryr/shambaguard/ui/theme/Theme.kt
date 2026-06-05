package dev.korryr.shambaguard.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ShambaGuard Material 3 colour schemes
// All roles map to ShambaGuard brand tokens from Color.kt.
// Dynamic colour (Monet) is intentionally disabled so the
// brand palette is always enforced.

private val ShambaLightColorScheme = lightColorScheme(
    // Primary (Forest green)
    primary             = Green40,
    onPrimary           = White,
    primaryContainer    = Green90,
    onPrimaryContainer  = Green10,

    // Secondary (Teal)
    secondary           = Teal40,
    onSecondary         = White,
    secondaryContainer  = Teal90,
    onSecondaryContainer = Teal30,

    // Tertiary (Amber for alerts/harvest)
    tertiary            = Color(0xFF7B5800),
    onTertiary          = White,
    tertiaryContainer   = Color(0xFFFFDEA5),
    onTertiaryContainer = Color(0xFF271900),

    // Error
    error               = ShambaRed,
    onError             = White,
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002),

    // Background & Surface
    background          = Cream99,
    onBackground        = Grey10,
    surface             = Cream99,
    onSurface           = Grey10,
    surfaceVariant      = Green95,
    onSurfaceVariant    = Grey30,
    surfaceTint         = Green40,

    // Outline
    outline             = Grey50,
    outlineVariant      = Grey80,

    // Inverse
    inverseSurface      = Grey10,
    inverseOnSurface    = Grey90,
    inversePrimary      = Green80,
)

private val ShambaDarkColorScheme = darkColorScheme(
    // Primary
    primary             = Green80,
    onPrimary           = Green20,
    primaryContainer    = Green30,
    onPrimaryContainer  = Green90,

    // Secondary
    secondary           = Teal80,
    onSecondary         = Teal30,
    secondaryContainer  = Color(0xFF1F5F5B),
    onSecondaryContainer = Teal90,

    // Tertiary
    tertiary            = Color(0xFFFFBA27),
    onTertiary          = Color(0xFF402D00),
    tertiaryContainer   = Color(0xFF5C4100),
    onTertiaryContainer = Color(0xFFFFDEA5),

    // Error
    error               = Color(0xFFFFB4AB),
    onError             = Color(0xFF690005),
    errorContainer      = Color(0xFF93000A),
    onErrorContainer    = Color(0xFFFFDAD6),

    // Background & Surface
    background          = Green10,
    onBackground        = Grey90,
    surface             = Green10,
    onSurface           = Grey90,
    surfaceVariant      = Green20,
    onSurfaceVariant    = Grey80,
    surfaceTint         = Green80,

    // Outline
    outline             = Grey50,
    outlineVariant      = Grey30,

    // Inverse
    inverseSurface      = Grey90,
    inverseOnSurface    = Grey10,
    inversePrimary      = Green40,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShambaGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ShambaDarkColorScheme else ShambaLightColorScheme

    // Make the system status bar transparent & adapt icon tint to theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content,
        motionScheme = MotionScheme.expressive(),
    )
}
