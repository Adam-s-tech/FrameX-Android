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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

/**
 * Performance Tools section with streamlined action buttons and live perf_game_list inspection.
 */
@Composable
fun VivoPerformanceToolsSection(
    launcherGames: Set<String>,
    perfGameList: List<String>,
    rawPerfGameList: String?,
    onRefreshPerfList: () -> Unit,
    onAddAllToPerfList: (Set<String>, (Boolean) -> Unit) -> Unit,
    onRemoveAllFromPerfList: (Set<String>, (Boolean) -> Unit) -> Unit,
    onCompileAll: (Set<String>, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onRefreshPerfList()
    }

    val addedCount = remember(launcherGames, perfGameList) {
        launcherGames.count { it in perfGameList }
    }
    val allAdded = launcherGames.isNotEmpty() && addedCount == launcherGames.size

    var isAddingOrRemoving by remember { mutableStateOf(false) }
    var isCompiling by remember { mutableStateOf(false) }
    var compileStatusText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FrameXSpacing.XLarge),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performance Tools",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Text(
                text = if (allAdded) "$addedCount games in list" else "$addedCount / ${launcherGames.size} in list",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (allAdded) Color(0xFF10B981) else Color(0xFF0EA5E9)
            )
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Button 1: Add/Remove All from perf_game_list
            Button(
                onClick = {
                    if (!isAddingOrRemoving) {
                        isAddingOrRemoving = true
                        if (allAdded) {
                            onRemoveAllFromPerfList(launcherGames) { isAddingOrRemoving = false }
                        } else {
                            onAddAllToPerfList(launcherGames) { isAddingOrRemoving = false }
                        }
                    }
                },
                enabled = launcherGames.isNotEmpty() && !isAddingOrRemoving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allAdded) Color(0xFF1E293B) else MaterialTheme.colorScheme.primary,
                    contentColor = if (allAdded) Color(0xFF38BDF8) else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {
                if (isAddingOrRemoving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (allAdded) "Remove All" else "Add All Games",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Button 2: Speed Compile (AOT)
            OutlinedButton(
                onClick = {
                    if (!isCompiling) {
                        isCompiling = true
                        compileStatusText = null
                        onCompileAll(launcherGames) { ok ->
                            isCompiling = false
                            compileStatusText = if (ok) "Compiled successfully" else "Compilation failed"
                        }
                    }
                },
                enabled = launcherGames.isNotEmpty() && !isCompiling,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF14B8A6).copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF14B8A6)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            ) {
                if (isCompiling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF14B8A6),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Compile All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        AnimatedVisibility(visible = compileStatusText != null) {
            compileStatusText?.let { msg ->
                Text(
                    text = msg,
                    fontSize = 12.sp,
                    color = if (msg.contains("success", ignoreCase = true)) Color(0xFF10B981) else Color(0xFFFF5252),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Live perf_game_list Card
        Card(
            shape = FrameXShapes.Card,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(FrameXBorders.ActiveBorderWidth, FrameXBorders.CardStroke),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE perf_game_list",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.Gray
                    )

                    IconButton(
                        onClick = onRefreshPerfList,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh list",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF090A0D))
                        .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    when {
                        rawPerfGameList == null -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF0EA5E9),
                                strokeWidth = 2.dp
                            )
                        }
                        rawPerfGameList.isBlank() -> {
                            Text(
                                text = "perf_game_list is currently empty on this device",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF6B7080)
                            )
                        }
                        else -> {
                            Text(
                                text = rawPerfGameList,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFC7CAD9),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
