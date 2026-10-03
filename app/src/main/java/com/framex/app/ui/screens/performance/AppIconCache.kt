package com.framex.app.ui.screens.performance

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.framex.app.utils.FrameXLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-performance, zero-churn memory cache for application icons.
 * Prevents redundant Binder transactions to PackageManager and Bitmap allocations
 * while scrolling lists of apps at 60/120Hz.
 */
object AppIconCache {
    private const val MAX_ENTRIES = 256
    private val cache = LruCache<String, ImageBitmap>(MAX_ENTRIES)

    fun get(packageName: String): ImageBitmap? = cache.get(packageName)

    fun put(packageName: String, bitmap: ImageBitmap) {
        cache.put(packageName, bitmap)
    }

    suspend fun loadIcon(context: Context, packageName: String): ImageBitmap? {
        val cached = get(packageName)
        if (cached != null) return cached

        return try {
            withContext(Dispatchers.IO) {
                val alreadyLoaded = get(packageName)
                if (alreadyLoaded != null) return@withContext alreadyLoaded

                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(packageName)
                val density = context.resources.displayMetrics.density
                val targetPx = (density * 48).toInt().coerceIn(72, 192)
                val width = if (drawable.intrinsicWidth in 1..targetPx) drawable.intrinsicWidth else targetPx
                val height = if (drawable.intrinsicHeight in 1..targetPx) drawable.intrinsicHeight else targetPx
                val bitmap = drawable.toBitmap(width = width, height = height).asImageBitmap()
                put(packageName, bitmap)
                bitmap
            }
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            FrameXLog.w("Failed to load app icon for $packageName", t)
            null
        }
    }

    fun clear() {
        cache.evictAll()
    }
}
