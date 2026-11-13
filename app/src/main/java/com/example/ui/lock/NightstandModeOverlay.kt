package com.example.ui.lock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NightstandModeOverlay(
    isOpen: Boolean,
    batteryPercent: Int,
    musicEnabled: Boolean,
    musicPreset: String,
    onToggleMusic: () -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        var now by remember { mutableStateOf(Date()) }
        LaunchedEffect(isOpen) {
            while (isOpen) {
                now = Date()
                delay(1000L)
            }
        }

        val transition = rememberInfiniteTransition(label = "nightstand_glow")
        val glowAlpha by transition.animateFloat(
            initialValue = 0.25f, targetValue = 0.65f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
            label = "glow"
        )

        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
        val minFormat = SimpleDateFormat("mm", Locale.getDefault())
        val secFormat = SimpleDateFormat("ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("vi", "VN"))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF0F172A).copy(alpha = glowAlpha), Color(0xFF02040A))
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NightsStay, contentDescription = null, tint = Color(0xFFA855F7))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chế độ Bàn Đêm AMOLED", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0x22FFFFFF))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Thoát", tint = Color.White)
                }
            }

            // Flip Clock Centerpiece
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FlipClockDigitCard(text = hourFormat.format(now), accentColor = Color(0xFF38BDF8))
                    Text(":", color = Color(0xFF38BDF8), fontSize = 54.sp, fontWeight = FontWeight.Bold)
                    FlipClockDigitCard(text = minFormat.format(now), accentColor = Color(0xFFA855F7))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${secFormat.format(now)} giây",
                    color = Color(0xFF38BDF8),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = dateFormat.format(now).replaceFirstChar { it.uppercase() },
                    color = Color(0xFF94A3B8),
                    fontSize = 15.sp
                )
            }

            // Bottom Ambient Music & Battery Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(20.dp))
                        .clickable { onToggleMusic() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = if (musicEnabled) Color(0xFF38BDF8) else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (musicEnabled) "Đang phát: $musicPreset" else "Bật nhạc thư giãn",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$batteryPercent%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FlipClockDigitCard(text: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .size(width = 116.dp, height = 132.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0B1120))
            .border(1.5.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 68.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        // Flip horizontal divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xAA02040A))
                .align(Alignment.Center)
        )
    }
}
