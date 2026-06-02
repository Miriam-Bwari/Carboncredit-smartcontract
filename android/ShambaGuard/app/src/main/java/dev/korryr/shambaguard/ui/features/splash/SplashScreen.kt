package dev.korryr.shambaguard.ui.features.splash

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.R
import dev.korryr.shambaguard.ui.theme.ShambaGreen400
import dev.korryr.shambaguard.ui.theme.ShambaGreen700
import dev.korryr.shambaguard.ui.theme.ShambaGreen800
import dev.korryr.shambaguard.ui.theme.ShambaGreen900
import dev.korryr.shambaguard.ui.theme.ShambaOnDark
import kotlinx.coroutines.delay

// Splash screen matching the Shamba Guard design spec
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutQuad),
        label = "Content Alpha"
    )

    val contentScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.88f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutBack),
        label = "Content Scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "Splash Animations")

    // Slow pulse for the outermost ring
    val outerPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Outer Pulse"
    )

    // Rotating dashed ring around icon
    val dashedRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Dashed Ring Rotation"
    )

    // Bottom loader spinner rotation
    val loaderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Loader Rotation"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2800)
        onSplashComplete()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background: deep forest green with diagonal light sweep
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    // Base deep green
                    drawRect(color = Color(0xFF0E3B1E))

                    // Diagonal radial light rays from top-right corner (subtle)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A5C2E).copy(alpha = 0.6f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.85f, size.height * 0.08f),
                            radius = size.width * 1.0f
                        )
                    )
                    // Second subtle ray cluster
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF166329).copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.7f, size.height * 0.05f),
                            radius = size.width * 0.7f
                        )
                    )

                    drawContent()
                }
        )

        // Concentric rings — centered on screen, behind everything
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val cx = size.width / 2f
                    // Position rings slightly above center to align with the icon area
                    val cy = size.height * 0.42f

                    val ringColor = Color.White.copy(alpha = 0.07f)

                    // Draw 4 concentric rings of increasing size
                    val radii = listOf(
                        80.dp.toPx() * outerPulse,
                        130.dp.toPx() * outerPulse,
                        185.dp.toPx() * outerPulse,
                        245.dp.toPx() * outerPulse
                    )
                    radii.forEach { radius ->
                        drawCircle(
                            color = ringColor,
                            radius = radius,
                            center = Offset(cx, cy),
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                    }

                    // One slightly brighter innermost ring around icon
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = 54.dp.toPx(),
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
        )

        // Rotating dashed ring — positions near top 42% to align with icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val cx = size.width / 2f
                    val cy = size.height * 0.42f

                    // Outer dashed rotating ring
                    val dashEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(12f, 24f),
                        phase = dashedRingRotation * 36f
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = 160.dp.toPx(),
                        center = Offset(cx, cy),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    )
                }
        )

        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .scale(contentScale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top spacer — icon appears roughly 40% from top
            Spacer(modifier = Modifier.weight(0.35f))

            // Globe icon in frosted circular container
            GlobeIconContainer()

            Spacer(modifier = Modifier.height(32.dp))

            // App name and tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Shamba Guard",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ShambaOnDark,
                        letterSpacing = 0.sp,
                        fontSize = 36.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your farm, watched from space.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 0.5.sp,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.weight(0.55f))

            // Bottom loading indicator
            BottomLoader(loaderRotation = loaderRotation)

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

// Frosted glass circle container with globe icon
@Composable
private fun GlobeIconContainer() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2A6B3C).copy(alpha = 0.9f),
                        Color(0xFF1A4D2A).copy(alpha = 0.95f)
                    )
                )
            )
            // Soft white border
            .drawBehind {
                drawCircle(
                    color = Color.White.copy(alpha = 0.18f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_shamba_globe),
            contentDescription = "Shamba Guard Globe",
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp)
        )
    }
}

// Bottom "ESTABLISHING LINK" loading indicator
@Composable
private fun BottomLoader(loaderRotation: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Circular spinner with leaf inside
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
            // Arc spinner
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .rotate(loaderRotation)
                    .drawBehind {
                        // Background circle (dim ring)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.10f),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                        // Spinning arc
                        drawArc(
                            color = Color.White.copy(alpha = 0.70f),
                            startAngle = 0f,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
            )
            // Leaf icon in the centre of the spinner
            Icon(
                painter = painterResource(id = R.drawable.ic_shamba_globe),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(16.dp)
            )
        }

        // Label
        Text(
            text = "ESTABLISHING LINK",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp,
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp
            )
        )
    }
}
