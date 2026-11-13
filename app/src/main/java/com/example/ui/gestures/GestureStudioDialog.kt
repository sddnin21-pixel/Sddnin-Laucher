package com.example.ui.gestures

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.data.model.GestureType
import com.example.data.model.LauncherMacro
import com.example.data.model.LauncherPreferences
import com.example.data.model.MacroStep

@Composable
fun GestureStudioDialog(
    isOpen: Boolean,
    preferences: LauncherPreferences,
    onUpdateGesture: (GestureType, ActionType) -> Unit,
    onRunMacro: (LauncherMacro) -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var selectedTab by remember { mutableIntStateOf(0) }

    val defaultMacros = listOf(
        LauncherMacro(
            id = "home_relax",
            name = "Về nhà & Thư giãn",
            description = "Bật Chế độ im lặng + Mở nhạc + Giảm độ sáng nhẹ",
            iconName = "weekend",
            steps = listOf(
                MacroStep("Bật Im lặng", "SILENT"),
                MacroStep("Mở Spotify / Zing", "com.spotify.music"),
                MacroStep("Độ sáng 40%", "40")
            )
        ),
        LauncherMacro(
            id = "deep_work",
            name = "Tập trung Học tập & Làm việc",
            description = "Bật Chế độ tập trung + Mở Ghi chú + Chặn app mạng xã hội",
            iconName = "work",
            steps = listOf(
                MacroStep("Bật Chế độ tập trung", "FOCUS_ON"),
                MacroStep("Mở Sổ tay", "QUICK_NOTES"),
                MacroStep("Khóa Zalo, Facebook", "LOCK_SOCIAL")
            )
        ),
        LauncherMacro(
            id = "ultra_battery",
            name = "Siêu tiết kiệm pin",
            description = "Chuyển Dark AMOLED + Dọn dẹp RAM + Tắt hiệu ứng",
            iconName = "battery_saver",
            steps = listOf(
                MacroStep("AMOLED Đen tuyền", "THEME_AMOLED"),
                MacroStep("Dọn RAM tức thì", "CLEAN_RAM"),
                MacroStep("Giảm hiệu ứng", "REDUCE_MOTION")
            )
        ),
        LauncherMacro(
            id = "game_boost",
            name = "Chiến Game FPS cao",
            description = "Game Boost + Tối ưu bộ nhớ + Tạm khóa cử chỉ vuốt",
            iconName = "sports_esports",
            steps = listOf(
                MacroStep("Tăng tốc Game", "BOOST_RAM"),
                MacroStep("Khóa cử chỉ mép", "LOCK_GESTURES"),
                MacroStep("Chế độ Không làm phiền", "DND_ON")
            )
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
                        Text("Gesture Studio & Macro", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Xưởng cử chỉ 2.0 & Tự động hóa", color = Color(0xFF38BDF8), fontSize = 11.sp)
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
                    text = { Text("Cử chỉ thông minh", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Macro tự động", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // GESTURES ASSIGNMENT LIST
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(GestureType.entries.toTypedArray()) { gesture ->
                            var expanded by remember { mutableStateOf(false) }

                            val currentAction = when (gesture) {
                                GestureType.SWIPE_UP -> preferences.swipeUpAction
                                GestureType.SWIPE_DOWN -> preferences.swipeDownAction
                                GestureType.DOUBLE_TAP -> preferences.doubleTapAction
                                GestureType.TWO_FINGER_SWIPE_UP -> preferences.twoFingerSwipeUpAction
                                GestureType.TWO_FINGER_SWIPE_DOWN -> preferences.twoFingerSwipeDownAction
                                GestureType.PINCH_IN -> preferences.pinchInAction
                                GestureType.PINCH_OUT -> preferences.pinchOutAction
                            }

                            val actionTitle = ActionType.entries.find { it.name == currentAction }?.title ?: currentAction

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { expanded = true }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Gesture, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(gesture.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Hành động: $actionTitle", color = Color(0xFF38BDF8), fontSize = 12.sp)
                                    }
                                }

                                Box {
                                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Color(0xFF1E293B))
                                    ) {
                                        ActionType.entries.forEach { action ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        action.title,
                                                        color = if (action.name == currentAction) Color(0xFF38BDF8) else Color.White,
                                                        fontSize = 13.sp
                                                    )
                                                },
                                                onClick = {
                                                    onUpdateGesture(gesture, action)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // MACRO ENGINE LIST
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Chuỗi tác vụ tự động chỉ với 1 chạm:",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        items(defaultMacros) { macro ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(macro.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text(macro.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Steps preview pills
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    macro.steps.forEach { step ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0x33FFFFFF))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(step.actionName, color = Color(0xFFE2E8F0), fontSize = 10.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { onRunMacro(macro) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Kích hoạt Macro ngay", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
