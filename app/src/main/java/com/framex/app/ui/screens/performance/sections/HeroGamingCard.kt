package com.framex.app.ui.screens.performance.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.R
import com.framex.app.gaming.GamingModeState
import com.framex.app.ui.screens.performance.ActiveGamingSession
import com.framex.app.ui.screens.performance.components.ActiveSessionStatusCard
import com.framex.app.ui.screens.performance.components.GamingActivationChargeModule
import com.framex.app.ui.screens.performance.components.GamingConsoleBackground
import com.framex.app.ui.theme.FrameXAccessibility
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

/**
 * Flagship console gaming hero card.
 *
 * Provides a clean cinematic presentation in standby, transitioning to a bold
 * high-contrast charge beam during activation, and subsystem telemetry when active.
 */
@Composable
fun HeroGamingCard(
    gamingState: GamingModeState,
    animatedProgress: Float,
    canActivate: Boolean,
    isActive: Boolean,
    isBusy: Boolean,
    activeColor: Color,
    primaryRed: Color,
    activeSession: ActiveGamingSession? = null,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FrameXSpacing.XLarge),
        shape = FrameXShapes.CardLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = FrameXBorders.ActiveBorderWidth,
            color = if (isActive) activeColor.copy(alpha = 0.35f) else FrameXBorders.CardStroke
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Random character background with high-contrast gradient scrim.
            GamingConsoleBackground(
                accentColor = if (isActive) activeColor else primaryRed,
                isActive = isActive,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FrameXSpacing.Large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header with brand and status badge.
                HeroConsoleHeader(
                    gamingState = gamingState,
                    isActive = isActive,
                    activeColor = activeColor,
                    primaryRed = primaryRed
                )

                Spacer(modifier = Modifier.height(FrameXSpacing.Large))

                // State-driven body: Standby banner, bold activation charge module, or active banner.
                AnimatedContent(
                    targetState = when {
                        isBusy -> HeroBodyState.BUSY
                        isActive -> HeroBodyState.ACTIVE
                        else -> HeroBodyState.STANDBY
                    },
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) },
                    label = "heroBodyTransition"
                ) { state ->
                    when (state) {
                        HeroBodyState.BUSY -> {
                            GamingActivationChargeModule(
                                gamingState = gamingState,
                                animatedProgress = animatedProgress,
                                accentColor = if (isActive) activeColor else primaryRed
                            )
                        }
                        HeroBodyState.ACTIVE -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Gaming Mode Active",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val activeSubtext = if (activeSession != null) {
                                    "${activeSession.summary.totalApplied} optimizations applied • ${activeSession.suspendedAppsCount} apps suspended"
                                } else {
                                    "System optimizations active and monitoring performance."
                                }
                                Text(
                                    text = activeSubtext,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = Color.LightGray.copy(alpha = 0.85f)
                                )
                            }
                        }
                        HeroBodyState.STANDBY -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Gaming Mode",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 26.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Optimize system performance, prioritize games, and reduce background load.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = Color.LightGray.copy(alpha = 0.80f)
                                )
                            }
                        }
                    }
                }

                // Error diagnostic banner.
                ErrorBannerAlert(
                    gamingState = gamingState,
                    primaryRed = primaryRed
                )

                Spacer(modifier = Modifier.height(FrameXSpacing.Large))

                // Real ledger of executed subsystems when active.
                if (isActive && !isBusy) {
                    ActiveSessionStatusCard(session = activeSession)
                    Spacer(modifier = Modifier.height(FrameXSpacing.Standard))
                }

                // Primary tactical console trigger button.
                HeroActionButton(
                    gamingState = gamingState,
                    canActivate = canActivate,
                    isActive = isActive,
                    isBusy = isBusy,
                    activeColor = activeColor,
                    primaryRed = primaryRed,
                    onActivate = onActivate,
                    onDeactivate = onDeactivate
                )
            }
        }
    }
}

private enum class HeroBodyState {
    STANDBY,
    BUSY,
    ACTIVE
}

/**
 * Top header row with title and status badge.
 */
@Composable
private fun HeroConsoleHeader(
    gamingState: GamingModeState,
    isActive: Boolean,
    activeColor: Color,
    primaryRed: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PERFORMANCE ENGINE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Gray
        )

        ConsoleStatusBadge(
            isActive = isActive,
            gamingState = gamingState,
            activeColor = activeColor,
            primaryRed = primaryRed
        )
    }
}

/**
 * Status indicator pill displaying current engine state.
 */
@Composable
private fun ConsoleStatusBadge(
    isActive: Boolean,
    gamingState: GamingModeState,
    activeColor: Color,
    primaryRed: Color,
    modifier: Modifier = Modifier
) {
    val badgeColor = when {
        isActive -> activeColor
        gamingState is GamingModeState.Enabling || gamingState is GamingModeState.Disabling -> Color(0xFFF59E0B)
        gamingState is GamingModeState.Error -> primaryRed
        else -> Color.Gray
    }

    val badgeLabel = when {
        isActive -> "ACTIVE"
        gamingState is GamingModeState.Enabling -> "TUNING"
        gamingState is GamingModeState.Disabling -> "REVERTING"
        gamingState is GamingModeState.Error -> "ERROR"
        else -> "STANDBY"
    }

    Box(
        modifier = modifier
            .clip(FrameXShapes.Pill)
            .background(badgeColor.copy(alpha = 0.12f))
            .border(FrameXBorders.ActiveBorderWidth, badgeColor.copy(alpha = 0.30f), FrameXShapes.Pill)
            .padding(horizontal = FrameXSpacing.Medium, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = badgeLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = badgeColor
            )
        }
    }
}

/**
 * Error banner displaying failure message when an operation fails.
 */
@Composable
private fun ErrorBannerAlert(
    gamingState: GamingModeState,
    primaryRed: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = gamingState is GamingModeState.Error,
        modifier = modifier
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(primaryRed.copy(alpha = 0.12f))
                    .border(1.dp, primaryRed.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = primaryRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = (gamingState as? GamingModeState.Error)?.message ?: "Operation failed",
                    color = primaryRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * High-contrast primary action button for activation, deactivation, and retry.
 */
@Composable
private fun HeroActionButton(
    gamingState: GamingModeState,
    canActivate: Boolean,
    isActive: Boolean,
    isBusy: Boolean,
    activeColor: Color,
    primaryRed: Color,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(14.dp)

    if (isBusy) {
        Button(
            onClick = {},
            enabled = false,
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = FrameXAccessibility.StandardRowHeight),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.White.copy(alpha = 0.06f),
                disabledContentColor = Color.LightGray
            )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = activeColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (gamingState is GamingModeState.Enabling) "Activating Gaming Mode…" else "Restoring System…",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    } else if (isActive) {
        Button(
            onClick = onDeactivate,
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = FrameXAccessibility.StandardRowHeight),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryRed.copy(alpha = 0.14f),
                contentColor = primaryRed
            ),
            border = BorderStroke(1.dp, primaryRed.copy(alpha = 0.35f))
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(FrameXSpacing.Small))
            Text(
                text = "Deactivate Gaming Mode",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    } else if (gamingState is GamingModeState.Error && gamingState.message.contains("Deactivation", ignoreCase = true)) {
        Button(
            onClick = onDeactivate,
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = FrameXAccessibility.StandardRowHeight),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryRed,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(FrameXSpacing.Small))
            Text(
                text = "Retry Deactivation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    } else {
        Button(
            onClick = onActivate,
            enabled = canActivate,
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = FrameXAccessibility.StandardRowHeight),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = Color.White.copy(alpha = 0.05f),
                disabledContentColor = Color.Gray
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play_premium),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(FrameXSpacing.Small))
            Text(
                text = if (canActivate) "Activate Gaming Mode" else "Complete setup first",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    }
}
