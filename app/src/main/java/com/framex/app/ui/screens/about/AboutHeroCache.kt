package com.framex.app.ui.screens.about

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.framex.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * In-memory bitmap cache for the About & Legal hero character artwork.
 * Prevents main thread bitmap decoding and allocation churn during Compose composition.
 * Uses RGB_565 configuration to reduce memory footprint on lower-end devices.
 */
object AboutHeroCache {
    @Volatile
    private var cachedBitmap: ImageBitmap? = null
    @Volatile
    private var cachedAvatarBitmap: ImageBitmap? = null
    @Volatile
    private var cachedQrBitmap: ImageBitmap? = null

    fun get(): ImageBitmap? = cachedBitmap
    fun getAvatar(): ImageBitmap? = cachedAvatarBitmap
    fun getQrCode(): ImageBitmap? = cachedQrBitmap

    suspend fun load(context: Context, resId: Int = R.drawable.about_hero): ImageBitmap? {
        cachedBitmap?.let { return it }
        return withContext(Dispatchers.IO) {
            cachedBitmap ?: synchronized(AboutHeroCache) {
                cachedBitmap ?: runCatching {
                    val options = BitmapFactory.Options().apply {
                        inScaled = false
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    BitmapFactory.decodeResource(context.resources, resId, options)
                        ?.asImageBitmap()
                        ?.also { cachedBitmap = it }
                }.getOrNull()
            }
        }
    }

    suspend fun loadAvatar(context: Context): ImageBitmap? {
        cachedAvatarBitmap?.let { return it }
        return withContext(Dispatchers.IO) {
            cachedAvatarBitmap ?: synchronized(AboutHeroCache) {
                cachedAvatarBitmap ?: runCatching {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 4 // Downsamples 800x800 avatar to 200x200
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    BitmapFactory.decodeResource(context.resources, R.drawable.avatar_creator, options)
                        ?.asImageBitmap()
                        ?.also { cachedAvatarBitmap = it }
                }.getOrNull()
            }
        }
    }

    suspend fun loadQrCode(context: Context): ImageBitmap? {
        cachedQrBitmap?.let { return it }
        return withContext(Dispatchers.IO) {
            cachedQrBitmap ?: synchronized(AboutHeroCache) {
                cachedQrBitmap ?: runCatching {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 8 // Downsamples 3000x3000 QR to 375x375 (saves ~35MB RAM)
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    BitmapFactory.decodeResource(context.resources, R.drawable.qr_code, options)
                        ?.asImageBitmap()
                        ?.also { cachedQrBitmap = it }
                }.getOrNull()
            }
        }
    }

    fun clear() {
        cachedBitmap = null
        cachedAvatarBitmap = null
        cachedQrBitmap = null
    }
}
