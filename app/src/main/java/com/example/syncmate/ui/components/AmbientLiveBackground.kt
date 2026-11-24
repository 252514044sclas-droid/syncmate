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
 * Dark Mode: Pure #000000 pitch black backdrop with rich, visible drifting warm sand beige / champagne
 * glowing nebulas and floating twinkling micro-particles.
 * Light Mode: Pure #FFFFFF crisp white backdrop with subtle warm taupe & espresso ambient motion.
 */
@Composable
fun AmbientLiveBackground(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientTransition")

    // Primary slow orbital drift (16 seconds cycle)
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalDrift1"
    )

    // Secondary counter-orbital drift (22 seconds cycle)
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalDrift2"
    )

    // Breathing pulse for luminous core (7 seconds cycle)
    val phasePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseCycle"
    )

    // Vertical particle float (11 seconds cycle)
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleFloat"
    )

    // Base background colors
    val backdropColor = if (darkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)

    // 28 pre-calculated seed points for particles
    val particles = remember {
        List(28) { index ->
            ParticleSeed(
                xRatio = ((index * 37 + 13) % 100) / 100f,
                yOffset = ((index * 59 + 29) % 100) / 100f,
                radius = 1.6f + (index % 5) * 0.55f,
                speedMultiplier = 0.65f + (index % 5) * 0.16f,
                twinkleSpeed = 1.0f + (index % 3) * 0.5f,
                seed = index * 1.37f,
                isWarmBeige = (index % 2 == 0)
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
            // 1. Drifting Soft Glowing Ambient Nebulas
            // ========================================================
            if (darkTheme) {
                // --- Dark Mode: Pure Pitch Black with Warm Beige & Champagne Nebulas ---
                // Primary Warm Sand Beige Nebula (Top Left / Drifting)
                val orb1X = canvasWidth * 0.28f + sin(phase1) * (canvasWidth * 0.22f)
                val orb1Y = canvasHeight * 0.24f + cos(phase1) * (canvasHeight * 0.15f)
                val orb1Radius = canvasWidth * 0.95f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE5D5C0).copy(alpha = 0.18f), // Warm Sand Beige
                            Color(0xFFE5D5C0).copy(alpha = 0.08f),
                            Color(0xFFE5D5C0).copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = orb1Radius
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                )

                // Secondary Soft Champagne Nebula (Bottom Right / Drifting)
                val orb2X = canvasWidth * 0.78f + cos(phase2) * (canvasWidth * 0.20f)
                val orb2Y = canvasHeight * 0.76f + sin(phase2) * (canvasHeight * 0.16f)
                val orb2Radius = canvasWidth * 1.05f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFF2E8DC).copy(alpha = 0.15f), // Soft Champagne
                            Color(0xFFF2E8DC).copy(alpha = 0.06f),
                            Color(0xFFF2E8DC).copy(alpha = 0.015f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = orb2Radius
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                )

                // Third Luminous Breathing Core (Mid-Screen)
                val pulseAlpha = 0.09f + sin(phasePulse) * 0.035f // 0.055f to 0.125f breathing
                val orb3X = canvasWidth * 0.52f + sin(phase2 * 0.8f) * (canvasWidth * 0.16f)
                val orb3Y = canvasHeight * 0.48f + cos(phase1 * 0.75f) * (canvasHeight * 0.12f)
                val orb3Radius = canvasWidth * 0.80f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = pulseAlpha),
                            Color(0xFFE5D5C0).copy(alpha = pulseAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(orb3X, orb3Y),
                        radius = orb3Radius
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                )

                // Fourth Warm Accent Bloom (Lower-Mid Left)
                val orb4X = canvasWidth * 0.20f + cos(phase1 * 0.6f) * (canvasWidth * 0.14f)
                val orb4Y = canvasHeight * 0.88f + sin(phase2 * 0.6f) * (canvasHeight * 0.10f)
                val orb4Radius = canvasWidth * 0.85f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB8A895).copy(alpha = 0.13f), // Muted Taupe
                            Color(0xFFB8A895).copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(orb4X, orb4Y),
                        radius = orb4Radius
                    ),
                    center = Offset(orb4X, orb4Y),
                    radius = orb4Radius
                )
            } else {
                // --- Light Mode: Pure White with Warm Taupe & Espresso Nebulas ---
                val orb1X = canvasWidth * 0.28f + sin(phase1) * (canvasWidth * 0.18f)
                val orb1Y = canvasHeight * 0.20f + cos(phase1) * (canvasHeight * 0.12f)
                val orb1Radius = canvasWidth * 0.90f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC8B094).copy(alpha = 0.14f), // Warm Taupe
                            Color(0xFFC8B094).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = orb1Radius
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                )

                val orb2X = canvasWidth * 0.80f + cos(phase2) * (canvasWidth * 0.18f)
                val orb2Y = canvasHeight * 0.78f + sin(phase2) * (canvasHeight * 0.14f)
                val orb2Radius = canvasWidth * 1.0f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEDE2D2).copy(alpha = 0.16f), // Soft Cream
                            Color(0xFFEDE2D2).copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = orb2Radius
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                )
            }

            // ========================================================
            // 2. Floating Ambient Micro-Particles & Twinkling Stars
            // ========================================================
            particles.forEach { p ->
                val currentYProgress = (p.yOffset - (particleProgress * p.speedMultiplier)) % 1f
                val normalizedY = if (currentYProgress < 0f) currentYProgress + 1f else currentYProgress
                val py = normalizedY * canvasHeight

                // Horizontal harmonic wobble
                val horizontalWobble = sin(phase1 * 1.3f + p.seed * 3.5f) * (16f * density)
                val px = (p.xRatio * canvasWidth + horizontalWobble).coerceIn(0f, canvasWidth)

                // Soft fade in near bottom and fade out near top
                val edgeFade = when {
                    normalizedY < 0.12f -> normalizedY / 0.12f
                    normalizedY > 0.88f -> (1f - normalizedY) / 0.12f
                    else -> 1f
                }

                // Twinkle cycle
                val twinkle = (sin(phasePulse * p.twinkleSpeed + p.seed) + 1f) / 2f

                if (darkTheme) {
                    val baseColor = if (p.isWarmBeige) Color(0xFFE5D5C0) else Color(0xFFFAF7F2)
                    val particleAlpha = (0.24f + twinkle * 0.32f) * edgeFade // 0.24 to 0.56

                    // Outer soft halo for visible particles
                    if (p.radius > 2.0f) {
                        drawCircle(
                            color = baseColor.copy(alpha = particleAlpha * 0.28f),
                            radius = p.radius * 2.4f * density,
                            center = Offset(px, py)
                        )
                    }

                    // Bright core
                    drawCircle(
                        color = baseColor.copy(alpha = particleAlpha),
                        radius = p.radius * density,
                        center = Offset(px, py)
                    )
                } else {
                    val baseColor = if (p.isWarmBeige) Color(0xFFA68A68) else Color(0xFF8C7E72)
                    val particleAlpha = (0.16f + twinkle * 0.18f) * edgeFade

                    drawCircle(
                        color = baseColor.copy(alpha = particleAlpha),
                        radius = p.radius * density,
                        center = Offset(px, py)
                    )
                }
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
    val speedMultiplier: Float,
    val twinkleSpeed: Float,
    val seed: Float,
    val isWarmBeige: Boolean
)
