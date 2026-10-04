package com.framex.app.ui.screens.overlay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.R
import com.framex.app.ui.components.OverlayDisplayConfig
import com.framex.app.ui.components.OverlayPreviewContent
import com.framex.app.ui.screens.overlay.ModuleRowState

/**
 * Preview stage displaying live overlay appearance with gaming atmospheric backdrop.
 * Only displays currently enabled modules and icons per active configuration.
 */
@Composable
fun OverlayPreviewCard(
    modules: List<ModuleRowState>,
    selectedMode: String,
    opacity: Float,
    accentColor: Color,
    colorIndex: Int,
    fontFamily: FontFamily?,
    textScale: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header: Pure white icon and typography matching Dashboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.overlay_preview_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }

        // Preview Window Box with Atmospheric Gaming Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF090B10))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.16f),
                            Color(0xFF090B10).copy(alpha = 0.95f),
                            Color(0xFF06070B)
                        ),
                        radius = 650f
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
                            Color.Transparent,
                            accentColor.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val enabledModules = remember(modules) {
                modules.filter { it.enabled }.map { it.id.storageKey }.toSet()
            }
            val moduleOrder = remember(modules) {
                modules.map { it.id.storageKey }
            }
            val enabledModuleIcons = remember(modules) {
                modules.filter { it.showIcon }.map { it.id.storageKey }.toSet()
            }

            OverlayPreviewContent(
                config = OverlayDisplayConfig(
                    mode = selectedMode,
                    enabledModules = enabledModules,
                    moduleOrder = moduleOrder,
                    opacity = opacity,
                    overlayScale = textScale,
                    useMonospace = fontFamily == FontFamily.Monospace,
                    colorIndex = colorIndex,
                    enabledModuleIcons = enabledModuleIcons
                )
            )
        }
    }
}
