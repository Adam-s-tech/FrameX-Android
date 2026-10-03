package com.framex.app.ui.screens.about.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommandToggleCard(
    title: String,
    commandSummary: String,
    statusText: String,
    statusColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    warningText: String? = null,
    footnoteText: String? = null,
    content: (@Composable () -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
        border = BorderStroke(
            1.dp,
            if (isChecked) statusColor.copy(alpha = 0.35f)
            else Color.White.copy(alpha = 0.06f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CommandToggleHeader(
                title = title,
                statusText = statusText,
                statusColor = statusColor,
                isChecked = isChecked,
                onCheckedChange = onCheckedChange,
                icon = icon,
                iconTint = iconTint
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = commandSummary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 15.sp
            )

            if (footnoteText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = footnoteText,
                    fontSize = 11.sp,
                    color = Color(0xFFFBBF24).copy(alpha = 0.85f),
                    lineHeight = 15.sp
                )
            }

            if (warningText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                CommandWarningBox(warningText = warningText)
            }

            if (content != null) {
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
fun ChildCommandToggleRow(
    title: String,
    commandSummary: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0D15), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
                text = title,
                color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp
            )
            Text(
                text = commandSummary,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = if (enabled) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.25f),
                lineHeight = 14.sp
            )
        }

        Switch(
            checked = isChecked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF20202A)
            )
        )
    }
}

@Composable
fun ExperimentalSectionDivider(
    modifier: Modifier = Modifier,
    description: String = "Experimental optimizations. Experiment if encountering specific display arbitration or process constraints."
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier.padding(vertical = 14.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "EXPERIMENTAL SETTINGS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.05.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 11.5.sp,
            color = Color.Gray,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CommandToggleHeader(
    title: String,
    statusText: String,
    statusColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector?,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 12.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (iconTint != Color.Unspecified) iconTint else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF272730)
            )
        )
    }
}

@Composable
private fun CommandWarningBox(warningText: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEF4444).copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp).padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = warningText,
            color = Color(0xFFFCA5A5),
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}
