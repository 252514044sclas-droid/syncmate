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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Ultra-aesthetic, hardware-accelerated ambient live animated background themed around
 * Rental Housing & Roommate Peer Connections.
 *
 * Theme Features:
 * 1. Pitch Black Backdrop (#000000) in Dark Mode / Crisp Pure White (#FFFFFF) in Light Mode.
 * 2. Subtle Blueprint / CAD Housing Grid: Faint architectural coordinate grid with crosshairs (+).
 * 3. Rental & Roommate Constellation Nodes:
 *    - Housing Nodes: Minimalist geometric house silhouettes with glowing roofs and foundation pins.
 *    - Roommate Nodes: Dual-orbit student synergy hubs representing peer connections.
 *    - Campus Zone Pins: Geolocation beacons anchoring rental neighborhoods.
 * 4. Dynamic Connection Mesh: Proximity filaments that gracefully illuminate when roommate and rental
 *    nodes drift close to each other, illustrating real-time housing matching.
 * 5. Traveling Signal Pulses: Luminous energy packets gliding along the connection lines,
 *    symbolizing roommate connection requests, instant chats, and lease agreements.
 * 6. Harmonic Rental Handshake Ripple: Soft resonant rings rippling from active housing hubs.
 * 7. Ambient City Sparks: Subtle floating motes of warm light drifting organically in the background.
 */
@Composable
fun AmbientLiveBackground(
    darkTheme: Boolean = isSystemInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RentalConnectionTransition")

    // Slow organic drift for constellation nodes (12 seconds continuous cycle)
    val driftPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DriftPhase"
    )

    // Signal packet flow along connection paths (3.2 seconds cycle)
    val packetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PacketProgress"
    )

    // Rental agreement handshake beacon ripple (4.5 seconds cycle)
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleProgress"
    )

    // Base background colors
    val backdropColor = if (darkTheme) Color(0xFF000000) else Color(0xFFFFFFFF)

    // Reusable Path objects to prevent allocations during Canvas draw pass
    val housePath = remember { Path() }
    val linePath = remember { Path() }

    // Seeded constellation nodes representing rental houses, roommate pairs, and campus pins
    val nodes = remember {
        listOf(
            RentalNode(baseX = 0.14f, baseY = 0.16f, type = RentalNodeType.HOUSE, speedX = 0.8f, speedY = 0.6f, ampX = 14f, ampY = 10f, phase = 0.2f, sizeDp = 18f),
            RentalNode(baseX = 0.36f, baseY = 0.11f, type = RentalNodeType.ROOMMATE, speedX = 0.7f, speedY = 0.9f, ampX = 12f, ampY = 15f, phase = 1.5f, sizeDp = 16f),
            RentalNode(baseX = 0.68f, baseY = 0.14f, type = RentalNodeType.HOUSE, speedX = 0.9f, speedY = 0.7f, ampX = 16f, ampY = 11f, phase = 2.8f, sizeDp = 19f),
            RentalNode(baseX = 0.88f, baseY = 0.24f, type = RentalNodeType.PIN, speedX = 0.6f, speedY = 0.8f, ampX = 10f, ampY = 13f, phase = 3.9f, sizeDp = 15f),
            RentalNode(baseX = 0.24f, baseY = 0.38f, type = RentalNodeType.ROOMMATE, speedX = 1.0f, speedY = 0.6f, ampX = 15f, ampY = 12f, phase = 0.9f, sizeDp = 17f),
            RentalNode(baseX = 0.50f, baseY = 0.33f, type = RentalNodeType.HOUSE, speedX = 0.7f, speedY = 0.8f, ampX = 13f, ampY = 14f, phase = 4.2f, sizeDp = 22f), // Central focal rental hub
            RentalNode(baseX = 0.78f, baseY = 0.40f, type = RentalNodeType.ROOMMATE, speedX = 0.8f, speedY = 1.0f, ampX = 14f, ampY = 16f, phase = 5.1f, sizeDp = 16f),
            RentalNode(baseX = 0.12f, baseY = 0.62f, type = RentalNodeType.HOUSE, speedX = 0.9f, speedY = 0.7f, ampX = 15f, ampY = 11f, phase = 1.8f, sizeDp = 18f),
            RentalNode(baseX = 0.38f, baseY = 0.54f, type = RentalNodeType.PIN, speedX = 0.7f, speedY = 0.8f, ampX = 11f, ampY = 13f, phase = 3.3f, sizeDp = 16f),
            RentalNode(baseX = 0.64f, baseY = 0.60f, type = RentalNodeType.ROOMMATE, speedX = 0.8f, speedY = 0.6f, ampX = 14f, ampY = 12f, phase = 2.1f, sizeDp = 17f),
            RentalNode(baseX = 0.88f, baseY = 0.68f, type = RentalNodeType.HOUSE, speedX = 0.6f, speedY = 0.9f, ampX = 12f, ampY = 15f, phase = 4.7f, sizeDp = 19f),
            RentalNode(baseX = 0.22f, baseY = 0.82f, type = RentalNodeType.ROOMMATE, speedX = 0.9f, speedY = 0.7f, ampX = 15f, ampY = 11f, phase = 0.6f, sizeDp = 16f),
            RentalNode(baseX = 0.52f, baseY = 0.78f, type = RentalNodeType.HOUSE, speedX = 0.8f, speedY = 0.8f, ampX = 13f, ampY = 14f, phase = 3.6f, sizeDp = 20f),
            RentalNode(baseX = 0.76f, baseY = 0.86f, type = RentalNodeType.PIN, speedX = 0.7f, speedY = 1.0f, ampX = 12f, ampY = 16f, phase = 5.4f, sizeDp = 15f)
        )
    }

    // Active connection topological links between roommates, rentals, and campus locations
    val connections = remember {
        listOf(
            Pair(0, 1), Pair(1, 4), Pair(1, 5),
            Pair(2, 3), Pair(2, 5), Pair(3, 6),
            Pair(4, 5), Pair(4, 7), Pair(4, 8),
            Pair(5, 6), Pair(5, 8), Pair(5, 9),
            Pair(6, 9), Pair(6, 10),
            Pair(7, 8), Pair(7, 11),
            Pair(8, 9), Pair(8, 11), Pair(8, 12),
            Pair(9, 10), Pair(9, 12), Pair(9, 13),
            Pair(10, 13),
            Pair(11, 12), Pair(12, 13)
        )
    }

    // Ambient floating city micro-sparks
    val sparks = remember {
        List(18) { index ->
            SparkSeed(
                xRatio = ((index * 47 + 19) % 100) / 100f,
                yRatio = ((index * 39 + 23) % 100) / 100f,
                radiusDp = 1.2f + (index % 3) * 0.5f,
                speedMultiplier = 0.5f + (index % 4) * 0.25f,
                seed = index * 2.17f
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

            val primaryColor = if (darkTheme) Color(0xFFE5D5C0) else Color(0xFFA68A68) // Warm champagne ivory
            val accentColor = if (darkTheme) Color(0xFF818CF8) else Color(0xFF6366F1)  // Soft indigo connection
            val matchColor = if (darkTheme) Color(0xFF34D399) else Color(0xFF059669)   // Emerald synergy match

            // ========================================================
            // 1. Architectural Blueprint Grid with Crosshair Ticks (+)
            // ========================================================
            val gridSpacing = 54f * density
            val gridColor = if (darkTheme) {
                Color(0xFFE5D5C0).copy(alpha = 0.022f)
            } else {
                Color(0xFFA68A68).copy(alpha = 0.032f)
            }

            var gx = gridSpacing
            while (gx < canvasWidth) {
                drawLine(
                    color = gridColor,
                    start = Offset(gx, 0f),
                    end = Offset(gx, canvasHeight),
                    strokeWidth = 0.7f * density
                )
                gx += gridSpacing
            }

            var gy = gridSpacing
            while (gy < canvasHeight) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, gy),
                    end = Offset(canvasWidth, gy),
                    strokeWidth = 0.7f * density
                )
                gy += gridSpacing
            }

            // Blueprint Coordinate Crosshairs (+) at key structural intersections
            val crosshairColor = if (darkTheme) primaryColor.copy(alpha = 0.045f) else primaryColor.copy(alpha = 0.06f)
            val crossSize = 3.5f * density
            var cx = gridSpacing * 2
            while (cx < canvasWidth) {
                var cy = gridSpacing * 2
                while (cy < canvasHeight) {
                    drawLine(
                        color = crosshairColor,
                        start = Offset(cx - crossSize, cy),
                        end = Offset(cx + crossSize, cy),
                        strokeWidth = 0.9f * density
                    )
                    drawLine(
                        color = crosshairColor,
                        start = Offset(cx, cy - crossSize),
                        end = Offset(cx, cy + crossSize),
                        strokeWidth = 0.9f * density
                    )
                    cy += gridSpacing * 3
                }
                cx += gridSpacing * 3
            }

            // ========================================================
            // 2. Compute Current Positions of Constellation Nodes
            // ========================================================
            val currentPositions = nodes.map { node ->
                val offsetX = sin(driftPhase * node.speedX + node.phase) * (node.ampX * density)
                val offsetY = cos(driftPhase * node.speedY + node.phase * 1.3f) * (node.ampY * density)
                Offset(
                    x = (node.baseX * canvasWidth + offsetX).coerceIn(24f * density, canvasWidth - 24f * density),
                    y = (node.baseY * canvasHeight + offsetY).coerceIn(24f * density, canvasHeight - 24f * density)
                )
            }

            // ========================================================
            // 3. Rental Agreement Handshake Ripple (Central Hub #5)
            // ========================================================
            val centralHubPos = currentPositions[5]
            val maxRippleRadius = 160f * density
            val currentRippleRadius = rippleProgress * maxRippleRadius
            val rippleAlpha = (1f - rippleProgress) * (if (darkTheme) 0.18f else 0.12f)

            // Outer expanding rental handshake wave
            drawCircle(
                color = matchColor.copy(alpha = rippleAlpha),
                radius = currentRippleRadius,
                center = centralHubPos,
                style = Stroke(width = 1.2f * density)
            )

            // Secondary concentric synergy wave
            if (currentRippleRadius > 40f * density) {
                drawCircle(
                    color = primaryColor.copy(alpha = rippleAlpha * 0.6f),
                    radius = currentRippleRadius * 0.65f,
                    center = centralHubPos,
                    style = Stroke(width = 0.9f * density)
                )
            }

            // ========================================================
            // 4. Connection Mesh Filaments & Active Chat Pulses
            // ========================================================
            val maxConnectionDist = 280f * density

            connections.forEachIndexed { connIdx, (fromIdx, toIdx) ->
                if (fromIdx in currentPositions.indices && toIdx in currentPositions.indices) {
                    val p1 = currentPositions[fromIdx]
                    val p2 = currentPositions[toIdx]

                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val dist = sqrt(dx * dx + dy * dy)

                    if (dist < maxConnectionDist) {
                        val proximityFactor = (1f - (dist / maxConnectionDist)).coerceIn(0f, 1f)
                        val lineAlpha = proximityFactor * (if (darkTheme) 0.14f else 0.12f)

                        // Subtle glowing connection line
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = lineAlpha * 0.7f),
                                    accentColor.copy(alpha = lineAlpha),
                                    primaryColor.copy(alpha = lineAlpha * 0.7f)
                                ),
                                start = p1,
                                end = p2
                            ),
                            start = p1,
                            end = p2,
                            strokeWidth = 1.1f * density,
                            cap = StrokeCap.Round
                        )

                        // Traveling Signal Packet (Roommate Chat / Rental Inquiry Pulse)
                        // Each connection has a phased packet traveling between the nodes
                        val packetT = (packetProgress * 1.5f + (connIdx * 0.13f)) % 1f
                        val packetX = p1.x + dx * packetT
                        val packetY = p1.y + dy * packetT

                        val packetAlpha = proximityFactor * (if (darkTheme) 0.75f else 0.60f) * sin(packetT * Math.PI.toFloat())

                        if (packetAlpha > 0.05f) {
                            // Soft ambient packet halo
                            drawCircle(
                                color = accentColor.copy(alpha = packetAlpha * 0.35f),
                                radius = 5.5f * density,
                                center = Offset(packetX, packetY)
                            )
                            // Bright signal core
                            drawCircle(
                                color = if (darkTheme) Color.White else primaryColor,
                                radius = 2.0f * density,
                                center = Offset(packetX, packetY)
                            )
                        }
                    }
                }
            }

            // ========================================================
            // 5. Draw Constellation Nodes (Houses, Roommates, Pins)
            // ========================================================
            nodes.forEachIndexed { idx, node ->
                val pos = currentPositions[idx]
                val baseAlpha = if (darkTheme) 0.65f else 0.55f

                when (node.type) {
                    RentalNodeType.HOUSE -> {
                        drawHouseNode(
                            scope = this,
                            path = housePath,
                            x = pos.x,
                            y = pos.y,
                            size = node.sizeDp * density,
                            color = primaryColor,
                            alpha = baseAlpha,
                            density = density,
                            darkTheme = darkTheme
                        )
                    }
                    RentalNodeType.ROOMMATE -> {
                        drawRoommateNode(
                            scope = this,
                            x = pos.x,
                            y = pos.y,
                            size = node.sizeDp * density,
                            color = accentColor,
                            alpha = baseAlpha,
                            density = density,
                            darkTheme = darkTheme
                        )
                    }
                    RentalNodeType.PIN -> {
                        drawPinNode(
                            scope = this,
                            x = pos.x,
                            y = pos.y,
                            size = node.sizeDp * density,
                            color = matchColor,
                            alpha = baseAlpha,
                            density = density,
                            darkTheme = darkTheme
                        )
                    }
                }
            }

            // ========================================================
            // 6. Ambient Floating City Micro-Sparks
            // ========================================================
            sparks.forEach { spark ->
                val currentX = ((spark.xRatio * canvasWidth) + (driftPhase * 15f * spark.speedMultiplier * density)) % canvasWidth
                val currentY = ((spark.yRatio * canvasHeight) + sin(driftPhase * 1.8f + spark.seed) * (10f * density)) % canvasHeight

                val sparkAlpha = (if (darkTheme) 0.16f else 0.12f) + sin(driftPhase * 2f + spark.seed) * 0.08f
                drawCircle(
                    color = primaryColor.copy(alpha = sparkAlpha.coerceIn(0.04f, 0.35f)),
                    radius = spark.radiusDp * density,
                    center = Offset(currentX, currentY)
                )
            }
        }

        // Foreground application content
        content()
    }
}

/**
 * Draws a minimalist architectural Rental House glyph.
 */
private fun drawHouseNode(
    scope: DrawScope,
    path: Path,
    x: Float,
    y: Float,
    size: Float,
    color: Color,
    alpha: Float,
    density: Float,
    darkTheme: Boolean
) {
    val w = size
    val h = size * 1.05f

    // Ambient house aura
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.18f),
        radius = size * 1.1f,
        center = Offset(x, y)
    )

    path.rewind()
    // Gable Roof Peak
    path.moveTo(x, y - h * 0.52f)
    // Right roof slope & eave
    path.lineTo(x + w * 0.50f, y - h * 0.08f)
    // Right wall
    path.lineTo(x + w * 0.40f, y - h * 0.08f)
    path.lineTo(x + w * 0.40f, y + h * 0.45f)
    // Floor foundation
    path.lineTo(x - w * 0.40f, y + h * 0.45f)
    // Left wall
    path.lineTo(x - w * 0.40f, y - h * 0.08f)
    // Left roof slope
    path.lineTo(x - w * 0.50f, y - h * 0.08f)
    path.close()

    // Structural outline of the house
    scope.drawPath(
        path = path,
        color = color.copy(alpha = alpha * 0.85f),
        style = Stroke(width = 1.3f * density, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Rental entryway / doorway
    val doorW = w * 0.24f
    val doorH = h * 0.30f
    scope.drawRect(
        color = color.copy(alpha = alpha * 0.70f),
        topLeft = Offset(x - doorW * 0.5f, y + h * 0.45f - doorH),
        size = Size(doorW, doorH),
        style = Stroke(width = 1.0f * density)
    )

    // Glowing interior window dot (signifying occupied / warm home)
    scope.drawCircle(
        color = if (darkTheme) Color.White.copy(alpha = alpha * 0.9f) else color.copy(alpha = alpha * 0.9f),
        radius = 1.5f * density,
        center = Offset(x, y - h * 0.15f)
    )
}

/**
 * Draws a Roommate Synergy Hub glyph (dual concentric peer nodes).
 */
private fun drawRoommateNode(
    scope: DrawScope,
    x: Float,
    y: Float,
    size: Float,
    color: Color,
    alpha: Float,
    density: Float,
    darkTheme: Boolean
) {
    val r = size * 0.32f

    // Soft orbital synergy ring
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.22f),
        radius = size * 0.95f,
        center = Offset(x, y),
        style = Stroke(width = 1.0f * density)
    )

    // Peer Student A node
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.90f),
        radius = r * 0.75f,
        center = Offset(x - r * 0.75f, y)
    )

    // Peer Student B node
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.90f),
        radius = r * 0.75f,
        center = Offset(x + r * 0.75f, y)
    )

    // Central connection spark between the two peers
    scope.drawCircle(
        color = if (darkTheme) Color.White else color,
        radius = 1.5f * density,
        center = Offset(x, y)
    )
}

/**
 * Draws a Campus / Neighborhood Geolocation Pin beacon.
 */
private fun drawPinNode(
    scope: DrawScope,
    x: Float,
    y: Float,
    size: Float,
    color: Color,
    alpha: Float,
    density: Float,
    darkTheme: Boolean
) {
    val r = size * 0.30f
    val pinHeadY = y - size * 0.25f

    // Concentric radar beacon around location
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.18f),
        radius = size * 0.85f,
        center = Offset(x, pinHeadY),
        style = Stroke(width = 1.0f * density)
    )

    // Pin head circle
    scope.drawCircle(
        color = color.copy(alpha = alpha * 0.85f),
        radius = r,
        center = Offset(x, pinHeadY),
        style = Stroke(width = 1.2f * density)
    )

    // Pin center dot
    scope.drawCircle(
        color = if (darkTheme) Color.White else color,
        radius = 1.6f * density,
        center = Offset(x, pinHeadY)
    )

    // Pin ground needle pointing down to map location
    scope.drawLine(
        color = color.copy(alpha = alpha * 0.75f),
        start = Offset(x, pinHeadY + r),
        end = Offset(x, y + size * 0.40f),
        strokeWidth = 1.2f * density,
        cap = StrokeCap.Round
    )
}

private enum class RentalNodeType {
    HOUSE, ROOMMATE, PIN
}

private data class RentalNode(
    val baseX: Float,
    val baseY: Float,
    val type: RentalNodeType,
    val speedX: Float,
    val speedY: Float,
    val ampX: Float,
    val ampY: Float,
    val phase: Float,
    val sizeDp: Float
)

private data class SparkSeed(
    val xRatio: Float,
    val yRatio: Float,
    val radiusDp: Float,
    val speedMultiplier: Float,
    val seed: Float
)
