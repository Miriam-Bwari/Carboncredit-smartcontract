package dev.korryr.shambaguard.ui.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Entrance animations
    var isVisible by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutQuad),
        label = "Alpha Entrance"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutBack),
        label = "Scale Entrance"
    )

    // Infinite rotation for the inner dashed ring
    val infiniteTransition = rememberInfiniteTransition(label = "Ring Spin")
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Ring Rotation"
    )

    // Pulse animation for the outer ring
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Ring Pulse"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        // Delay before navigating away to the main app graph
        delay(2500)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ShambaGreen800,
                        ShambaGreen900
                    ),
                    center = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    radius = 2000f // Large radius to cover screen
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative rings
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Outer static pulsing ring
                    drawCircle(
                        color = ShambaRingLight,
                        radius = 160.dp.toPx() * pulseAnim,
                        center = center,
                        style = Stroke(
                            width = 1.dp.toPx()
                        )
                    )

                    // Inner static solid ring
                    drawCircle(
                        color = ShambaRingMid,
                        radius = 120.dp.toPx(),
                        center = center,
                        style = Stroke(
                            width = 2.dp.toPx()
                        )
                    )
                }
        )

        // Animated rotating dashed ring
        Box(
            modifier = Modifier
                .size(240.dp) // 120dp radius
                .rotate(rotationAnim)
                .drawBehind {
                    drawCircle(
                        color = ShambaGreen400.copy(alpha = 0.5f),
                        radius = 120.dp.toPx(),
                        center = Offset(size.width / 2, size.height / 2),
                        style = Stroke(
                            width = 4.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(20f, 40f), // Dash on, dash off
                                phase = 0f
                            )
                        )
                    )
                }
        )

        // Main centred content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shamba_globe),
                contentDescription = "Shamba Guard Satellite Globe",
                tint = Color.Unspecified, // Uses original colors from vector
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SHAMBA",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = ShambaOnDark
                )
            )
            Text(
                text = "GUARD",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 12.sp,
                    color = ShambaGreen400
                ),
                modifier = Modifier.padding(start = 6.dp) // Optical alignment
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Minimalist loading indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spinning leaf/satellite arc indicator
                CircularProgressIndicatorCustom()
                Text(
                    text = "ESTABLISHING LINK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = ShambaRingMid
                    )
                )
            }
        }
    }
}

// Custom subtle progress indicator to match brand
@Composable
private fun CircularProgressIndicatorCustom() {
    val infiniteTransition = rememberInfiniteTransition(label = "Loader Spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Loader Rotation"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .rotate(rotation)
            .drawBehind {
                drawArc(
                    color = ShambaGreen400,
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
    )
}
