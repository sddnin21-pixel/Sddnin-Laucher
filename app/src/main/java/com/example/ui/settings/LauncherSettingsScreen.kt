package com.example.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.data.model.LauncherPreferences
import com.example.data.model.TouchParticleEffect
import com.example.data.model.TransitionEffect
import com.example.ui.components.getIconShape
import com.example.ui.theme.AccentColorsList
import com.example.ui.theme.WallpaperPresets

@Composable
fun LauncherSettingsScreen(
    isOpen: Boolean,
    preferences: LauncherPreferences,
    allApps: List<AppInfo>,
    onClose: () -> Unit,
    onUpdateGrid: (Int, Int) -> Unit,
    onUpdateIconShape: (IconShape) -> Unit,
    onUpdateIconScale: (Float) -> Unit,
    onUpdateAccentColor: (String) -> Unit,
    onUpdateWallpaper: (Int) -> Unit,
    onToggleShowLabels: () -> Unit,
    onUpdateTransition: (TransitionEffect) -> Unit,
    onUpdatePin: (String) -> Unit,
    onToggleAppLock: (String) -> Unit,
    onToggleAppHidden: (String) -> Unit,
    onToggleFocusMode: () -> Unit,
    onOpenFocusDialog: () -> Unit,
    onOpenGestureStudio: () -> Unit = {},
    onOpenThemeEngine: () -> Unit = {},
    onOpenVault: () -> Unit = {},
    onOpenAppManager: () -> Unit = {},
    onOpenWallpaperCustomizer: () -> Unit = {},
    onToggleDynamicIsland: () -> Unit = {},
    onToggleEdgeSidebar: () -> Unit = {},
    onToggleZenMode: () -> Unit = {},
    onSelectTouchParticle: (TouchParticleEffect) -> Unit = {},
    onUpdateBlueLightFilter: (Float) -> Unit = {},
    onSelectIconPackStyle: (IconPackStyle) -> Unit = {},
    onToggleDockNeonBorder: () -> Unit = {},
    onToggleAutoProfileByTime: () -> Unit = {},
    onToggleSoundEffects: () -> Unit = {},
    onToggleHapticFeedback: () -> Unit = {},
    onOpenToolbox: () -> Unit = {},
    onOpenGestureDraw: () -> Unit = {},
    onOpenNightstand: () -> Unit = {},
    onOpenUsageInsights: () -> Unit = {},
    onOpenNotificationDigest: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onExportBackup: () -> String,
    onRestoreBackup: (String) -> Boolean
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
    ) {
        var selectedTab by remember { mutableIntStateOf(0) }
        var showChangePinDialog by remember { mutableStateOf(false) }
        var showBackupRestoreDialog by remember { mutableStateOf(false) }
        var backupJsonText by remember { mutableStateOf("") }

        val sortedUsage = remember(allApps) {
            allApps.sortedByDescending { it.usageCount }.take(6)
        }
        val maxCount = remember(sortedUsage) {
            (sortedUsage.firstOrNull()?.usageCount ?: 1).coerceAtLeast(1)
        }

        if (showChangePinDialog) {
            var newPin by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showChangePinDialog = false },
                title = { Text("Đổi mã PIN bảo mật") },
                text = {
                    Column {
                        Text("Nhập mã PIN mới (4 số):", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it },
                            label = { Text("Mã PIN (4 chữ số)") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPin.length == 4) {
                            onUpdatePin(newPin)
                            showChangePinDialog = false
                        }
                    }) {
                        Text("Lưu mã PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showChangePinDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }

        if (showBackupRestoreDialog) {
            AlertDialog(
                onDismissRequest = { showBackupRestoreDialog = false },
                title = { Text("Sao lưu & Khôi phục Bố cục") },
                text = {
                    Column {
                        Text("Dán mã JSON bản sao lưu vào bên dưới để khôi phục:", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = backupJsonText,
                            onValueChange = { backupJsonText = it },
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (backupJsonText.isNotBlank()) {
                                onRestoreBackup(backupJsonText)
                                showBackupRestoreDialog = false
                            }
                        }
                    ) {
                        Text("Khôi phục ngay")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBackupRestoreDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xFF0A0E17)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cài đặt Sddnin Launcher",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Settings Tabs
                val tabs = listOf(
                    Pair("Giao diện", Icons.Default.Palette),
                    Pair("Lưới & Icon", Icons.Default.GridOn),
                    Pair("3.0 Pro", Icons.Default.AutoAwesome),
                    Pair("Bảo mật", Icons.Default.Lock),
                    Pair("Thống kê", Icons.Default.QueryStats),
                    Pair("Sao lưu", Icons.Default.Backup)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs.indices.toList()) { idx ->
                        val isSelected = selectedTab == idx
                        val (title, icon) = tabs[idx]
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                .clickable { selectedTab = idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // GIAO DIỆN & HÌNH NỀN
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                                        .clickable { onOpenThemeEngine() }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Theme Engine 2.0 & Chợ Theme", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Live Theme theo giờ, Chợ theme cộng đồng & AI Wallpaper", color = Color(0xFFBAE6FD), fontSize = 11.sp)
                                    }
                                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF38BDF8))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                SectionHeader("Hình nền động, Ảnh & Nhạc nền")
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = onOpenWallpaperCustomizer,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tùy chỉnh Ảnh, Video lặp & Nhạc nền...", fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(WallpaperPresets.indices.toList()) { idx ->
                                        val (name, brush) = WallpaperPresets[idx]
                                        val isChosen = preferences.wallpaperIndex == idx
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { onUpdateWallpaper(idx) }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 64.dp, height = 96.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(brush)
                                                    .border(
                                                        if (isChosen) 2.5.dp else 1.dp,
                                                        if (isChosen) Color(0xFF38BDF8) else Color(0x33FFFFFF),
                                                        RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isChosen) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(name, color = Color(0xFFCBD5E1), fontSize = 10.sp, maxLines = 1)
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                SectionHeader("Màu nhấn (Accent Color)")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AccentColorsList.forEach { (name, hex) ->
                                        val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Cyan }
                                        val isSel = preferences.accentColorHex.equals(hex, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(c)
                                                .clickable { onUpdateAccentColor(hex) }
                                                .border(
                                                    if (isSel) 3.dp else 0.dp,
                                                    if (isSel) Color.White else Color.Transparent,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                SectionHeader("Hiệu ứng chuyển trang (Page Transition)")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(TransitionEffect.entries) { effect ->
                                        val isSel = preferences.transitionEffect == effect
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                                .clickable { onUpdateTransition(effect) }
                                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                text = effect.title,
                                                color = if (isSel) Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // LƯỚI & ICON
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF8B5CF6).copy(alpha = 0.25f))
                                        .border(1.dp, Color(0xFFA78BFA), RoundedCornerShape(16.dp))
                                        .clickable { onOpenGestureStudio() }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Gesture Studio & Macro 2.0", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Cài đặt cử chỉ vuốt, chạm 2 ngón, pinch & chuỗi macro", color = Color(0xFFDDD6FE), fontSize = 11.sp)
                                    }
                                    Icon(Icons.Default.Gesture, contentDescription = null, tint = Color(0xFFA78BFA))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                SectionHeader("Bố cục lưới màn hình chính")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Kích thước lưới: ${preferences.gridColumns} cột x ${preferences.gridRows} hàng", color = Color.White, fontSize = 14.sp)
                                        Text("Thay đổi số cột và hàng trên màn hình chính", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(Pair(4, 5), Pair(4, 6), Pair(5, 5), Pair(5, 6)).forEach { (c, r) ->
                                        val isSel = preferences.gridColumns == c && preferences.gridRows == r
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                                .clickable { onUpdateGrid(c, r) }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text("${c}x${r}", color = if (isSel) Color.Black else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                SectionHeader("Kiểu bo góc icon (Icon Shape)")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconShape.entries.forEach { shape ->
                                        val isSel = preferences.iconShape == shape
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { onUpdateIconShape(shape) }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(getIconShape(shape))
                                                    .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF334155))
                                                    .border(
                                                        if (isSel) 2.dp else 0.dp,
                                                        Color.White,
                                                        getIconShape(shape)
                                                    )
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(shape.title, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                SectionHeader("Kích thước icon (${(preferences.iconScale * 100).toInt()}%)")
                                Slider(
                                    value = preferences.iconScale,
                                    onValueChange = onUpdateIconScale,
                                    valueRange = 0.8f..1.25f,
                                    steps = 8,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF38BDF8),
                                        activeTrackColor = Color(0xFF38BDF8)
                                    )
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Hiển thị tên ứng dụng", color = Color.White, fontSize = 14.sp)
                                        Text("Bật hoặc tắt nhãn chữ bên dưới icon", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.showLabels,
                                        onCheckedChange = { onToggleShowLabels() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF38BDF8)
                                        )
                                    )
                                }
                            }
                        }

                        2 -> {
                            // LAUNCHER 3.0 PRO FEATURES
                            item {
                                SectionHeader("Trải nghiệm Sddnin Launcher 3.0")
                                Spacer(modifier = Modifier.height(6.dp))
                                SettingToggleRow(
                                    title = "Dynamic Island",
                                    subtitle = "Viên thuốc đa nhiệm trên đỉnh màn hình (Pomodoro, Nhạc, Pin)",
                                    checked = preferences.dynamicIslandEnabled,
                                    onToggle = onToggleDynamicIsland
                                )
                                SettingToggleRow(
                                    title = "Thanh Cạnh Edge Sidebar",
                                    subtitle = "Chạm cạnh phải để mở nhanh app yêu thích & công cụ 3.0",
                                    checked = preferences.edgeSidebarEnabled,
                                    onToggle = onToggleEdgeSidebar
                                )
                                SettingToggleRow(
                                    title = "Chế độ Zen Tối Giản",
                                    subtitle = "Màn hình tập trung ít phân tâm với trích dẫn & 4 app thiết yếu",
                                    checked = preferences.zenModeEnabled,
                                    onToggle = onToggleZenMode
                                )
                                SettingToggleRow(
                                    title = "Viền Neon Thanh Dock",
                                    subtitle = "Hiệu ứng viền gradient phát sáng quanh thanh dưới",
                                    checked = preferences.dockNeonBorderEnabled,
                                    onToggle = onToggleDockNeonBorder
                                )
                                SettingToggleRow(
                                    title = "Tự chuyển Hồ sơ theo giờ",
                                    subtitle = "8h–17h dùng hồ sơ Công việc, tối dùng Cá nhân",
                                    checked = preferences.autoProfileByTime,
                                    onToggle = onToggleAutoProfileByTime
                                )
                                SettingToggleRow(
                                    title = "Âm thanh phản hồi (Sound FX)",
                                    subtitle = "Phát âm click nhẹ tổng hợp khi chạm ứng dụng & nút",
                                    checked = preferences.soundEffectsEnabled,
                                    onToggle = onToggleSoundEffects
                                )
                                SettingToggleRow(
                                    title = "Rung phản hồi xúc giác (Haptics)",
                                    subtitle = "Rung nhẹ tinh tế khi tương tác & hoàn thành Pomodoro",
                                    checked = preferences.hapticFeedbackEnabled,
                                    onToggle = onToggleHapticFeedback
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Hiệu ứng hạt màn hình (Ambient Particles)")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(TouchParticleEffect.entries) { effect ->
                                        val isSel = preferences.touchParticleEffect == effect
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                                .clickable { onSelectTouchParticle(effect) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = effect.title,
                                                color = if (isSel) Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Phong cách Icon Pack 3.0")
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(IconPackStyle.entries) { style ->
                                        val isSel = preferences.iconPackStyle == style
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) Color(0xFFA855F7) else Color(0xFF1E293B))
                                                .clickable { onSelectIconPackStyle(style) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = style.title,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Lọc Ánh Sáng Xanh Bảo Vệ Mắt (${(preferences.blueLightFilterIntensity * 100).toInt()}%)")
                                Slider(
                                    value = preferences.blueLightFilterIntensity,
                                    onValueChange = onUpdateBlueLightFilter,
                                    valueRange = 0f..0.45f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFFFBBF24),
                                        activeTrackColor = Color(0xFFF59E0B)
                                    )
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                SectionHeader("Mở nhanh Tiện ích 3.0")
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    QuickOpenRow("Hộp Công Cụ Đa Năng", "Quy đổi, Bấm giờ, La bàn, Tạo QR", Color(0xFFFBBF24), onOpenToolbox)
                                    QuickOpenRow("Sức Khỏe Số & Thống Kê", "Phân bố danh mục, điểm cân bằng A+", Color(0xFF10B981), onOpenUsageInsights)
                                    QuickOpenRow("Tóm Tắt Thông Báo Thông Minh", "Gom nhóm thông báo ưu tiên", Color(0xFF38BDF8), onOpenNotificationDigest)
                                    QuickOpenRow("Vẽ Chữ Mở Nhanh", "Vẽ O, C, M, W, Z ngay trên màn hình", Color(0xFFA855F7), onOpenGestureDraw)
                                    QuickOpenRow("Chế Độ Bàn Đêm AMOLED", "Đồng hồ lật số khổng lồ & nhạc nền", Color(0xFF818CF8), onOpenNightstand)
                                    QuickOpenRow("Huy Hiệu & Thành Tựu", "Xem tiến trình khám phá 3.0 (${preferences.unlockedAchievements.size}/6)", Color(0xFFF472B6), onOpenAchievements)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        3 -> {
                            // BẢO MẬT & RIÊNG TƯ
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0F766E).copy(alpha = 0.25f))
                                        .border(1.dp, Color(0xFF14B8A6), RoundedCornerShape(16.dp))
                                        .clickable { onOpenVault() }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Két Mã Hóa & Riêng Tư Động 2.0", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Kho ứng dụng bảo mật, Hồ sơ trẻ em/khách & Nhật ký", color = Color(0xFFCCFBF1), fontSize = 11.sp)
                                    }
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF14B8A6))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                SectionHeader("Mã PIN bảo vệ")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF1E293B))
                                        .clickable { showChangePinDialog = true }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Đổi mã PIN", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Mã PIN hiện tại: ••••", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    }
                                    Text("Thay đổi", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Khóa ứng dụng (${preferences.lockedPackages.size} app đã khóa)")
                                Text("Các app bị khóa sẽ yêu cầu nhập PIN 4 số trước khi mở:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            items(allApps) { app ->
                                val isLocked = preferences.lockedPackages.contains(app.packageName)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isLocked) Color(0x33EF4444) else Color(0xFF1E293B))
                                        .clickable { onToggleAppLock(app.packageName) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(app.displayLabel, color = Color.White, fontSize = 13.sp)
                                    Switch(
                                        checked = isLocked,
                                        onCheckedChange = { onToggleAppLock(app.packageName) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFFEF4444)
                                        )
                                    )
                                }
                            }
                        }

                        4 -> {
                            // THỐNG KÊ SỬ DỤNG
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE11D48).copy(alpha = 0.2f))
                                        .border(1.dp, Color(0xFFFB7185), RoundedCornerShape(16.dp))
                                        .clickable { onOpenAppManager() }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Quản Lý Ứng Dụng 2.0", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Dọn dẹp app ít dùng, cảnh báo app trùng chức năng & gỡ nhanh", color = Color(0xFFFFE4E6), fontSize = 11.sp)
                                    }
                                    Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color(0xFFFB7185))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                SectionHeader("Ứng dụng dùng nhiều nhất")
                                Text("Thống kê tần suất mở app trên thiết bị của bạn:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(sortedUsage) { app ->
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(app.displayLabel, color = Color.White, fontSize = 13.sp)
                                        Text("${app.usageCount} lần", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (app.usageCount.toFloat() / maxCount).coerceIn(0.05f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF38BDF8),
                                        trackColor = Color(0xFF1E293B)
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                SectionHeader("Chế độ tập trung (Focus Mode)")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF1E293B))
                                        .clickable { onOpenFocusDialog() }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Cấu hình Chế độ tập trung",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            if (preferences.focusModeActive) "Trạng thái: Đang BẬT" else "Trạng thái: Đang TẮT",
                                            color = if (preferences.focusModeActive) Color(0xFF34D399) else Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = Color(0xFFA78BFA))
                                }
                            }
                        }

                        5 -> {
                            // SAO LƯU & KHÔI PHỤC
                            item {
                                SectionHeader("Xuất cấu hình & Bố cục (JSON)")
                                Text("Lưu lại tất cả các trang, thư mục, widget và cài đặt:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val json = onExportBackup()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("SddninLauncherBackup", json))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sao chép cấu hình JSON vào bộ nhớ đệm", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                SectionHeader("Khôi phục từ bản sao lưu")
                                Text("Dán mã cấu hình JSON để nạp lại giao diện cũ:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showBackupRestoreDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Nhập cấu hình JSON...", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF38BDF8),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF38BDF8)
            )
        )
    }
}

@Composable
private fun QuickOpenRow(
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        Text("Mở", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
