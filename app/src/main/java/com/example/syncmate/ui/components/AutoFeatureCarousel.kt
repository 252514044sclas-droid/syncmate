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
import com.example.syncmate.ui.theme.SlateCard
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
            onClick = onNavigateToFairRent
        ),
        FeatureSlide(
            title = "AI Scam & Fraud Shield",
            subtitle = "Detect advance-payment scams, wire-transfer fraud, and price anomalies before paying.",
            badgeText = "SCAM SHIELD",
            badgeColor = RoseError,
            icon = Icons.Default.Security,
            category = SlideCategory.SCAM_SHIELD,
            onClick = onNavigateToFakeListing
        ),
        FeatureSlide(
            title = "Legal Lease PDF Review",
            subtitle = "Scan sublease contracts for auto-renewal traps, mandatory cleaning fees, and notice periods.",
            badgeText = "LEGAL SCANNER",
            badgeColor = AmberWarning,
            icon = Icons.Default.Description,
            category = SlideCategory.LEGAL_SCANNER,
            onClick = onNavigateToContractGuard
        ),
        FeatureSlide(
            title = "Verified .edu Student Network",
            subtitle = "Connect safely with verified university peers and match study & living rhythm.",
            badgeText = "VERIFIED PEERS",
            badgeColor = EmeraldSuccess,
            icon = Icons.Default.Verified,
            category = SlideCategory.VERIFIED_NETWORK,
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
                            if (isSelected) Color(0xFF00E5FF) else TextSecondary.copy(alpha = 0.3f)
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
            .background(SlateCard)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        slide.badgeColor.copy(alpha = 0.5f),
                        SlateCardBorder.copy(alpha = 0.2f)
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
                        .background(slide.badgeColor.copy(alpha = 0.15f)),
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
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (category) {
            SlideCategory.ML_ANALYTICS -> {
                // Animated Sine Wave & Bar Chart Graphic
                val path = Path()
                path.moveTo(width * 0.4f, height * 0.8f)

                for (x in (width * 0.4f).toInt()..(width.toInt()) step 10) {
                    val y = height * 0.6f + sin((x + animProgress * 100) * 0.03f) * 18f
                    path.lineTo(x.toFloat(), y.toFloat())
                }

                drawPath(
                    path = path,
                    color = accentColor.copy(alpha = 0.18f),
                    style = Stroke(width = 4f)
                )

                // Animated bar chart columns
                val barWidth = 14f
                val spacing = 22f
                val startX = width * 0.65f
                for (i in 0..4) {
                    val barHeight = (30f + i * 15f + sin(animProgress * 3.14f + i) * 12f).coerceAtLeast(10f)
                    drawRect(
                        color = accentColor.copy(alpha = 0.12f),
                        topLeft = Offset(startX + i * spacing, height * 0.85f - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
            }

            SlideCategory.SCAM_SHIELD -> {
                // Animated Radar Sonar Pulsing Circles
                val center = Offset(width * 0.85f, height * 0.5f)
                val maxRadius = 110f
                val radius1 = (maxRadius * animProgress)
                val radius2 = (maxRadius * ((animProgress + 0.5f) % 1f))

                drawCircle(
                    color = accentColor.copy(alpha = (1f - animProgress / 1f) * 0.18f),
                    radius = radius1,
                    center = center,
                    style = Stroke(width = 3f)
                )

                drawCircle(
                    color = accentColor.copy(alpha = (1f - ((animProgress + 0.5f) % 1f)) * 0.18f),
                    radius = radius2,
                    center = center,
                    style = Stroke(width = 3f)
                )
            }

            SlideCategory.LEGAL_SCANNER -> {
                // Animated Scanning Beam Line Moving Up & Down
                val scanLineY = height * 0.2f + (height * 0.6f * animProgress)

                drawLine(
                    color = accentColor.copy(alpha = 0.22f),
                    start = Offset(width * 0.35f, scanLineY),
                    end = Offset(width * 0.95f, scanLineY),
                    strokeWidth = 3f
                )

                // Document outline lines
                val startX = width * 0.55f
                for (i in 0..3) {
                    val lineY = height * 0.3f + i * 18f
                    drawLine(
                        color = accentColor.copy(alpha = 0.08f),
                        start = Offset(startX, lineY),
                        end = Offset(startX + 120f, lineY),
                        strokeWidth = 6f
                    )
                }
            }

            SlideCategory.VERIFIED_NETWORK -> {
                // Animated Constellation Network Nodes & Orbiting Particles
                val center = Offset(width * 0.8f, height * 0.45f)
                val orbitRadius = 45f

                drawCircle(
                    color = accentColor.copy(alpha = 0.12f),
                    radius = orbitRadius,
                    center = center,
                    style = Stroke(width = 2f)
                )

                val angle1 = (animProgress * 6.28f)
                val angle2 = (animProgress * 6.28f + 3.14f)

                val node1 = Offset(center.x + (orbitRadius * cos(angle1)).toFloat(), center.y + (orbitRadius * sin(angle1)).toFloat())
                val node2 = Offset(center.x + (orbitRadius * cos(angle2)).toFloat(), center.y + (orbitRadius * sin(angle2)).toFloat())

                drawLine(color = accentColor.copy(alpha = 0.15f), start = center, end = node1, strokeWidth = 2f)
                drawLine(color = accentColor.copy(alpha = 0.15f), start = center, end = node2, strokeWidth = 2f)

                drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 6f, center = node1)
                drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 6f, center = node2)
                drawCircle(color = accentColor.copy(alpha = 0.5f), radius = 8f, center = center)
            }
        }
    }
}
