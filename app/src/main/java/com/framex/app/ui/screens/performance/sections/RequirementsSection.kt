package com.framex.app.ui.screens.performance.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.ui.screens.performance.components.RequirementRow
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

/**
 * Compressed System Status card displaying requirement states at a glance
 * with collapsible granular diagnostic details.
 */
@Composable
fun RequirementsSection(
    shizukuReady: Boolean,
    isShizukuAvailable: Boolean,
    hasWriteSettingsAccess: Boolean,
    hasDndAccess: Boolean,
    hasNotifListenerAccess: Boolean,
    onRequestShizuku: () -> Unit,
    onRequestWriteSettings: () -> Unit,
    onRequestDndAccess: () -> Unit,
    onRequestNotificationListener: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    val activeCount = listOf(
        shizukuReady,
        hasWriteSettingsAccess,
        hasDndAccess,
        hasNotifListenerAccess
    ).count { it }

    val allReady = activeCount == 4
    val accentGreen = Color(0xFF10B981)
    val warningAmber = Color(0xFFF59E0B)

    Column(modifier = modifier.padding(horizontal = FrameXSpacing.XLarge)) {
        // Section header with title and Details toggle.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "System Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(role = Role.Button) { showDetails = !showDetails }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showDetails) "Hide" else "Details",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Compressed main card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = FrameXShapes.Card,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(FrameXBorders.ActiveBorderWidth, FrameXBorders.CardStroke)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FrameXSpacing.Large)
            ) {
                // Primary status banner row.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (allReady) accentGreen.copy(alpha = 0.16f) else warningAmber.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (allReady) Icons.Default.Check else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (allReady) accentGreen else warningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (allReady) "System Ready" else "Setup Incomplete",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (allReady) "All 4 required services are active" else "$activeCount of 4 required services active",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(16.dp))

                // 4-column glance telemetry row.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompactStatusColumnItem(
                        title = "Shizuku",
                        subtitle = if (shizukuReady) "Connected" else if (isShizukuAvailable) "Grant" else "Offline",
                        isSatisfied = shizukuReady,
                        accentGreen = accentGreen,
                        warningAmber = warningAmber,
                        onClick = {
                            if (!shizukuReady) {
                                onRequestShizuku()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    CompactStatusColumnItem(
                        title = "Write Settings",
                        subtitle = if (hasWriteSettingsAccess) "Authorized" else "Denied",
                        isSatisfied = hasWriteSettingsAccess,
                        accentGreen = accentGreen,
                        warningAmber = warningAmber,
                        onClick = {
                            if (!hasWriteSettingsAccess) {
                                onRequestWriteSettings()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    CompactStatusColumnItem(
                        title = "DND Policy",
                        subtitle = if (hasDndAccess) "Available" else "Missing",
                        isSatisfied = hasDndAccess,
                        accentGreen = accentGreen,
                        warningAmber = warningAmber,
                        onClick = {
                            if (!hasDndAccess) {
                                onRequestDndAccess()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    CompactStatusColumnItem(
                        title = "Notification",
                        subtitle = if (hasNotifListenerAccess) "Active" else "Disabled",
                        isSatisfied = hasNotifListenerAccess,
                        accentGreen = accentGreen,
                        warningAmber = warningAmber,
                        onClick = {
                            if (!hasNotifListenerAccess) {
                                onRequestNotificationListener()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Collapsible detailed diagnostic descriptions.
                AnimatedVisibility(visible = showDetails) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(4.dp))

                        RequirementRow(
                            label = "Shizuku Service",
                            description = if (shizukuReady) "Connected — ADB shell is available"
                            else if (isShizukuAvailable) "Running but permission not granted"
                            else "Shizuku not running",
                            satisfied = shizukuReady,
                            onAction = if (!shizukuReady) onRequestShizuku else null,
                            actionLabel = if (isShizukuAvailable) "Grant" else "Open"
                        )

                        RequirementRow(
                            label = "Write System Settings",
                            description = if (hasWriteSettingsAccess) "Authorized to modify brightness & rotation"
                            else "Required for custom brightness/rotate overrides",
                            satisfied = hasWriteSettingsAccess,
                            onAction = onRequestWriteSettings
                        )

                        RequirementRow(
                            label = "DND / Interruption Policy",
                            description = if (hasDndAccess) "Can suppress notifications via DND"
                            else "Required to enable Do Not Disturb during gaming",
                            satisfied = hasDndAccess,
                            onAction = onRequestDndAccess
                        )

                        RequirementRow(
                            label = "Notification Listener",
                            description = if (hasNotifListenerAccess) "Active — system notifications will be cancelled"
                            else "Optional: cancels notifications that bypass DND",
                            satisfied = hasNotifListenerAccess,
                            onAction = onRequestNotificationListener,
                            actionLabel = "Enable"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactStatusColumnItem(
    title: String,
    subtitle: String,
    isSatisfied: Boolean,
    accentGreen: Color,
    warningAmber: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isSatisfied) accentGreen else warningAmber

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .border(1.dp, iconColor.copy(alpha = 0.40f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSatisfied) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(13.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = iconColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
