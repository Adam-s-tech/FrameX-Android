package com.framex.app.ui.screens.performance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.framex.app.gaming.ledger.LedgerSummary
import com.framex.app.gaming.ledger.OpStatus
import com.framex.app.gaming.ledger.Stage
import com.framex.app.gaming.ledger.StageStatus
import com.framex.app.gaming.ledger.StageSummary
import com.framex.app.ui.screens.performance.ActiveGamingSession
import com.framex.app.ui.theme.FrameXBorders
import com.framex.app.ui.theme.FrameXShapes
import com.framex.app.ui.theme.FrameXSpacing

private val SessionCardShape = FrameXShapes.Large
private val HeaderTagShape = RoundedCornerShape(4.dp)

/**
 * Tactical subsystem ledger displaying active optimizations, command status,
 * and stage-level execution hierarchies with skipped operations filtered out.
 */
@Composable
fun ActiveSessionStatusCard(
    session: ActiveGamingSession?,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFF10B981)

    // Filter out stages and operations that were skipped to keep telemetry actionable.
    val stages = remember(session?.summary?.stages) {
        session?.summary?.stages.orEmpty().mapNotNull { stageSummary ->
            val appliedPrimary = stageSummary.primaryOps.filter { it.status != OpStatus.SKIPPED }
            val appliedDetail = stageSummary.detailOps.filter { it.status != OpStatus.SKIPPED }
            if (appliedPrimary.isEmpty() && appliedDetail.isEmpty() && stageSummary.status == StageStatus.SKIPPED) {
                null
            } else {
                stageSummary.copy(
                    primaryOps = appliedPrimary,
                    detailOps = appliedDetail
                )
            }
        }
    }

    var userExpandedOverrides by remember { mutableStateOf<Map<Stage, Boolean>>(emptyMap()) }

    val allExpanded = stages.isNotEmpty() && stages.all { stageSummary ->
        userExpandedOverrides[stageSummary.stage] == true
    }

    Card(
        shape = SessionCardShape,
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.06f)),
        modifier = modifier
            .fillMaxWidth()
            .border(FrameXBorders.ActiveBorderWidth, accentColor.copy(alpha = 0.2f), SessionCardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FrameXSpacing.Standard),
            verticalArrangement = Arrangement.spacedBy(FrameXSpacing.Small)
        ) {
            ActiveSessionHeaderRow(
                title = session?.title ?: "Console Engine Synchronized",
                hasStages = stages.isNotEmpty(),
                allExpanded = allExpanded,
                onToggleExpandAll = {
                    val target = !allExpanded
                    userExpandedOverrides = stages.associate { it.stage to target }
                },
                accentColor = accentColor
            )

            session?.summary?.let { summary ->
                ActiveSessionStatsSummary(summary = summary, accentColor = accentColor)
            }

            HorizontalDivider(color = accentColor.copy(alpha = 0.15f))

            ActiveSessionStagesList(
                stages = stages,
                userExpandedOverrides = userExpandedOverrides,
                onToggleStage = { stage, targetExpanded ->
                    userExpandedOverrides = userExpandedOverrides + (stage to targetExpanded)
                }
            )
        }
    }
}

@Composable
private fun ActiveSessionStatsSummary(
    summary: LedgerSummary,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    if (summary.totalApplied <= 0 && summary.totalFailed <= 0) return
    val statsText = buildString {
        append("Applied ${summary.totalApplied}")
        if (summary.totalFailed > 0) {
            append(" · ${summary.totalFailed} failed")
        }
    }
    Text(
        text = statsText,
        color = accentColor.copy(alpha = 0.85f),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
private fun ActiveSessionStagesList(
    stages: List<StageSummary>,
    userExpandedOverrides: Map<Stage, Boolean>,
    onToggleStage: (Stage, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (stages.isEmpty()) {
        Text(
            text = "No active subsystem telemetry for this session",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = modifier.padding(vertical = 4.dp)
        )
    } else {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            stages.forEach { stageSummary ->
                key(stageSummary.stage) {
                    val isExpanded = userExpandedOverrides[stageSummary.stage] ?: false
                    StatusBlock(
                        stageSummary = stageSummary,
                        isExpanded = isExpanded,
                        onToggleExpand = { onToggleStage(stageSummary.stage, !isExpanded) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveSessionHeaderRow(
    title: String,
    hasStages: Boolean,
    allExpanded: Boolean,
    onToggleExpandAll: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        if (hasStages) {
            Text(
                text = if (allExpanded) "Collapse all" else "Expand all",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .clip(HeaderTagShape)
                    .clickable(role = Role.Button, onClick = onToggleExpandAll)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
