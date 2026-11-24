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
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Ultra-aesthetic, hardware-accelerated ambient live animated background.
 *
 * Theme Features:
 * 1. Deep Velvet Pitch Black Backdrop (#000000) in Dark Mode / Crisp Pure White (#FFFFFF) in Light Mode.
 * 2. Ethereal Liquid Silk Aurora: Large, soft-glowing warm sand beige & champagne gold nebulas
 *    drifting smoothly in organic Lissajous curves with gentle breathing radius.
 * 3. Living Constellation Network (SyncMate Matching Motif): Delicate celestial student nodes
 *    floating gently through space; whenever two nodes drift near each other, glowing hairline
 *    constellation connection threads smoothly illuminate and fade between them.
 * 4. Ambient Twinkling Stars & Bokeh Halos: Delicate stardust particles that shimmer and pulse.
 * 5. Occasional Ethereal Shooting Stars: Subtle diagonal light streaks gliding across the upper sky.
 */
@Composable
fun AmbientLiveBackground(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AestheticAmbientTransition")

    // Slow organic aurora orbital drift (20 seconds cycle)
    val auroraDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AuroraDrift"
    )

    // Secondary counter-orbital drift (28 seconds cycle)
    val secondaryDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SecondaryDrift"
    )

    // Gentle breathing pulse for the luminous nebula core (8 seconds cycle)
    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BreathPhase"
    )

    // Vertical constellation float (14 seconds cycle)
    val constellationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ConstellationProgress"
    )

    // Ethereal shooting star cycle (11 seconds periodic loop)
    val shootingStarProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShootingStar"
    )

    // Base background colors
    val backdropColor = if (darkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)

    // 22 Pre-calculated seed points for the constellation network
    val nodes = remember {
        List(22) { index ->
            ConstellationNodeSeed(
                xRatio = ((index * 47 + 19) % 100) / 100f,
                yOffset = ((index * 61 + 31) % 100) / 100f,
                speedMultiplier = 0.65f + (index % 5) * 0.14f,
                wobbleSpeed = 0.8f + (index % 4) * 0.35f,
                wobbleAmplitude = 12f + (index % 3) * 6f,
                radiusDp = 1.6f + (index % 4) * 0.55f,
                twinkleSpeed = 1.0f + (index % 3) * 0.5f,
                isStar = (index % 3 == 0),
                seed = index * 1.414f
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
            // 1. Ethereal Liquid Silk Aurora (Soft Breathing Nebulas)
            // ========================================================
            val breathScale = 1f + 0.12f * sin(breathPhase)

            if (darkTheme) {
                // --- Dark Mode: Deep Pitch Black with Warm Sand Beige & Champagne Glows ---
                // Primary Warm Sand Beige Aurora Nebula (Upper-Left Drifting)
                val aurora1X = canvasWidth * 0.28f + sin(auroraDrift) * (canvasWidth * 0.20f)
                val aurora1Y = canvasHeight * 0.24f + cos(auroraDrift * 0.8f) * (canvasHeight * 0.14f)
                val aurora1Radius = canvasWidth * 0.95f * breathScale

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE5D5C0).copy(alpha = 0.17f), // Warm Sand Beige
                            Color(0xFFE5D5C0).copy(alpha = 0.08f),
                            Color(0xFFE5D5C0).copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        center = Offset(aurora1X, aurora1Y),
                        radius = aurora1Radius
                    ),
                    center = Offset(aurora1X, aurora1Y),
                    radius = aurora1Radius
                )

                // Secondary Soft Champagne Aurora Nebula (Lower-Right Drifting)
                val aurora2X = canvasWidth * 0.76f + cos(secondaryDrift * 0.7f) * (canvasWidth * 0.18f)
                val aurora2Y = canvasHeight * 0.74f + sin(secondaryDrift * 0.9f) * (canvasHeight * 0.15f)
                val aurora2Radius = canvasWidth * 1.05f * (2f - breathScale)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFF2E8DC).copy(alpha = 0.14f), // Soft Champagne
                            Color(0xFFF2E8DC).copy(alpha = 0.06f),
                            Color(0xFFF2E8DC).copy(alpha = 0.015f),
                            Color.Transparent
                        ),
                        center = Offset(aurora2X, aurora2Y),
                        radius = aurora2Radius
                    ),
                    center = Offset(aurora2X, aurora2Y),
                    radius = aurora2Radius
                )

                // Central Luminous Breathing Core (Mid-Screen Warm Light)
                val coreAlpha = 0.08f + sin(breathPhase) * 0.035f // 0.045f to 0.115f
                val aurora3X = canvasWidth * 0.52f + sin(secondaryDrift * 0.6f) * (canvasWidth * 0.14f)
                val aurora3Y = canvasHeight * 0.50f + cos(auroraDrift * 0.7f) * (canvasHeight * 0.10f)
                val aurora3Radius = canvasWidth * 0.75f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = coreAlpha),
                            Color(0xFFE5D5C0).copy(alpha = coreAlpha * 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(aurora3X, aurora3Y),
                        radius = aurora3Radius
                    ),
                    center = Offset(aurora3X, aurora3Y),
                    radius = aurora3Radius
                )
            } else {
                // --- Light Mode: Crisp White with Warm Taupe & Cream Silk Nebulas ---
                val aurora1X = canvasWidth * 0.30f + sin(auroraDrift) * (canvasWidth * 0.18f)
                val aurora1Y = canvasHeight * 0.22f + cos(auroraDrift * 0.8f) * (canvasHeight * 0.12f)
                val aurora1Radius = canvasWidth * 0.90f * breathScale

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC8B094).copy(alpha = 0.13f), // Warm Taupe
                            Color(0xFFC8B094).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(aurora1X, aurora1Y),
                        radius = aurora1Radius
                    ),
                    center = Offset(aurora1X, aurora1Y),
                    radius = aurora1Radius
                )

                val aurora2X = canvasWidth * 0.78f + cos(secondaryDrift * 0.7f) * (canvasWidth * 0.16f)
                val aurora2Y = canvasHeight * 0.76f + sin(secondaryDrift * 0.9f) * (canvasHeight * 0.14f)
                val aurora2Radius = canvasWidth * 1.0f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEDE2D2).copy(alpha = 0.15f), // Soft Cream
                            Color(0xFFEDE2D2).copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(aurora2X, aurora2Y),
                        radius = aurora2Radius
                    ),
                    center = Offset(aurora2X, aurora2Y),
                    radius = aurora2Radius
                )
            }

            // ========================================================
            // 2. Living Constellation Network & Stardust Nodes
            // ========================================================
            val nodeCount = nodes.size
            val posX = FloatArray(nodeCount)
            val posY = FloatArray(nodeCount)
            val nodeFades = FloatArray(nodeCount)

            // Step A: Calculate current positions and edge fades
            nodes.forEachIndexed { i, node ->
                val currentYProg = (node.yOffset - (constellationProgress * node.speedMultiplier)) % 1f
                val normY = if (currentYProg < 0f) currentYProg + 1f else currentYProg
                val py = normY * canvasHeight

                val wobble = sin(auroraDrift * node.wobbleSpeed + node.seed) * (node.wobbleAmplitude * density)
                val px = (node.xRatio * canvasWidth + wobble).coerceIn(0f, canvasWidth)

                posX[i] = px
                posY[i] = py

                // Smooth edge fade at screen borders
                val edgeFade = when {
                    normY < 0.10f -> normY / 0.10f
                    normY > 0.90f -> (1f - normY) / 0.10f
                    else -> 1f
                }
                nodeFades[i] = edgeFade
            }

            // Step B: Draw hairline connection threads between nearby nodes
            val maxConnectionDist = 95f * density
            val maxConnectionDistSq = maxConnectionDist * maxConnectionDist
            val connectionBaseColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)
            val maxLineAlpha = if (darkTheme) 0.20f else 0.13f

            for (i in 0 until nodeCount) {
                for (j in i + 1 until nodeCount) {
                    val dx = posX[i] - posX[j]
                    val dy = posY[i] - posY[j]
                    val distSq = dx * dx + dy * dy

                    if (distSq < maxConnectionDistSq) {
                        val dist = sqrt(distSq)
                        val proximity = (1f - dist / maxConnectionDist) * nodeFades[i] * nodeFades[j]
                        val lineAlpha = proximity * maxLineAlpha

                        drawLine(
                            color = connectionBaseColor.copy(alpha = lineAlpha),
                            start = Offset(posX[i], posY[i]),
                            end = Offset(posX[j], posY[j]),
                            strokeWidth = 1f * density,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // Step C: Draw nodes (twinkling stars and stardust motes)
            val starBaseColor = if (darkTheme) Color(0xFFFAF7F2) else Color(0xFF5C4F42)
            val warmAccentColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)

            nodes.forEachIndexed { i, node ->
                val px = posX[i]
                val py = posY[i]
                val fade = nodeFades[i]

                val twinkle = (sin(breathPhase * node.twinkleSpeed + node.seed) + 1f) / 2f
                val baseColor = if (node.isStar) starBaseColor else warmAccentColor

                if (darkTheme) {
                    val particleAlpha = (0.28f + twinkle * 0.36f) * fade // 0.28 to 0.64
                    val radiusPx = node.radiusDp * density

                    // Soft glowing aura for prominent stars
                    if (node.isStar) {
                        drawCircle(
                            color = baseColor.copy(alpha = particleAlpha * 0.25f),
                            radius = radiusPx * 2.8f,
                            center = Offset(px, py)
                        )
                    }

                    // Bright stellar core
                    drawCircle(
                        color = baseColor.copy(alpha = particleAlpha),
                        radius = radiusPx,
                        center = Offset(px, py)
                    )
                } else {
                    val particleAlpha = (0.16f + twinkle * 0.20f) * fade
                    val radiusPx = node.radiusDp * density

                    drawCircle(
                        color = baseColor.copy(alpha = particleAlpha),
                        radius = radiusPx,
                        center = Offset(px, py)
                    )
                }
            }

            // ========================================================
            // 3. Ethereal Shooting Light Streaks (Periodic Cosmic Glides)
            // ========================================================
            // Shooting Streak 1 (Glides from upper-left to mid-right during 0.18f .. 0.32f window)
            val streak1Window = (shootingStarProgress - 0.18f) / 0.14f
            if (streak1Window in 0f..1f) {
                val startX = canvasWidth * 0.08f + streak1Window * (canvasWidth * 0.85f)
                val startY = canvasHeight * 0.08f + streak1Window * (canvasHeight * 0.22f)
                val tailX = (startX - canvasWidth * 0.18f).coerceAtLeast(0f)
                val tailY = (startY - canvasHeight * 0.05f).coerceAtLeast(0f)

                val streakFade = sin(streak1Window * Math.PI.toFloat())
                val streakAlpha = (if (darkTheme) 0.40f else 0.22f) * streakFade

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            warmAccentColor.copy(alpha = streakAlpha * 0.5f),
                            starBaseColor.copy(alpha = streakAlpha)
                        ),
                        start = Offset(tailX, tailY),
                        end = Offset(startX, startY)
                    ),
                    start = Offset(tailX, tailY),
                    end = Offset(startX, startY),
                    strokeWidth = 1.6f * density,
                    cap = StrokeCap.Round
                )
            }

            // Shooting Streak 2 (Glides from mid-right to lower-left during 0.68f .. 0.82f window)
            val streak2Window = (shootingStarProgress - 0.68f) / 0.14f
            if (streak2Window in 0f..1f) {
                val startX = canvasWidth * 0.92f - streak2Window * (canvasWidth * 0.78f)
                val startY = canvasHeight * 0.38f + streak2Window * (canvasHeight * 0.18f)
                val tailX = (startX + canvasWidth * 0.16f).coerceAtMost(canvasWidth)
                val tailY = (startY - canvasHeight * 0.04f).coerceAtLeast(0f)

                val streakFade = sin(streak2Window * Math.PI.toFloat())
                val streakAlpha = (if (darkTheme) 0.35f else 0.18f) * streakFade

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            warmAccentColor.copy(alpha = streakAlpha * 0.5f),
                            starBaseColor.copy(alpha = streakAlpha)
                        ),
                        start = Offset(tailX, tailY),
                        end = Offset(startX, startY)
                    ),
                    start = Offset(tailX, tailY),
                    end = Offset(startX, startY),
                    strokeWidth = 1.4f * density,
                    cap = StrokeCap.Round
                )
            }
        }

        // Foreground application content
        content()
    }
}

private data class ConstellationNodeSeed(
    val xRatio: Float,
    val yOffset: Float,
    val speedMultiplier: Float,
    val wobbleSpeed: Float,
    val wobbleAmplitude: Float,
    val radiusDp: Float,
    val twinkleSpeed: Float,
    val isStar: Boolean,
    val seed: Float
)
