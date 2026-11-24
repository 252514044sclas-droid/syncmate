package com.example.syncmate.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance, hardware-accelerated ambient live animated background.
 *
 * Dark Mode: Pure #000000 black backdrop with subtle, very light white / warm beige glowing orbs and floating micro-particles.
 * Light Mode: Pure #FFFFFF white backdrop with subtle soft light grey / warm taupe ambient motion that preserves crisp text contrast.
 */
@Composable
fun AmbientLiveBackground(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientTransition")

    // Primary slow orbital drift (20 seconds cycle)
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalDrift1"
    )

    // Secondary counter-orbital drift (28 seconds cycle)
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalDrift2"
    )

    // Vertical particle float (12 seconds cycle)
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleFloat"
    )

    // Base background colors
    val backdropColor = if (darkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)

    // Pre-calculated seed points for particles
    val particles = remember {
        List(18) { index ->
            ParticleSeed(
                xRatio = ((index * 37 + 13) % 100) / 100f,
                yOffset = ((index * 59 + 29) % 100) / 100f,
                radius = 1.2f + (index % 4) * 0.7f,
                speedMultiplier = 0.7f + (index % 5) * 0.15f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backdropColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            if (canvasWidth <= 0 || canvasHeight <= 0) return@Canvas

            // ========================================================
            // 1. Drifting Soft Glowing Ambient Orbs
            // ========================================================
            if (darkTheme) {
                // --- Dark Mode Ambient Orbs (Soft White & Warm Sand Beige) ---
                val orb1X = canvasWidth * 0.25f + sin(phase1) * (canvasWidth * 0.18f)
                val orb1Y = canvasHeight * 0.22f + cos(phase1) * (canvasHeight * 0.12f)
                val orb1Radius = canvasWidth * 0.85f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.055f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = orb1Radius
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                )

                val orb2X = canvasWidth * 0.82f + cos(phase2) * (canvasWidth * 0.16f)
                val orb2Y = canvasHeight * 0.75f + sin(phase2) * (canvasHeight * 0.14f)
                val orb2Radius = canvasWidth * 0.95f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE5D5C0).copy(alpha = 0.045f), // Warm Sand Beige
                            Color(0xFFE5D5C0).copy(alpha = 0.015f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = orb2Radius
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                )

                val orb3X = canvasWidth * 0.5f + sin(phase2 * 0.7f) * (canvasWidth * 0.15f)
                val orb3Y = canvasHeight * 0.48f + cos(phase1 * 0.8f) * (canvasHeight * 0.10f)
                val orb3Radius = canvasWidth * 0.7f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(orb3X, orb3Y),
                        radius = orb3Radius
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                )
            } else {
                // --- Light Mode Ambient Orbs (Crisp Light Grey & Soft Warm Taupe) ---
                val orb1X = canvasWidth * 0.28f + sin(phase1) * (canvasWidth * 0.16f)
                val orb1Y = canvasHeight * 0.20f + cos(phase1) * (canvasHeight * 0.10f)
                val orb1Radius = canvasWidth * 0.85f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF9E9E9E).copy(alpha = 0.11f), // Soft Light Grey
                            Color(0xFF9E9E9E).copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = orb1Radius
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                )

                val orb2X = canvasWidth * 0.80f + cos(phase2) * (canvasWidth * 0.15f)
                val orb2Y = canvasHeight * 0.78f + sin(phase2) * (canvasHeight * 0.12f)
                val orb2Radius = canvasWidth * 0.95f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC8B094).copy(alpha = 0.09f), // Warm Taupe-Beige
                            Color(0xFFC8B094).copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = orb2Radius
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                )

                val orb3X = canvasWidth * 0.5f + cos(phase1 * 0.75f) * (canvasWidth * 0.12f)
                val orb3Y = canvasHeight * 0.50f + sin(phase2 * 0.8f) * (canvasHeight * 0.08f)
                val orb3Radius = canvasWidth * 0.70f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFBDBDBD).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(orb3X, orb3Y),
                        radius = orb3Radius
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                )
            }

            // ========================================================
            // 2. Faint Floating Micro-Particles
            // ========================================================
            val baseParticleColor = if (darkTheme) {
                Color.White.copy(alpha = 0.06f)
            } else {
                Color(0xFF9E9E9E).copy(alpha = 0.12f)
            }

            particles.forEach { p ->
                val currentYProgress = (p.yOffset - (particleProgress * p.speedMultiplier)) % 1f
                val normalizedY = if (currentYProgress < 0f) currentYProgress + 1f else currentYProgress
                val py = normalizedY * canvasHeight

                val horizontalWobble = sin(phase1 + p.xRatio * 10f) * 14f
                val px = (p.xRatio * canvasWidth + horizontalWobble).coerceIn(0f, canvasWidth)

                // Soft fade in near bottom and fade out near top
                val alphaMultiplier = when {
                    normalizedY < 0.15f -> normalizedY / 0.15f
                    normalizedY > 0.85f -> (1f - normalizedY) / 0.15f
                    else -> 1f
                }

                drawCircle(
                    color = baseParticleColor.copy(alpha = baseParticleColor.alpha * alphaMultiplier),
                    radius = p.radius * density,
                    center = Offset(px, py)
                )
            }
        }

        // Foreground application content
        content()
    }
}

private data class ParticleSeed(
    val xRatio: Float,
    val yOffset: Float,
    val radius: Float,
    val speedMultiplier: Float
)
