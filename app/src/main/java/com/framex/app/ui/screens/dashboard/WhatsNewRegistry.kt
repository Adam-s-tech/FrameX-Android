package com.framex.app.ui.screens.dashboard

import com.framex.app.BuildConfig

object WhatsNewRegistry {

    /**
     * Debug testing flag. When true, forces the What's New modal to appear on every dashboard load.
     * Switch to false for release production behavior.
     */
    const val DEBUG_FORCE_SHOW_WHATS_NEW = true

    /**
     * Resolves the list of What's New items to display.
     * Configure specific releases and items here.
     */
    fun getWhatsNewForVersion(versionCode: Long): WhatsNewInfo? {
        val currentVersionCode = BuildConfig.VERSION_CODE.toLong()
        if (!DEBUG_FORCE_SHOW_WHATS_NEW && versionCode != currentVersionCode) return null

        val items = mutableListOf<WhatsNewFeatureItem>()

        items.add(
            WhatsNewFeatureItem(
                id = "live_metrics",
                category = UpdateItemCategory.LIVE_METRICS,
                title = "Live Metrics Improvements",
                description = "More stable FPS, CPU, GPU and RAM monitoring with improved accuracy.",
                isNewBadge = true
            )
        )

        items.add(
            WhatsNewFeatureItem(
                id = "game_mode",
                category = UpdateItemCategory.GAME_MODE,
                title = "Game Mode Enhancements",
                description = "Faster profile switching and better app detection for smoother performance."
            )
        )

        items.add(
            WhatsNewFeatureItem(
                id = "ui_theme",
                category = UpdateItemCategory.UI_THEME,
                title = "UI & Theme Updates",
                description = "Refined design, smoother animations and new customization options."
            )
        )

        items.add(
            WhatsNewFeatureItem(
                id = "bug_fixes",
                category = UpdateItemCategory.BUG_FIXES,
                title = "Bug Fixes",
                description = "General stability improvements and several minor bug fixes."
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
