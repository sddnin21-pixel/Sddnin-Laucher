package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.hypot

@Composable
fun GestureDrawOverlay(
    isOpen: Boolean,
    onClose: () -> Unit,
    onRecognizedLetter: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val points = remember { mutableStateListOf<Offset>() }
        var detectedHint by remember { mutableStateOf("Vẽ chữ cái trên màn hình...") }

        fun recognizeGesture(pts: List<Offset>): String? {
            if (pts.size < 8) return null
            val first = pts.first()
            val last = pts.last()
            val minX = pts.minOf { it.x }
            val maxX = pts.maxOf { it.x }
            val minY = pts.minOf { it.y }
            val maxY = pts.maxOf { it.y }
            val width = (maxX - minX).coerceAtLeast(1f)
            val height = (maxY - minY).coerceAtLeast(1f)
            val diag = hypot(width, height)
            val startEndDist = hypot(last.x - first.x, last.y - first.y)

            // Closed loop -> O
            if (startEndDist < diag * 0.28f && width > 60f && height > 60f) return "O"
            // Mostly horizontal -> - (Search)
            if (width > height * 2.2f) return "S"
            // Open to the right (C)
            val mid = pts[pts.size / 2]
            if (first.x > minX + width * 0.55f && last.x > minX + width * 0.55f && mid.x < minX + width * 0.45f) return "C"
            // Down-up-down-up (W) or Up-down-up-down (M)
            var dirChangesY = 0
            var prevDy = 0f
            for (i in 1 until pts.size) {
                val dy = pts[i].y - pts[i - 1].y
                if (kotlin.math.abs(dy) > 4f) {
                    if (prevDy != 0f && (dy > 0) != (prevDy > 0)) dirChangesY++
                    prevDy = dy
                }
            }
            if (dirChangesY >= 2) {
                return if (first.y < minY + height * 0.4f) "W" else "M"
            }
            // Diagonal Z-like
            if (first.y < minY + height * 0.35f && last.y > maxY - height * 0.35f) return "Z"
            return "S"
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE0050914))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Drawing Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { start ->
                                points.clear()
                                points.add(start)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                points.add(change.position)
                            },
                            onDragEnd = {
                                val letter = recognizeGesture(points)
                                if (letter != null) {
                                    detectedHint = "Đã nhận diện: $letter"
                                    onRecognizedLetter(letter)
                                    points.clear()
                                } else {
                                    detectedHint = "Nét vẽ ngắn quá, thử vẽ lại nhé!"
                                    points.clear()
                                }
                            }
                        )
                    }
            ) {
                if (points.size > 1) {
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) lineTo(points[i].x, points[i].y)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF38BDF8),
                        style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // Top Header & Legend
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Draw, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Vẽ Cử Chỉ Mở Nhanh", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(detectedHint, color = Color(0xFFFBBF24), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "O" to "Trình duyệt",
                        "C" to "Cài đặt",
                        "M" to "Nhạc nền",
                        "W" to "Công cụ",
                        "Z" to "Zen Mode",
                        "S" to "Tìm kiếm"
                    ).forEach { (key, title) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x22FFFFFF))
                                .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(12.dp))
                                .clickable { onRecognizedLetter(key) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(key, color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text(title, color = Color(0xFFCBD5E1), fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
