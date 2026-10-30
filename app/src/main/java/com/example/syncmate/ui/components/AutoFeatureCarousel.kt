package com.example.syncmate.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.syncmate.ui.theme.AmberWarning
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.RoseError
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

enum class SlideCategory {
    ML_ANALYTICS,
    SCAM_SHIELD,
    LEGAL_SCANNER,
    VERIFIED_NETWORK
}

data class FeatureSlide(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeColor: Color,
    val icon: ImageVector,
    val category: SlideCategory,
    val gradientColors: List<Color>,
    val onClick: () -> Unit
)

@Composable
fun AutoFeatureCarousel(
    onNavigateToFairRent: () -> Unit,
    onNavigateToFakeListing: () -> Unit,
    onNavigateToContractGuard: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = listOf(
        FeatureSlide(
            title = "ML Fair-Rent & Utility Engine",
            subtitle = "Calculate fair market rent & monthly utility bills in INR based on campus zone data.",
            badgeText = "ML POWERED",
            badgeColor = Color(0xFF00E5FF),
            icon = Icons.Default.Analytics,
            category = SlideCategory.ML_ANALYTICS,
            gradientColors = listOf(Color(0xFF064E3B), Color(0xFF0F172A), Color(0xFF065F46)),
            onClick = onNavigateToFairRent
        ),
        FeatureSlide(
            title = "AI Scam & Fraud Shield",
            subtitle = "Detect advance-payment scams, wire-transfer fraud, and price anomalies before paying.",
            badgeText = "SCAM SHIELD",
            badgeColor = RoseError,
            icon = Icons.Default.Security,
            category = SlideCategory.SCAM_SHIELD,
            gradientColors = listOf(Color(0xFF4C0519), Color(0xFF0F172A), Color(0xFF881337)),
            onClick = onNavigateToFakeListing
        ),
        FeatureSlide(
            title = "Legal Lease PDF Review",
            subtitle = "Scan sublease contracts for auto-renewal traps, mandatory cleaning fees, and notice periods.",
            badgeText = "LEGAL SCANNER",
            badgeColor = AmberWarning,
            icon = Icons.Default.Description,
            category = SlideCategory.LEGAL_SCANNER,
            gradientColors = listOf(Color(0xFF451A03), Color(0xFF0F172A), Color(0xFF78350F)),
            onClick = onNavigateToContractGuard
        ),
        FeatureSlide(
            title = "Verified .edu Student Network",
            subtitle = "Connect safely with verified university peers and match study & living rhythm.",
            badgeText = "VERIFIED PEERS",
            badgeColor = EmeraldSuccess,
            icon = Icons.Default.Verified,
            category = SlideCategory.VERIFIED_NETWORK,
            gradientColors = listOf(Color(0xFF022C22), Color(0xFF0F172A), Color(0xFF065F46)),
            onClick = onNavigateToPreferences
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-sliding timer effect: transitions slide every 3.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            val nextPage = (pagerState.currentPage + 1) % slides.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val slide = slides[page]
            CarouselSlideCard(slide = slide)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Page Indicator Dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slides.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (isSelected) 22.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (isSelected) slides[index].badgeColor else TextSecondary.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun CarouselSlideCard(
    slide: FeatureSlide
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = slide.gradientColors
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        slide.badgeColor.copy(alpha = 0.6f),
                        SlateCardBorder.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { slide.onClick() }
    ) {
        // Animated Background Graphic Canvas
        AnimatedCategoryBackground(
            category = slide.category,
            accentColor = slide.badgeColor
        )

        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(
                    text = slide.badgeText,
                    backgroundColor = slide.badgeColor
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(slide.badgeColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slide.icon,
                        contentDescription = slide.title,
                        tint = slide.badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = slide.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = slide.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2
            )
        }
    }
}

@Composable
fun AnimatedCategoryBackground(
    category: SlideCategory,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "category_anim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (category) {
            SlideCategory.ML_ANALYTICS -> {
                // Glowing radial background light
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(width * 0.8f, height * 0.3f),
                        radius = 140f
                    ),
                    radius = 140f,
                    center = Offset(width * 0.8f, height * 0.3f)
                )

                // Animated Sine Wave
                val path = Path()
                path.moveTo(width * 0.3f, height * 0.8f)
                for (x in (width * 0.3f).toInt()..(width.toInt()) step 8) {
                    val y = height * 0.55f + sin((x + animProgress * 120) * 0.04f) * 22f
                    path.lineTo(x.toFloat(), y)
                }
                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.35f),
                    style = Stroke(width = 5f)
                )

                // Animated Bar Chart Columns
                val barWidth = 12f
                val spacing = 20f
                val startX = width * 0.7f
                for (i in 0..4) {
                    val barHeight = (25f + i * 14f + sin(animProgress * 6.28f + i) * 15f).coerceAtLeast(10f)
                    drawRect(
                        color = accentColor.copy(alpha = 0.25f),
                        topLeft = Offset(startX + i * spacing, height * 0.85f - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
            }

            SlideCategory.SCAM_SHIELD -> {
                // Glowing radial danger light
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset(width * 0.85f, height * 0.5f),
                        radius = 160f
                    ),
                    radius = 160f,
                    center = Offset(width * 0.85f, height * 0.5f)
                )

                // Animated Radar Sonar Pulsing Circles
                val center = Offset(width * 0.85f, height * 0.5f)
                val maxRadius = 120f
                val radius1 = (maxRadius * animProgress)
                val radius2 = (maxRadius * ((animProgress + 0.5f) % 1f))

                drawCircle(
                    color = accentColor.copy(alpha = (1f - animProgress) * 0.4f),
                    radius = radius1,
                    center = center,
                    style = Stroke(width = 4f)
                )
                drawCircle(
                    color = accentColor.copy(alpha = (1f - ((animProgress + 0.5f) % 1f)) * 0.4f),
                    radius = radius2,
                    center = center,
                    style = Stroke(width = 4f)
                )
            }

            SlideCategory.LEGAL_SCANNER -> {
                // Glowing radial amber light
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(width * 0.75f, height * 0.4f),
                        radius = 150f
                    ),
                    radius = 150f,
                    center = Offset(width * 0.75f, height * 0.4f)
                )

                // Animated Scanning Beam Line
                val scanLineY = height * 0.15f + (height * 0.7f * animProgress)
                drawLine(
                    color = accentColor.copy(alpha = 0.5f),
                    start = Offset(width * 0.3f, scanLineY),
                    end = Offset(width * 0.95f, scanLineY),
                    strokeWidth = 4f
                )

                // Document Line Outlines
                val startX = width * 0.5f
                for (i in 0..3) {
                    val lineY = height * 0.25f + i * 20f
                    drawLine(
                        color = accentColor.copy(alpha = 0.15f),
                        start = Offset(startX, lineY),
                        end = Offset(startX + 130f, lineY),
                        strokeWidth = 6f
                    )
                }
            }

            SlideCategory.VERIFIED_NETWORK -> {
                // Glowing radial emerald light
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset(width * 0.8f, height * 0.45f),
                        radius = 160f
                    ),
                    radius = 160f,
                    center = Offset(width * 0.8f, height * 0.45f)
                )

                // Constellation Network Orbit
                val center = Offset(width * 0.8f, height * 0.45f)
                val orbitRadius = 50f

                drawCircle(
                    color = accentColor.copy(alpha = 0.25f),
                    radius = orbitRadius,
                    center = center,
                    style = Stroke(width = 2.5f)
                )

                val angle1 = (animProgress * 6.28f)
                val angle2 = (animProgress * 6.28f + 3.14f)

                val node1 = Offset(center.x + (orbitRadius * cos(angle1)).toFloat(), center.y + (orbitRadius * sin(angle1)).toFloat())
                val node2 = Offset(center.x + (orbitRadius * cos(angle2)).toFloat(), center.y + (orbitRadius * sin(angle2)).toFloat())

                drawLine(color = accentColor.copy(alpha = 0.3f), start = center, end = node1, strokeWidth = 2.5f)
                drawLine(color = accentColor.copy(alpha = 0.3f), start = center, end = node2, strokeWidth = 2.5f)

                drawCircle(color = accentColor.copy(alpha = 0.8f), radius = 7f, center = node1)
                drawCircle(color = accentColor.copy(alpha = 0.8f), radius = 7f, center = node2)
                drawCircle(color = accentColor.copy(alpha = 0.9f), radius = 10f, center = center)
            }
        }
    }
}
