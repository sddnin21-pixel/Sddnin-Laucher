package com.example.ui.controls

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

data class QuickToggleItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isActive: Boolean,
    val onClick: () -> Unit
)

@Composable
fun QuickControlCenter(
    isOpen: Boolean,
    gameModeActive: Boolean,
    batterySaverActive: Boolean,
    oneHandedActive: Boolean,
    focusModeActive: Boolean,
    onToggleGameMode: () -> Unit,
    onToggleBatterySaver: () -> Unit,
    onToggleOneHanded: () -> Unit,
    onToggleFocusMode: () -> Unit,
    onLockScreen: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        var isWifiOn by remember { mutableStateOf(true) }
        var isBluetoothOn by remember { mutableStateOf(true) }
        var isFlashlightOn by remember { mutableStateOf(false) }
        var isSilentOn by remember { mutableStateOf(false) }
        var brightness by remember { mutableFloatStateOf(0.75f) }
        var volume by remember { mutableFloatStateOf(0.6f) }

        val toggles = listOf(
            QuickToggleItem("Wi-Fi", if (isWifiOn) "Đã kết nối" else "Tắt", Icons.Default.Wifi, isWifiOn) {
                isWifiOn = !isWifiOn
                try { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (e: Exception) {}
            },
            QuickToggleItem("Bluetooth", if (isBluetoothOn) "Đang bật" else "Tắt", Icons.Default.Bluetooth, isBluetoothOn) {
                isBluetoothOn = !isBluetoothOn
                try { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (e: Exception) {}
            },
            QuickToggleItem("Đèn pin", if (isFlashlightOn) "Đang bật" else "Tắt", Icons.Default.FlashlightOn, isFlashlightOn) {
                isFlashlightOn = !isFlashlightOn
            },
            QuickToggleItem("Chuông / Rung", if (isSilentOn) "Im lặng" else "Bình thường", Icons.Default.NotificationsOff, isSilentOn) {
                isSilentOn = !isSilentOn
            },
            QuickToggleItem("Chơi Game", if (gameModeActive) "Game Boost ON" else "Tắt", Icons.Default.SportsEsports, gameModeActive) {
                onToggleGameMode()
            },
            QuickToggleItem("Pin AMOLED", if (batterySaverActive) "Đang bật" else "Tắt", Icons.Default.BatteryAlert, batterySaverActive) {
                onToggleBatterySaver()
            },
            QuickToggleItem("Chế độ một tay", if (oneHandedActive) "Đang thu gọn" else "Toàn màn hình", Icons.Default.Smartphone, oneHandedActive) {
                onToggleOneHanded()
            },
            QuickToggleItem("Tập trung", if (focusModeActive) "Đang bật" else "Tắt", Icons.Default.CenterFocusStrong, focusModeActive) {
                onToggleFocusMode()
            }
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xF20F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bảng điều khiển nhanh", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Phím tắt hệ thống & Điều khiển launcher", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                    }
                }

                // Grid of 8 Quick Toggles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(toggles) { toggle ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (toggle.isActive) Color(0xFF0284C7).copy(alpha = 0.35f) else Color(0xFF1E293B))
                                .border(
                                    1.dp,
                                    if (toggle.isActive) Color(0xFF38BDF8) else Color(0x22FFFFFF),
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { toggle.onClick() }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (toggle.isActive) Color(0xFF38BDF8) else Color(0x22FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = toggle.icon,
                                    contentDescription = null,
                                    tint = if (toggle.isActive) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(toggle.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(toggle.subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Brightness Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BrightnessMedium, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Độ sáng màn hình", color = Color.White, fontSize = 12.sp)
                        }
                        Text("${(brightness * 100).toInt()}%", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    }
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFBBF24),
                            activeTrackColor = Color(0xFFFBBF24)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Volume Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Âm lượng đa phương tiện", color = Color.White, fontSize = 12.sp)
                        }
                        Text("${(volume * 100).toInt()}%", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    }
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF38BDF8)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Quick Lock Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.25f))
                        .clickable {
                            onLockScreen()
                            onClose()
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFC4B5FD))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Khóa màn hình ngay", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
