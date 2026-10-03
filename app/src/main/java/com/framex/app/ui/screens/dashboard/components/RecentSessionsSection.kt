package com.framex.app.ui.screens.dashboard.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.framex.app.R

private val ViewAllShape = RoundedCornerShape(6.dp)
private val SessionCardShape = RoundedCornerShape(14.dp)
private val SessionIconShape = RoundedCornerShape(10.dp)

/**
 * Recent Sessions Section for the Dashboard.
 * Displays previous gameplay performance telemetry sessions or a production-ready
 * empty state without fake mock telemetry. Header icon and title are rendered in white.
 */
@Composable
fun RecentSessionsSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val comingSoonMessage = stringResource(R.string.coming_soon)

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header: Pure white icon and typography, with View All action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.dashboard_recent_sessions),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(ViewAllShape)
                    .clickable {
                        Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.dashboard_view_all),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Sessions Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SessionCardShape)
                .background(Color(0xFF0F1015))
                .border(1.dp, Color(0xFF1E2028), SessionCardShape)
                .clickable {
                    Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show()
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(SessionIconShape)
                        .background(Color(0xFF1E2028)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.dashboard_no_recent_sessions),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.dashboard_no_recent_sessions_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF616161),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
