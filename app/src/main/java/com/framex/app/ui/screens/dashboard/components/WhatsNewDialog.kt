package com.framex.app.ui.screens.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.framex.app.R
import com.framex.app.ui.screens.dashboard.UpdateItemCategory
import com.framex.app.ui.screens.dashboard.WhatsNewFeatureItem
import com.framex.app.ui.screens.dashboard.WhatsNewInfo

private val ModalBackground = Color(0xFF0F1015)

@Composable
fun WhatsNewDialog(
    info: WhatsNewInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ModalBackground),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Cybernetic Anime Hero Artwork Background (Animated GIF via ImageDecoder)
                    AnimatedGifImage(
                        drawableResId = R.drawable.post_update_hero,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    )

                    // Left-to-Right Scrim to keep typography crisp
                    val horizontalScrim = remember {
                        Brush.horizontalGradient(
                            colors = listOf(
                                ModalBackground,
                                ModalBackground.copy(alpha = 0.92f),
                                ModalBackground.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(horizontalScrim)
                    )

                    // Vertical Scrim blending bottom into the card content
                    val verticalScrim = remember {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ModalBackground.copy(alpha = 0.70f),
                                ModalBackground
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(verticalScrim)
                    )

                    // Modal Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header Row: What's New Title + Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "What's New",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "v${info.versionName} (Build ${info.versionCode})",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = info.subtitle,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.70f),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Configurable Feature Items List
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(info.items, key = { it.id }) { item ->
                                WhatsNewItemCard(item = item)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Primary "Got it" Button
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Got it",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhatsNewItemCard(
    item: WhatsNewFeatureItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF14151B))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.customIcon ?: resolveCategoryIcon(item.category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (item.isNewBadge) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "NEW",
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.description,
                fontSize = 11.5.sp,
                color = Color.White.copy(alpha = 0.65f),
                lineHeight = 15.sp
            )
        }
    }
}

private fun resolveCategoryIcon(category: UpdateItemCategory): ImageVector = when (category) {
    UpdateItemCategory.LIVE_METRICS -> Icons.Default.Analytics
    UpdateItemCategory.GAME_MODE -> Icons.Default.SportsEsports
    UpdateItemCategory.UI_THEME -> Icons.Default.Palette
    UpdateItemCategory.BUG_FIXES -> Icons.Default.Build
    UpdateItemCategory.HARDWARE -> Icons.Default.Memory
    UpdateItemCategory.PRIVILEGE -> Icons.Default.Security
}

@Composable
private fun AnimatedGifImage(
    drawableResId: Int,
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            android.widget.ImageView(ctx).apply {
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(resources, drawableResId)
                    val drawable = android.graphics.ImageDecoder.decodeDrawable(source)
                    setImageDrawable(drawable)
                    if (drawable is android.graphics.drawable.AnimatedImageDrawable) {
                        drawable.start()
                    }
                } else {
                    setImageResource(drawableResId)
                }
            }
        },
        modifier = modifier
    )
}
