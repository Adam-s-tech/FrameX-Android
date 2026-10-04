package com.framex.app.ui.screens.performance.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.gaming.GamingModeState

/**
 * Bold, high-contrast activation charge module.
 *
 * Renders large percentage telemetry, a glowing high-tech energy charge beam,
 * and live pipeline stage descriptions during activation or restoration.
 */
@Composable
fun GamingActivationChargeModule(
    gamingState: GamingModeState,
    animatedProgress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chargeBeamPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowValue"
    )

    val percent = (animatedProgress * 100).toInt().coerceIn(0, 100)
    val statusText = when (val s = gamingState) {
        is GamingModeState.Enabling -> s.statusText
        is GamingModeState.Disabling -> "Restoring system state…"
        else -> "Calibrating…"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(vertical = 18.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large bold percentage readout.
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$percent",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-1).sp
                ),
                color = Color.White
            )
            Text(
                text = "%",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = accentColor,
                modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // High-tech cyber energy charge beam.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val progressWidth = (size.width * animatedProgress.coerceIn(0f, 1f)).coerceAtLeast(8.dp.toPx())

                // Gradient glowing charge beam.
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.4f),
                            accentColor.copy(alpha = 0.85f),
                            accentColor
                        ),
                        startX = 0f,
                        endX = progressWidth
                    ),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(progressWidth, size.height / 2f),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )

                // Leading spark node.
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(progressWidth - 4.dp.toPx(), size.height / 2f)
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.7f * pulseGlow),
                    radius = 8.dp.toPx(),
                    center = Offset(progressWidth - 4.dp.toPx(), size.height / 2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live pipeline status step.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = pulseGlow))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText.uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.8.sp,
                    fontSize = 11.sp
                ),
                color = Color.LightGray
            )
        }
    }
}
