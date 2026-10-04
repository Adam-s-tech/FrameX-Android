package com.framex.app.ui.screens.dashboard

import com.framex.app.BuildConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal

object WhatsNewRegistry {

    /**
     * Debug testing flag. When true, forces the What's New modal to appear on every dashboard load.
     * Switch to false for release production behavior.
     */
    const val DEBUG_FORCE_SHOW_WHATS_NEW = false

    /**
     * Resolves the list of What's New items to display.
     * Configure specific releases and items here.
     */
    fun getWhatsNewForVersion(versionCode: Long): WhatsNewInfo? {
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()
        if (!DEBUG_FORCE_SHOW_WHATS_NEW && versionCode != currentVersionCode) return null

        val items = listOf(
            WhatsNewFeatureItem(
                id = "execution_center",
                category = UpdateItemCategory.GAME_MODE,
                title = "Execution Center",
                description = "We moved all gaming mode commands into the Execution Center in About & Legal. Tap Execution Center to view and customize them directly.",
                isNewBadge = true,
                actionId = "execution_center",
                customIcon = Icons.Default.Terminal
            ),
            WhatsNewFeatureItem(
                id = "faster_gaming_mode",
                category = UpdateItemCategory.HARDWARE,
                title = "Faster Game Mode & Restoration",
                description = "Optimized background command execution and system restoration to apply and turn off settings much faster.",
                isNewBadge = true,
                customIcon = Icons.Default.Speed
            ),
            WhatsNewFeatureItem(
                id = "overlay_drag_jitter_fix",
                category = UpdateItemCategory.BUG_FIXES,
                title = "Overlay Drag & Screen Bounds",
                description = "Fixed dragging jitter on the floating overlay and corrected screen edge snapping when rotating the device.",
                customIcon = Icons.Default.OpenWith
            ),
            WhatsNewFeatureItem(
                id = "stability_and_memory_fixes",
                category = UpdateItemCategory.BUG_FIXES,
                title = "Stability & Memory Improvements",
                description = "Resolved background freezes, smoothed out scrolling lists, and eliminated disk lag to keep the app light and responsive.",
                customIcon = Icons.Default.Memory
            )
        )

        return WhatsNewInfo(
            versionName = BuildConfig.VERSION_NAME,
            versionCode = currentVersionCode,
            subtitle = "Here's what's new in this update. Thank you for keeping FrameX up to date!",
            items = items
        )
    }
}
