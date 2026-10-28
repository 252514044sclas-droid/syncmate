package com.example.syncmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 16,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val borderLowColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryAccent = MaterialTheme.colorScheme.primary

    val shape = RoundedCornerShape(cornerRadiusDp.dp)

    val baseModifier = modifier
        .clip(shape)
        .background(surfaceColor)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    primaryAccent.copy(alpha = 0.35f),
                    borderLowColor.copy(alpha = 0.4f)
                )
            ),
            shape = shape
        )

    val finalModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else {
        baseModifier
    }

    Box(
        modifier = finalModifier,
        content = content
    )
}
