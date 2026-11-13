package com.example.ui.lock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SimulatedLockScreen(
    isActive: Boolean,
    onUnlock: () -> Unit
) {
    AnimatedVisibility(
        visible = isActive,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        var currentTime by remember { mutableStateOf(Date()) }

        LaunchedEffect(isActive) {
            while (isActive) {
                currentTime = Date()
                delay(1000)
            }
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd 'tháng' MM", Locale("vi", "VN"))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onUnlock() },
                        onTap = { onUnlock() }
                    )
                }
                .pointerInput(Unit) {
                    var totalDragY = 0f
                    var unlocked = false
                    detectVerticalDragGestures(
                        onDragStart = {
                            totalDragY = 0f
                            unlocked = false
                        },
                        onDragEnd = {
                            totalDragY = 0f
                            unlocked = false
                        },
                        onDragCancel = {
                            totalDragY = 0f
                            unlocked = false
                        }
                    ) { _, dragAmount ->
                        totalDragY += dragAmount
                        if (!unlocked && totalDragY < -50f) {
                            unlocked = true
                            onUnlock()
                        }
                    }
                }
                .padding(32.dp)
        ) {
            // Lock icon at top
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Khóa",
                tint = Color(0x88FFFFFF),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .size(20.dp)
            )

            // Center Clock & Date
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeFormat.format(currentTime),
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = dateFormat.format(currentTime).replaceFirstChar { it.uppercase() },
                    color = Color(0xFF94A3B8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " 88% • Sẵn sàng hoạt động",
                        color = Color(0xFF6EE7B7),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "\"Sự tập trung tạo nên khác biệt mỗi ngày.\"",
                    color = Color(0xAA38BDF8),
                    fontSize = 12.sp
                )
            }

            // Bottom action icons & glowing fingerprint unlock
            var torchOn by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0x2238BDF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Vân tay mở khóa",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Chạm cảm biến hoặc vuốt lên để mở khóa",
                    color = Color(0x88FFFFFF),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (torchOn) Color(0xFFFBBF24) else Color(0x33FFFFFF))
                            .clickable { torchOn = !torchOn },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = "Đèn pin",
                            tint = if (torchOn) Color.Black else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable { onUnlock() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Máy ảnh",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
