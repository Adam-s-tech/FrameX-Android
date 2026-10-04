package com.framex.app.ui.screens.dashboard

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

enum class UpdateItemCategory {
    LIVE_METRICS,
    GAME_MODE,
    UI_THEME,
    BUG_FIXES,
    HARDWARE,
    PRIVILEGE
}

@Immutable
data class WhatsNewFeatureItem(
    val id: String,
    val category: UpdateItemCategory,
    val title: String,
    val description: String,
    val isNewBadge: Boolean = false,
    val customIcon: ImageVector? = null
)

@Immutable
data class WhatsNewInfo(
    val versionName: String,
    val versionCode: Long,
    val subtitle: String = "Here's what's new in this update. Thank you for keeping FrameX up to date!",
    val items: List<WhatsNewFeatureItem> = emptyList()
)
