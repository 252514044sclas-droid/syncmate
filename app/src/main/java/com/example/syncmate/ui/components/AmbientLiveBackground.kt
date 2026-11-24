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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Ultra-aesthetic, hardware-accelerated ambient live animated background featuring
 * luminous pulse stripe lines and an ECG heartbeat waveform sweep.
 *
 * Theme Features:
 * 1. Pure Pitch Black Backdrop (#000000) in Dark Mode / Crisp Pure White (#FFFFFF) in Light Mode.
 * 2. Subtle Geometric Oscilloscope Grid: Faint, elegant metric lines that establish a high-tech instrument aesthetic.
 * 3. 5 Horizontal Harmonic Stripe Lines spanning the screen with subtle continuous wave undulation.
 * 4. Traveling ECG Heartbeat Waveform: A sharp, organic cardiac P-Q-R-S-T pulse that sweeps smoothly
 *    across the focal stripe line, accompanied by a phosphorescent trailing glow.
 * 5. Luminous Heartbeat Beacon & Radial Shockwaves: Glowing pulse core at the R-wave spike tip
 *    with soft concentric cardiac shockwaves rippling outwards across adjacent stripes.
 * 6. Ambient Floating Micro-Sparks: Tiny floating energy motes around the active pulse region.
 */
@Composable
fun AmbientLiveBackground(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatStripeTransition")

    // Continuous heartbeat pulse sweep (2.4 seconds per cardiac cycle)
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeartbeatPulseProgress"
    )

    // Gentle baseline stripe wave undulation (8 seconds cycle)
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Base background colors
    val backdropColor = if (darkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)

    // Reusable Path objects to avoid allocations during Canvas draw pass
    val mainStripePath = remember { Path() }
    val glowTrailPath = remember { Path() }
    val harmonicPath = remember { Path() }

    // Seed points for ambient micro-sparks floating near stripe lines
    val sparks = remember {
        List(16) { index ->
            SparkSeed(
                xRatio = ((index * 43 + 17) % 100) / 100f,
                yRatio = 0.25f + ((index * 37 + 13) % 55) / 100f,
                radiusDp = 1.2f + (index % 3) * 0.6f,
                speedMultiplier = 0.6f + (index % 4) * 0.2f,
                seed = index * 1.73f
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
            // 1. Subtle Oscilloscope / Metric Grid Lines
            // ========================================================
            val gridSpacing = 48f * density
            val gridColor = if (darkTheme) {
                Color(0xFFE5D5C0).copy(alpha = 0.025f)
            } else {
                Color(0xFFA68A68).copy(alpha = 0.035f)
            }

            var gx = gridSpacing
            while (gx < canvasWidth) {
                drawLine(
                    color = gridColor,
                    start = Offset(gx, 0f),
                    end = Offset(gx, canvasHeight),
                    strokeWidth = 0.8f * density
                )
                gx += gridSpacing
            }

            var gy = gridSpacing
            while (gy < canvasHeight) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, gy),
                    end = Offset(canvasWidth, gy),
                    strokeWidth = 0.8f * density
                )
                gy += gridSpacing
            }

            // ========================================================
            // 2. Cardiac Shockwave Ripple (Expanding Radial Rings)
            // ========================================================
            val heartbeatPulseX = pulseProgress * (canvasWidth + 240f * density) - 120f * density
            val focalY = canvasHeight * 0.50f

            // Shockwave when the pulse passes the center region
            val centerProximity = 1f - kotlin.math.abs(pulseProgress - 0.5f) * 2f
            if (centerProximity > 0f) {
                val ringRadius = (1f - centerProximity) * (canvasWidth * 0.45f)
                val ringAlpha = centerProximity * (if (darkTheme) 0.12f else 0.08f)
                val ringColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)

                drawCircle(
                    color = ringColor.copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = Offset(canvasWidth * 0.50f, focalY),
                    style = Stroke(width = 1.2f * density)
                )

                if (ringRadius > 30f * density) {
                    drawCircle(
                        color = ringColor.copy(alpha = ringAlpha * 0.5f),
                        radius = ringRadius * 0.65f,
                        center = Offset(canvasWidth * 0.50f, focalY),
                        style = Stroke(width = 1.0f * density)
                    )
                }
            }

            // ========================================================
            // 3. Ambient Harmonic Stripe Lines (4 Parallel Waves)
            // ========================================================
            val stripeRatios = floatArrayOf(0.18f, 0.34f, 0.66f, 0.82f)
            val baseStripeColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)

            stripeRatios.forEachIndexed { idx, ratio ->
                val baseLineY = canvasHeight * ratio
                val stripeAlpha = if (darkTheme) 0.08f + (idx % 2) * 0.03f else 0.10f + (idx % 2) * 0.04f

                harmonicPath.rewind()
                harmonicPath.moveTo(0f, baseLineY)

                val step = 8f * density
                var hx = 0f
                while (hx <= canvasWidth) {
                    val waveOffset = sin((hx / (90f * density)) + wavePhase + idx * 1.5f) * (4.5f * density)

                    // Sympathetic resonance ripple from the heartbeat on the main line
                    val distToHeartbeat = kotlin.math.abs(hx - heartbeatPulseX) / (120f * density)
                    val resonance = if (distToHeartbeat < 1.0f) {
                        val d = distToHeartbeat
                        val decay = 1f - d * d
                        sin(distToHeartbeat * 6f) * (12f * density) * decay * (if (idx in 1..2) 0.6f else 0.3f)
                    } else 0f

                    harmonicPath.lineTo(hx, baseLineY + waveOffset + resonance)
                    hx += step
                }

                drawPath(
                    path = harmonicPath,
                    color = baseStripeColor.copy(alpha = stripeAlpha),
                    style = Stroke(width = 1.2f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // ========================================================
            // 4. Primary Heartbeat Stripe Line (Focal ECG Waveform)
            // ========================================================
            val ecgScale = 90f * density // Width of the heartbeat complex
            val pulseHeadX = heartbeatPulseX

            mainStripePath.rewind()
            glowTrailPath.rewind()

            mainStripePath.moveTo(0f, focalY)

            val stepPx = 3f * density
            var px = 0f
            var trailStarted = false
            val trailLength = 200f * density
            val trailStartX = pulseHeadX - trailLength

            var rWavePeakX = pulseHeadX
            var rWavePeakY = focalY

            while (px <= canvasWidth) {
                val dx = (px - pulseHeadX) / ecgScale
                val ecgOffset = if (dx in -1.2f..1.2f) {
                    // P wave (gentle upward bump at dx = -0.42)
                    val pDiff = (dx + 0.42f) / 0.12f
                    val p = -14f * density * exp(-pDiff * pDiff)

                    // Q wave (sharp downward dip at dx = -0.14)
                    val qDiff = (dx + 0.14f) / 0.05f
                    val q = 16f * density * exp(-qDiff * qDiff)

                    // R wave (dramatic tall sharp upward spike at dx = 0.0)
                    val rDiff = dx / 0.045f
                    val r = -85f * density * exp(-rDiff * rDiff)

                    // S wave (sharp downward dip at dx = +0.14)
                    val sDiff = (dx - 0.14f) / 0.06f
                    val s = 38f * density * exp(-sDiff * sDiff)

                    // T wave (smooth upward recovery wave at dx = +0.42)
                    val tDiff = (dx - 0.42f) / 0.14f
                    val t = -22f * density * exp(-tDiff * tDiff)

                    p + q + r + s + t
                } else 0f

                // Subtle underlying sine wave
                val baselineWave = sin((px / (110f * density)) + wavePhase * 1.2f) * (3.5f * density)
                val py = focalY + ecgOffset + baselineWave

                mainStripePath.lineTo(px, py)

                // Track the exact peak of the R-wave
                if (kotlin.math.abs(px - pulseHeadX) < stepPx) {
                    rWavePeakX = px
                    rWavePeakY = py
                }

                // Collect points for the phosphorescent glow trail
                if (px in trailStartX..pulseHeadX) {
                    if (!trailStarted) {
                        glowTrailPath.moveTo(px, py)
                        trailStarted = true
                    } else {
                        glowTrailPath.lineTo(px, py)
                    }
                }

                px += stepPx
            }

            // Draw baseline of primary stripe
            val mainLineBaseAlpha = if (darkTheme) 0.15f else 0.18f
            drawPath(
                path = mainStripePath,
                color = baseStripeColor.copy(alpha = mainLineBaseAlpha),
                style = Stroke(width = 1.4f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw phosphorescent trailing glow along the pulse path
            if (trailStarted) {
                val glowColor = if (darkTheme) Color(0xFFF2E8DC) else Color(0xFFA68A68)
                val trailAlpha = if (darkTheme) 0.45f else 0.35f

                // Outer soft luminous halo
                drawPath(
                    path = glowTrailPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            glowColor.copy(alpha = trailAlpha * 0.3f),
                            glowColor.copy(alpha = trailAlpha)
                        ),
                        startX = trailStartX.coerceAtLeast(0f),
                        endX = pulseHeadX.coerceAtLeast(1f)
                    ),
                    style = Stroke(width = 5.0f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Core sharp bright heartbeat trace
                drawPath(
                    path = glowTrailPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            (if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)).copy(alpha = 0.5f),
                            (if (darkTheme) Color.White else Color(0xFF5C4F42)).copy(alpha = 0.95f)
                        ),
                        startX = trailStartX.coerceAtLeast(0f),
                        endX = pulseHeadX.coerceAtLeast(1f)
                    ),
                    style = Stroke(width = 2.2f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // ========================================================
            // 5. Heartbeat Pulse Beacon (Luminous Cardiac Core)
            // ========================================================
            if (pulseHeadX in 0f..canvasWidth) {
                val beaconRadius = 16f * density
                val beaconGlowColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68)
                val beaconCoreColor = if (darkTheme) Color.White else Color(0xFF5C4F42)

                // Ambient halo aura around the heartbeat tip
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            beaconGlowColor.copy(alpha = if (darkTheme) 0.55f else 0.40f),
                            beaconGlowColor.copy(alpha = if (darkTheme) 0.18f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(rWavePeakX, rWavePeakY),
                        radius = beaconRadius * 2.2f
                    ),
                    center = Offset(rWavePeakX, rWavePeakY),
                    radius = beaconRadius * 2.2f
                )

                // Radiant bright center point
                drawCircle(
                    color = beaconCoreColor,
                    radius = 3.5f * density,
                    center = Offset(rWavePeakX, rWavePeakY)
                )
            }

            // ========================================================
            // 6. Ambient Floating Energy Sparks
            // ========================================================
            sparks.forEach { spark ->
                val currentX = ((spark.xRatio * canvasWidth) + (wavePhase * 20f * spark.speedMultiplier)) % canvasWidth
                val distFromHeartbeat = kotlin.math.abs(currentX - pulseHeadX)
                val sparkGlow = if (distFromHeartbeat < 140f * density) {
                    1f - (distFromHeartbeat / (140f * density))
                } else 0f

                val sy = canvasHeight * spark.yRatio + sin(wavePhase * 1.5f + spark.seed) * (8f * density)
                val baseSparkColor = if (darkTheme) Color(0xFFFAF7F2) else Color(0xFFA68A68)
                val sparkAlpha = (if (darkTheme) 0.20f else 0.15f) + sparkGlow * 0.45f

                drawCircle(
                    color = baseSparkColor.copy(alpha = sparkAlpha),
                    radius = (spark.radiusDp + sparkGlow * 1.2f) * density,
                    center = Offset(currentX, sy)
                )
            }
        }

        // Foreground application content
        content()
    }
}

private data class SparkSeed(
    val xRatio: Float,
    val yRatio: Float,
    val radiusDp: Float,
    val speedMultiplier: Float,
    val seed: Float
)
