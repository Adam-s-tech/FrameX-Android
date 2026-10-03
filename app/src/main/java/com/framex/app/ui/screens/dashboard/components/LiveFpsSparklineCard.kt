package com.framex.app.ui.screens.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.R
import com.framex.app.ui.screens.dashboard.FpsStatsSummary
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXMotion
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

/**
 * Live Metrics section showing strictly the real-time FPS ECG/oscilloscope curve
 * and live FPS readout. CPU, GPU, RAM, and Frame Time cards are excluded per design specification.
 * Preserves the deep black background styling and zero-allocation Canvas drawing.
 */
@Composable
fun LiveFpsSparklineCard(
    fpsHistory: List<Int>,
    stats: FpsStatsSummary = FpsStatsSummary(),
    currentFps: Int = stats.currentFps,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = Color(0xFF1E2028)
    val liveRed = Color(0xFFE6193C)

    val infiniteTransition = rememberInfiniteTransition(label = "sparklineDotPulse")
    val dotGlowRadius by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FrameXMotion.StandardEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotGlowRadius"
    )

    val sparklineDescription = stringResource(
        R.string.cd_sparkline_chart
    ) + ": $currentFps " + stringResource(R.string.dashboard_fps_unit)

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header: White ECG pulse icon, title, and real-time live indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EcgPulseIcon(
                    tint = Color.White,
                    modifier = Modifier.size(width = 20.dp, height = 16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.dashboard_live_metrics),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.dashboard_realtime),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(liveRed)
                )
            }
        }

        // FPS Card with Black Background, ECG curve, and 120/60/0 oscilloscope scale
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0C0C0D), FrameXShapes.Card)
                .border(FrameXBorders.ActiveBorderWidth, FrameXBorders.CardStroke, FrameXShapes.Card)
                .padding(FrameXSpacing.Standard)
                .semantics(mergeDescendants = true) {
                    contentDescription = sparklineDescription
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Centered FPS label & Large Live FPS readout
                Column(
                    modifier = Modifier.padding(end = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_fps_unit),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9E9E9E),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$currentFps",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = lineColor
                    )
                }

                // Right Row: Oscilloscope Curve with 120 / 60 / 0 vertical axis on the right
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .drawWithCache {
                                val w = size.width
                                val h = size.height
                                val strokePx = 2.dp.toPx()
                                val gridStrokePx = 1.dp.toPx()
                                val dotRadiusPx = 3.5.dp.toPx()

                                val scaleMax = maxOf(120f, (fpsHistory.maxOrNull() ?: 60).toFloat())
                                val yForFps: (Int) -> Float = { fps ->
                                    h * (1f - (fps.toFloat() / scaleMax).coerceIn(0f, 1f))
                                }

                                val path = Path()
                                val fillPath = Path()
                                var lastX = w
                                var lastY = yForFps(if (fpsHistory.isNotEmpty()) fpsHistory.last() else currentFps)

                                if (fpsHistory.size >= 2) {
                                    fpsHistory.forEachIndexed { i, fps ->
                                        val x = w * i / (fpsHistory.size - 1).toFloat()
                                        val y = yForFps(fps)
                                        if (i == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }
                                        if (i == fpsHistory.size - 1) {
                                            lastX = x
                                            lastY = y
                                        }
                                    }
                                    fillPath.addPath(path)
                                    fillPath.lineTo(w, h)
                                    fillPath.lineTo(0f, h)
                                    fillPath.close()
                                }

                                val gradientBrush = Brush.verticalGradient(
                                    colors = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0f))
                                )

                                onDrawBehind {
                                    // Background grid lines: 120 (top), 60 (center), 0 (bottom)
                                    drawLine(
                                        color = gridColor,
                                        start = Offset(0f, 1.dp.toPx()),
                                        end = Offset(w, 1.dp.toPx()),
                                        strokeWidth = gridStrokePx
                                    )
                                    drawLine(
                                        color = gridColor,
                                        start = Offset(0f, h * 0.5f),
                                        end = Offset(w, h * 0.5f),
                                        strokeWidth = gridStrokePx
                                    )
                                    drawLine(
                                        color = gridColor,
                                        start = Offset(0f, h - 1.dp.toPx()),
                                        end = Offset(w, h - 1.dp.toPx()),
                                        strokeWidth = gridStrokePx
                                    )

                                    if (fpsHistory.size >= 2) {
                                        drawPath(fillPath, brush = gradientBrush)
                                        drawPath(
                                            path = path,
                                            color = lineColor,
                                            style = Stroke(
                                                width = strokePx,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                        // Animated pulsing end dot
                                        drawCircle(
                                            color = lineColor.copy(alpha = 0.35f),
                                            radius = dotGlowRadius.dp.toPx(),
                                            center = Offset(lastX, lastY)
                                        )
                                        drawCircle(
                                            color = Color(0xFF0C0C0D),
                                            radius = dotRadiusPx,
                                            center = Offset(lastX, lastY)
                                        )
                                        drawCircle(
                                            color = lineColor,
                                            radius = dotRadiusPx,
                                            center = Offset(lastX, lastY),
                                            style = Stroke(width = strokePx)
                                        )
                                    } else {
                                        // Idle baseline line (at 0 FPS, or current FPS if any)
                                        val baseY = yForFps(if (fpsHistory.isNotEmpty()) fpsHistory.last() else currentFps)
                                        drawLine(
                                            color = lineColor.copy(alpha = 0.3f),
                                            start = Offset(0f, baseY),
                                            end = Offset(w, baseY),
                                            strokeWidth = strokePx
                                        )
                                        drawCircle(
                                            color = lineColor.copy(alpha = 0.35f),
                                            radius = dotGlowRadius.dp.toPx(),
                                            center = Offset(w, baseY)
                                        )
                                        drawCircle(
                                            color = lineColor,
                                            radius = dotRadiusPx,
                                            center = Offset(w, baseY)
                                        )
                                    }
                                }
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Oscilloscope vertical axis labels
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 2.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "120",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF616161),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "60",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF616161),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF616161),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EcgPulseIcon(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height
            val strokePx = 1.8.dp.toPx()
            val path = Path().apply {
                moveTo(0f, h * 0.5f)
                lineTo(w * 0.22f, h * 0.5f)
                lineTo(w * 0.35f, h * 0.15f)
                lineTo(w * 0.48f, h * 0.85f)
                lineTo(w * 0.60f, h * 0.30f)
                lineTo(w * 0.72f, h * 0.5f)
                lineTo(w, h * 0.5f)
            }
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            onDrawBehind {
                drawPath(
                    path = path,
                    color = tint,
                    style = stroke
                )
            }
        }
    )
}
