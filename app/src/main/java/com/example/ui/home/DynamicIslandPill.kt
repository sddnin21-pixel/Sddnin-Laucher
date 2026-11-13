package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LauncherPreferences
import kotlinx.coroutines.delay

@Composable
fun DynamicIslandPill(
    preferences: LauncherPreferences,
    batteryPercent: Int,
    isCharging: Boolean,
    pomodoroRunning: Boolean,
    pomodoroSecondsLeft: Int,
    onTogglePomodoro: () -> Unit,
    onToggleMusic: () -> Unit,
    onOpenToolbox: () -> Unit,
    onOpenNightstand: () -> Unit,
    onToggleZenMode: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition(label = "island_eq")
    val eq1 by transition.animateFloat(
        initialValue = 4f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "eq1"
    )
    val eq2 by transition.animateFloat(
        initialValue = 12f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(340, easing = LinearEasing), RepeatMode.Reverse),
        label = "eq2"
    )

    val mins = pomodoroSecondsLeft / 60
    val secs = pomodoroSecondsLeft % 60
    val pomoStr = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = spring(dampingRatio = 0.72f))
                .clip(RoundedCornerShape(if (expanded) 26.dp else 99.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xEE0B1120), Color(0xEE131C31), Color(0xEE0B1120))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0x6638BDF8),
                            if (pomodoroRunning) Color(0x99F43F5E) else Color(0x66A855F7),
                            Color(0x6638BDF8)
                        )
                    ),
                    shape = RoundedCornerShape(if (expanded) 26.dp else 99.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = if (expanded) 12.dp else 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Compact Pill Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Live Indicator
                if (pomodoroRunning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(pomoStr, color = Color(0xFFF43F5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (preferences.backgroundMusicEnabled) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.height(14.dp)
                    ) {
                        Box(Modifier.width(2.5.dp).height(eq1.dp).background(Color(0xFF38BDF8), RoundedCornerShape(2.dp)))
                        Box(Modifier.width(2.5.dp).height(eq2.dp).background(Color(0xFFA855F7), RoundedCornerShape(2.dp)))
                        Box(Modifier.width(2.5.dp).height(eq1.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
                    }
                } else {
                    Icon(
                        imageVector = if (preferences.zenModeEnabled) Icons.Default.SelfImprovement else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Center Status Text
                AnimatedContent(
                    targetState = when {
                        pomodoroRunning -> "Đang tập trung • $pomoStr"
                        preferences.backgroundMusicEnabled -> "Đang phát: ${preferences.backgroundMusicPreset}"
                        preferences.zenModeEnabled -> "Zen Mode • Tối giản"
                        else -> "Dynamic Island • Sddnin 3.0"
                    },
                    label = "island_text"
                ) { statusText ->
                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right Battery Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = if (isCharging || batteryPercent > 30) Color(0xFF10B981) else Color(0xFFF43F5E),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$batteryPercent%",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expanded Dynamic Island Controls
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IslandActionChip(
                            icon = if (pomodoroRunning) Icons.Default.Pause else Icons.Default.Timer,
                            label = if (pomodoroRunning) "Tạm dừng" else "Pomodoro",
                            tint = Color(0xFFF43F5E),
                            onClick = onTogglePomodoro
                        )
                        IslandActionChip(
                            icon = if (preferences.backgroundMusicEnabled) Icons.Default.Pause else Icons.Default.MusicNote,
                            label = if (preferences.backgroundMusicEnabled) "Tắt nhạc" else "Nhạc nền",
                            tint = Color(0xFF38BDF8),
                            onClick = onToggleMusic
                        )
                        IslandActionChip(
                            icon = Icons.Default.Build,
                            label = "Công cụ",
                            tint = Color(0xFFFBBF24),
                            onClick = {
                                expanded = false
                                onOpenToolbox()
                            }
                        )
                        IslandActionChip(
                            icon = Icons.Default.DarkMode,
                            label = "Nightstand",
                            tint = Color(0xFFA855F7),
                            onClick = {
                                expanded = false
                                onOpenNightstand()
                            }
                        )
                        IslandActionChip(
                            icon = Icons.Default.Notifications,
                            label = "Thông báo",
                            tint = Color(0xFF10B981),
                            onClick = {
                                expanded = false
                                onOpenNotifications()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IslandActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f))
                .border(1.dp, tint.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = label, color = Color(0xFFE2E8F0), fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}
