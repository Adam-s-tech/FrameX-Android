package com.framex.app.ui.screens.performance.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.gaming.AppInfo
import com.framex.app.ui.components.WovenNetBackground
import com.framex.app.ui.screens.performance.components.AppWhitelistRow
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

fun LazyListScope.AppWhitelistSection(
    userApps: List<AppInfo>,
    whitelist: Set<String>,
    onToggleWhitelist: (String) -> Unit
) {
    item(key = "app_whitelist_section") {
        AppWhitelistContent(
            userApps = userApps,
            whitelist = whitelist,
            onToggleWhitelist = onToggleWhitelist
        )
    }
}

@Composable
private fun AppWhitelistContent(
    userApps: List<AppInfo>,
    whitelist: Set<String>,
    onToggleWhitelist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Pin whitelisted apps to the top, then sort alphabetically.
    val sortedApps = remember(userApps, whitelist) {
        userApps.sortedWith(
            compareByDescending<AppInfo> { whitelist.contains(it.packageName) }
                .thenBy { it.label.lowercase() }
        )
    }

    val visibleApps = if (isExpanded) sortedApps else sortedApps.take(4)

    Column(modifier = modifier.padding(horizontal = FrameXSpacing.XLarge)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "App Whitelist",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = "${whitelist.size} protected",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Apps switched ON will not be killed or restricted when Gaming Mode activates.",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        if (userApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                visibleApps.forEach { app ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = FrameXShapes.Medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(FrameXBorders.ActiveBorderWidth, FrameXBorders.CardStroke)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            WovenNetBackground(modifier = Modifier.matchParentSize())
                            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                                AppWhitelistRow(
                                    app = app,
                                    isWhitelisted = whitelist.contains(app.packageName),
                                    onToggle = { onToggleWhitelist(app.packageName) }
                                )
                            }
                        }
                    }
                }
            }

            // Expand / Collapse action button.
            if (sortedApps.size > 4) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(role = Role.Button) { isExpanded = !isExpanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "Show All (${sortedApps.size} apps)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
