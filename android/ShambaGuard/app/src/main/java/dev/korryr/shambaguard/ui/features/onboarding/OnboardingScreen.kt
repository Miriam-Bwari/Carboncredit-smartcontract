package dev.korryr.shambaguard.ui.features.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.korryr.shambaguard.ui.theme.Cream98
import dev.korryr.shambaguard.ui.theme.Green40
import dev.korryr.shambaguard.ui.theme.Green90
import dev.korryr.shambaguard.ui.theme.ShambaRed
import dev.korryr.shambaguard.ui.theme.Teal40
import kotlinx.coroutines.launch

// Onboarding Screen
// Matches the ARM Advice reference design with ShambaGuard branding.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Cream98)
    ) {

        // Skip button
        TextButton(
            onClick = onFinish,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 8.dp),
        ) {
            Text(
                text  = "Skip",
                color = ShambaRed,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                )
            )
        }

        // Page content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Swipeable pages
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { pageIndex ->
                OnboardingPageContent(
                    page           = pages[pageIndex],
                    isCurrentPage  = pagerState.currentPage == pageIndex,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                repeat(pages.size) { index ->
                    PageDot(isSelected = pagerState.currentPage == index)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA button
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green40,
                    contentColor   = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp,
                )
            ) {
                Text(
                    text  = if (isLastPage) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// Single page content
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isCurrentPage: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue  = if (isCurrentPage) 1f else 0.92f,
        animationSpec = tween(400, easing = EaseOutBack),
        label        = "Page Scale",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Teal illustration card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Teal40,
                            Color(0xFF245C57),
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Illustration
            Image(
                painter           = painterResource(page.illustrationRes),
                contentDescription = page.title,
                modifier          = Modifier.size(180.dp),
                contentScale      = ContentScale.Fit,
            )

            // Card label at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = page.cardLabel,
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.5.sp,
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Headline
        Text(
            text      = page.title,
            style     = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = Color(0xFF0D3B1A),
                lineHeight = 38.sp,
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text      = page.description,
            style     = MaterialTheme.typography.bodyMedium.copy(
                color      = Color(0xFF44483D),
                lineHeight = 22.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

// Animated page dot indicator
@Composable
private fun PageDot(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        targetValue  = if (isSelected) 28.dp else 8.dp,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label        = "Dot Width",
    )
    val color by animateColorAsState(
        targetValue  = if (isSelected) Green40 else Green90,
        animationSpec = tween(300),
        label        = "Dot Color",
    )

    Box(
        modifier = modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}
