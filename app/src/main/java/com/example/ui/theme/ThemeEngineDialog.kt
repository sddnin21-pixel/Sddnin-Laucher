package com.example.ui.theme

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IconShape
import com.example.data.model.LauncherPreferences
import com.example.data.model.ThemePreset
import com.example.data.model.TransitionEffect

@Composable
fun ThemeEngineDialog(
    isOpen: Boolean,
    preferences: LauncherPreferences,
    onApplyPreset: (ThemePreset) -> Unit,
    onGenerateAiWallpaper: () -> Unit,
    onToggleLiveTheme: () -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val presets = listOf(
        ThemePreset(
            id = "cyberpunk",
            name = "Cyberpunk 2077",
            description = "Neon Cyan & Magenta, biểu tượng lục giác đậm chất tương lai",
            accentColorHex = "#06B6D4",
            wallpaperIndex = 1,
            iconShape = IconShape.HEXAGON
        ),
        ThemePreset(
            id = "slate_minimal",
            name = "Obsidian Tối Giản",
            description = "Đá đen tối giản, biểu tượng tròn, tinh tế và lịch lãm",
            accentColorHex = "#38BDF8",
            wallpaperIndex = 3,
            iconShape = IconShape.CIRCLE
        ),
        ThemePreset(
            id = "emerald_forest",
            name = "Rừng Ngọc Lục Bảo",
            description = "Xanh ngọc thiên nhiên dịu mắt, hình dáng squircle mềm mại",
            accentColorHex = "#10B981",
            wallpaperIndex = 2,
            iconShape = IconShape.SQUIRCLE
        ),
        ThemePreset(
            id = "sunset_amber",
            name = "Cam Hoàng Hôn",
            description = "Ánh rực ấm áp cuối ngày, bo góc cổ điển, năng động",
            accentColorHex = "#F97316",
            wallpaperIndex = 4,
            iconShape = IconShape.ROUNDED_SQUARE
        ),
        ThemePreset(
            id = "amoled_midnight",
            name = "AMOLED Đen Tuyền",
            description = "Tiết kiệm 100% pin màn hình OLED, sắc thái tương phản cao",
            accentColorHex = "#A855F7",
            wallpaperIndex = 5,
            iconShape = IconShape.ROUNDED_SQUARE
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFF0A0E17)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Theme Engine 2.0", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Chợ theme & Live Theme tự động", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF38BDF8)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Chợ Theme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("AI Wallpaper", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Live Theme Switch Card
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x331E293B)),
                                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Live Theme theo thời gian", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Tự đổi màu theme theo buổi sáng, chiều, tối và đêm", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.liveThemeEnabled,
                                        onCheckedChange = { onToggleLiveTheme() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF38BDF8)
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Text("Bộ Theme hoàn chỉnh từ cộng đồng:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }

                        items(presets) { preset ->
                            val color = try { Color(android.graphics.Color.parseColor(preset.accentColorHex)) } catch (e: Exception) { Color.Cyan }
                            val isApplied = preferences.wallpaperIndex == preset.wallpaperIndex && preferences.iconShape == preset.iconShape

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(
                                        if (isApplied) 2.dp else 1.dp,
                                        if (isApplied) Color(0xFF38BDF8) else Color(0x22FFFFFF),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { onApplyPreset(preset) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(color.copy(alpha = 0.25f))
                                        .border(1.5.dp, color, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Palette, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(preset.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        if (isApplied) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF0284C7))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Đang dùng", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text(preset.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onApplyPreset(preset) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isApplied) Color(0x33FFFFFF) else Color(0xFF38BDF8)
                                    )
                                ) {
                                    Text(if (isApplied) "Đã áp dụng" else "Áp dụng", color = if (isApplied) Color.White else Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // AI WALLPAPER GENERATOR TAB
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(38.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AI Wallpaper Generator", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Tạo hình nền thuật toán gradient thông minh theo bảng màu cá nhân của bạn mà không cần kết nối mạng.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onGenerateAiWallpaper() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tạo & Áp dụng hình nền AI", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
