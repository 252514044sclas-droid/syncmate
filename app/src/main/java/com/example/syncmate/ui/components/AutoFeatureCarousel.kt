package com.example.syncmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.syncmate.ui.theme.AmberWarning
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.RoseError
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class FeatureSlide(
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeColor: Color,
    val icon: ImageVector,
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
            onClick = onNavigateToFairRent
        ),
        FeatureSlide(
            title = "AI Scam & Fraud Shield",
            subtitle = "Detect advance-payment scams, wire-transfer fraud, and price anomalies before paying.",
            badgeText = "SCAM SHIELD",
            badgeColor = RoseError,
            icon = Icons.Default.Security,
            onClick = onNavigateToFakeListing
        ),
        FeatureSlide(
            title = "Legal Lease PDF Review",
            subtitle = "Scan sublease contracts for auto-renewal traps, mandatory cleaning fees, and notice periods.",
            badgeText = "LEGAL SCANNER",
            badgeColor = AmberWarning,
            icon = Icons.Default.Description,
            onClick = onNavigateToContractGuard
        ),
        FeatureSlide(
            title = "Verified .edu Student Network",
            subtitle = "Connect safely with verified university peers and match study & living rhythm.",
            badgeText = "VERIFIED PEERS",
            badgeColor = EmeraldSuccess,
            icon = Icons.Default.Verified,
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
            .padding(18.dp)
    ) {
        Column {
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
