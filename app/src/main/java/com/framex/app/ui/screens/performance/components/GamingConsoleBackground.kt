package com.framex.app.ui.screens.performance.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.framex.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val HeroBackgroundDrawables = listOf(
    R.drawable.hero_bg_1,
    R.drawable.hero_bg_2,
    R.drawable.hero_bg_3
)

/**
 * Memory cache for console hero background bitmaps.
 * Prevents main thread bitmap decoding and allocation churn during Compose composition.
 * Uses RGB_565 and downsampling to remain lightweight on <= 4GB RAM hardware.
 */
object HeroBackgroundCache {
    private val cache = LruCache<Int, ImageBitmap>(3)

    fun get(resId: Int): ImageBitmap? = cache.get(resId)

    suspend fun load(context: Context, resId: Int): ImageBitmap? {
        val cached = get(resId)
        if (cached != null) return cached

        return withContext(Dispatchers.IO) {
            runCatching {
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inSampleSize = 2
                }
                BitmapFactory.decodeResource(context.resources, resId, options)?.asImageBitmap()?.also {
                    cache.put(resId, it)
                }
            }.getOrNull()
        }
    }

    fun putForTesting(resId: Int, bitmap: ImageBitmap) {
        cache.put(resId, bitmap)
    }

    fun clear() {
        cache.evictAll()
    }
}

/**
 * Tactical console hero background.
 *
 * Randomly selects one of three character artworks upon composition,
 * overlaid with an OLED dark gradient and minimalist corner HUD brackets.
 */
@Composable
fun GamingConsoleBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFE6193C),
    isActive: Boolean = false
) {
    val context = LocalContext.current
    // Select one of the three hero artworks randomly per composition session.
    val backgroundRes = remember { HeroBackgroundDrawables.random() }
    val bitmap by produceState<ImageBitmap?>(
        initialValue = remember(backgroundRes) { HeroBackgroundCache.get(backgroundRes) },
        key1 = backgroundRes
    ) {
        if (value == null) {
            value = HeroBackgroundCache.load(context, backgroundRes)
        }
    }

    Box(modifier = modifier) {
        // 1. Character artwork aligned to right with enhanced visibility (offloaded from main thread).
        bitmap?.let { loadedBitmap ->
            Image(
                bitmap = loadedBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.38f)
            )
        }

        // 2. High-contrast gradient scrim balancing artwork visibility on the right with text clarity on the left.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0C0D12).copy(alpha = 0.92f),
                            Color(0xFF0C0D12).copy(alpha = 0.75f),
                            Color(0xFF10121A).copy(alpha = 0.45f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF08090C).copy(alpha = 0.70f)
                        )
                    )
                )
        )

        // 3. Ambient core radial bloom.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isActive) 0.14f else 0.04f),
                            Color.Transparent
                        ),
                        radius = 480f
                    )
                )
        )

        // 4. Subtle corner HUD brackets.
        ConsoleHudFrame(
            accentColor = accentColor,
            isActive = isActive,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ConsoleHudFrame(
    accentColor: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.drawWithCache {
            val width = size.width
            val height = size.height
            val bracketLen = 14.dp.toPx()
            val strokePx = 1.dp.toPx()
            val bracketColor = accentColor.copy(alpha = if (isActive) 0.28f else 0.12f)

            onDrawBehind {
                // Top-Left bracket.
                drawLine(
                    color = bracketColor,
                    start = Offset(14.dp.toPx(), 14.dp.toPx()),
                    end = Offset(14.dp.toPx() + bracketLen, 14.dp.toPx()),
                    strokeWidth = strokePx
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(14.dp.toPx(), 14.dp.toPx()),
                    end = Offset(14.dp.toPx(), 14.dp.toPx() + bracketLen),
                    strokeWidth = strokePx
                )

                // Top-Right bracket.
                drawLine(
                    color = bracketColor,
                    start = Offset(width - 14.dp.toPx() - bracketLen, 14.dp.toPx()),
                    end = Offset(width - 14.dp.toPx(), 14.dp.toPx()),
                    strokeWidth = strokePx
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(width - 14.dp.toPx(), 14.dp.toPx()),
                    end = Offset(width - 14.dp.toPx(), 14.dp.toPx() + bracketLen),
                    strokeWidth = strokePx
                )

                // Bottom-Left bracket.
                drawLine(
                    color = bracketColor,
                    start = Offset(14.dp.toPx(), height - 14.dp.toPx()),
                    end = Offset(14.dp.toPx() + bracketLen, height - 14.dp.toPx()),
                    strokeWidth = strokePx
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(14.dp.toPx(), height - 14.dp.toPx() - bracketLen),
                    end = Offset(14.dp.toPx(), height - 14.dp.toPx()),
                    strokeWidth = strokePx
                )

                // Bottom-Right bracket.
                drawLine(
                    color = bracketColor,
                    start = Offset(width - 14.dp.toPx() - bracketLen, height - 14.dp.toPx()),
                    end = Offset(width - 14.dp.toPx(), height - 14.dp.toPx()),
                    strokeWidth = strokePx
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(width - 14.dp.toPx(), height - 14.dp.toPx() - bracketLen),
                    end = Offset(width - 14.dp.toPx(), height - 14.dp.toPx()),
                    strokeWidth = strokePx
                )
            }
        }
    )
}
