package com.framex.app.ui.screens.splash.components

import android.graphics.PathMeasure as AndroidPathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private val FrameDrawEasing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)

/**
 * Hardware-accelerated canvas logo drawing the FrameX master vector.
 * Replicates the "signal enters the frame" animation sequence:
 * 1. Clockwise outer frame drawing from left stub with a glowing leading tip.
 * 2. Inner X building and contour stroke.
 * 3. Text reveal for FrameX and PERFORMANCE SUITE.
 * 4. Subtle neon bloom pulse and scale settle.
 */
@Composable
fun SplashAnimatedLogo(
    onLogoComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val frameProgress = remember { Animatable(0f) }
    val xProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(10f) }
    val pulseAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(1f) }

    val framePath = remember {
        Path().apply {
            moveTo(308f, 512f)
            lineTo(128f, 512f)
            lineTo(128f, 128f)
            lineTo(896f, 128f)
            lineTo(896f, 896f)
            lineTo(128f, 896f)
            lineTo(128f, 512f)
        }
    }

    val xPath = remember {
        Path().apply {
            moveTo(270.0f, 304.3f)
            lineTo(270.0f, 337.6f)
            lineTo(445.5f, 512.0f)
            lineTo(270.0f, 686.4f)
            lineTo(270.0f, 753.0f)
            lineTo(354.0f, 753.0f)
            lineTo(512.0f, 581.0f)
            lineTo(670.0f, 753.0f)
            lineTo(753.0f, 753.0f)
            lineTo(753.0f, 686.2f)
            lineTo(583.1f, 512.0f)
            lineTo(753.0f, 337.8f)
            lineTo(753.0f, 271.0f)
            lineTo(669.8f, 271.0f)
            lineTo(512.0f, 439.6f)
            lineTo(354.2f, 271.0f)
            lineTo(270.0f, 271.0f)
            close()
        }
    }

    val frameAndroidPath = remember(framePath) { framePath.asAndroidPath() }
    val xAndroidPath = remember(xPath) { xPath.asAndroidPath() }

    val androidFrameMeasure = remember(frameAndroidPath) {
        AndroidPathMeasure(frameAndroidPath, false)
    }
    val androidXMeasure = remember(xAndroidPath) {
        AndroidPathMeasure(xAndroidPath, false)
    }

    val frameLength = remember(androidFrameMeasure) { androidFrameMeasure.length }
    val xLength = remember(androidXMeasure) { androidXMeasure.length }

    val partialFramePath = remember { Path() }
    val partialXPath = remember { Path() }
    val posArray = remember { FloatArray(2) }
    val tanArray = remember { FloatArray(2) }

    LaunchedEffect(Unit) {
        coroutineScope {
            // 1. Draw outer frame
            launch {
                frameProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 650, delayMillis = 100, easing = FrameDrawEasing)
                )
            }

            // 2. Draw inner X right after frame begins
            launch {
                xProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600, delayMillis = 520, easing = FrameDrawEasing)
                )
            }

            // 3. Reveal typography
            launch {
                textAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400, delayMillis = 850, easing = FastOutSlowInEasing)
                )
            }
            launch {
                textOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400, delayMillis = 850, easing = FastOutSlowInEasing)
                )
            }

            // 4. Subtle pulse and settle scale finish
            launch {
                pulseAlpha.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(durationMillis = 280, delayMillis = 1050)
                )
                pulseAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 420)
                )
            }
            launch {
                logoScale.animateTo(
                    targetValue = 1.025f,
                    animationSpec = tween(durationMillis = 250, delayMillis = 1050)
                )
                logoScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 250)
                )
                onLogoComplete()
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                val scale = size.minDimension / 1024f

                withTransform({
                    scale(scale, scale, pivot = Offset.Zero)
                }) {
                    // Soft Neon Pulse Bloom
                    if (pulseAlpha.value > 0f) {
                        drawPath(
                            path = framePath,
                            color = Color.White.copy(alpha = pulseAlpha.value * 0.45f),
                            style = Stroke(width = 80f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                        )
                        drawPath(
                            path = xPath,
                            color = Color.White.copy(alpha = pulseAlpha.value * 0.45f),
                            style = Stroke(width = 80f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                        )
                    }

                    // Outer Frame
                    if (frameProgress.value > 0f) {
                        partialFramePath.reset()
                        if (frameProgress.value >= 1f) {
                            drawPath(
                                path = framePath,
                                color = Color.White,
                                style = Stroke(width = 50f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
                            )
                        } else {
                            val stopDist = frameLength * frameProgress.value
                            val androidDst = partialFramePath.asAndroidPath()
                            androidFrameMeasure.getSegment(0f, stopDist, androidDst, true)

                            // Frame glow halo
                            drawPath(
                                path = partialFramePath,
                                color = Color.White.copy(alpha = 0.35f),
                                style = Stroke(width = 72f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                            )
                            // Frame main stroke
                            drawPath(
                                path = partialFramePath,
                                color = Color.White,
                                style = Stroke(width = 50f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                            )

                            // Glowing Leading Edge Tracer Tip
                            if (androidFrameMeasure.getPosTan(stopDist, posArray, tanArray)) {
                                val tipCenter = Offset(posArray[0], posArray[1])
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.45f),
                                    radius = 48f,
                                    center = tipCenter
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 26f,
                                    center = tipCenter
                                )
                            }
                        }
                    }

                    // Inner X
                    if (xProgress.value > 0f) {
                        partialXPath.reset()
                        if (xProgress.value >= 1f) {
                            drawPath(
                                path = xPath,
                                color = Color.White,
                                style = Stroke(width = 50f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
                            )
                        } else {
                            val stopDist = xLength * xProgress.value
                            val androidDst = partialXPath.asAndroidPath()
                            androidXMeasure.getSegment(0f, stopDist, androidDst, true)

                            // X glow halo
                            drawPath(
                                path = partialXPath,
                                color = Color.White.copy(alpha = 0.35f),
                                style = Stroke(width = 72f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                            )
                            // X main stroke
                            drawPath(
                                path = partialXPath,
                                color = Color.White,
                                style = Stroke(width = 50f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                            )

                            // Glowing Leading Edge Tracer on X
                            if (androidXMeasure.getPosTan(stopDist, posArray, tanArray)) {
                                val tipCenter = Offset(posArray[0], posArray[1])
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.45f),
                                    radius = 42f,
                                    center = tipCenter
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 22f,
                                    center = tipCenter
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Branding Typography Reveal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                alpha = textAlpha.value
                translationY = textOffsetY.value
            }
        ) {
            Text(
                text = "FrameX",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.splash_performance_suite),
                color = Color.White.copy(alpha = 0.40f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.5.sp
            )
        }
    }
}
