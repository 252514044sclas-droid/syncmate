package com.example.syncmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder

fun Modifier.glassCard(
    cornerRadiusDp: Int = 16,
    alpha: Float = 0.85f
) = this
    .clip(RoundedCornerShape(cornerRadiusDp.dp))
    .background(SlateCard.copy(alpha = alpha))
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                IndigoPrimary.copy(alpha = 0.45f),
                SlateCardBorder.copy(alpha = 0.2f)
            )
        ),
        shape = RoundedCornerShape(cornerRadiusDp.dp)
    )

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 16,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .glassCard(cornerRadiusDp)
            .clickable { onClick() }
    } else {
        modifier.glassCard(cornerRadiusDp)
    }

    Box(
        modifier = cardModifier,
        content = content
    )
}
