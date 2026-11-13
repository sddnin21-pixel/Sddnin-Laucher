package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView

@Composable
fun EdgeSidebarPanel(
    enabled: Boolean,
    favoriteApps: List<AppInfo>,
    iconShape: IconShape,
    iconPackStyle: IconPackStyle,
    zenModeActive: Boolean,
    onAppClick: (AppInfo) -> Unit,
    onOpenToolbox: () -> Unit,
    onOpenGestureDraw: () -> Unit,
    onOpenUsageInsights: () -> Unit,
    onOpenNightstand: () -> Unit,
    onOpenAchievements: () -> Unit,
    onToggleZenMode: () -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!enabled) return
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Right Edge Handle when collapsed
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 2.dp)
                    .width(22.dp)
                    .height(92.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x9938BDF8), Color(0x99A855F7))
                        )
                    )
                    .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount < -12f) isExpanded = true
                        }
                    }
                    .clickable { isExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Mở Thanh Cạnh",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Expanded Floating Edge Sidebar
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(74.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xEA0F172A))
                    .border(1.dp, Color(0x4438BDF8), RoundedCornerShape(28.dp))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable { isExpanded = false },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(14.dp))
                }

                // Quick Favorite Apps
                favoriteApps.take(3).forEach { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                isExpanded = false
                                onAppClick(app)
                            }
                            .padding(2.dp)
                    ) {
                        AppIconView(
                            app = app,
                            iconShape = iconShape,
                            size = 38.dp,
                            showBadge = false,
                            iconPackStyle = iconPackStyle
                        )
                    }
                }

                Box(modifier = Modifier.width(36.dp).height(1.dp).background(Color(0x33FFFFFF)))

                EdgeToolButton(Icons.Default.Build, "Công cụ", Color(0xFFFBBF24)) {
                    isExpanded = false; onOpenToolbox()
                }
                EdgeToolButton(Icons.Default.Draw, "Vẽ mở", Color(0xFF38BDF8)) {
                    isExpanded = false; onOpenGestureDraw()
                }
                EdgeToolButton(Icons.Default.QueryStats, "Thống kê", Color(0xFF10B981)) {
                    isExpanded = false; onOpenUsageInsights()
                }
                EdgeToolButton(Icons.Default.SelfImprovement, if (zenModeActive) "Thoát Zen" else "Zen", Color(0xFFF472B6)) {
                    isExpanded = false; onToggleZenMode()
                }
                EdgeToolButton(Icons.Default.DarkMode, "Chờ", Color(0xFFA855F7)) {
                    isExpanded = false; onOpenNightstand()
                }
                EdgeToolButton(Icons.Default.EmojiEvents, "Huy hiệu", Color(0xFFFB923C)) {
                    isExpanded = false; onOpenAchievements()
                }
                EdgeToolButton(Icons.Default.Lock, "Khóa", Color(0xFFEF4444)) {
                    isExpanded = false; onLockScreen()
                }
            }
        }
    }
}

@Composable
private fun EdgeToolButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 3.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color(0xFFE2E8F0), fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}
