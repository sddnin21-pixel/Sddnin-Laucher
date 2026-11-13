package com.example.ui.widgets

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LauncherPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Feature #8: Pomodoro Focus Timer Widget
 */
@Composable
fun PomodoroWidget(
    isRunning: Boolean,
    secondsLeft: Int,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    val timeStr = String.format("%02d:%02d", mins, secs)
    val progress = 1f - (secondsLeft / (25f * 60f)).coerceIn(0f, 1f)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pomodoro Tập Trung", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = if (isRunning) "Đang chạy" else "Sẵn sàng",
                color = if (isRunning) Color(0xFFF43F5E) else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeStr,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF43F5E))
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable { onReset() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
            color = Color(0xFFF43F5E),
            trackColor = Color(0x33FFFFFF)
        )
    }
}

/**
 * Feature #9: Event Countdown Widget
 */
@Composable
fun CountdownWidget(
    title: String,
    daysLeft: Int,
    onAdjustDays: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = daysLeft.toString(),
                color = Color(0xFF38BDF8),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("ngày", color = Color(0xFF94A3B8), fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable { onAdjustDays((daysLeft - 1).coerceAtLeast(0)) }
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text("-1", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3338BDF8))
                    .clickable { onAdjustDays(daysLeft + 1) }
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text("+1", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Feature #10: Daily Habit Tracker Widget
 */
@Composable
fun HabitTrackerWidget(
    habitStates: String,
    onToggleHabit: (Int) -> Unit
) {
    val habits = listOf("Uống 2L nước", "Đọc 15 trang", "Tập thể dục", "Ngủ trước 23h")
    val states = List(4) { idx -> habitStates.getOrNull(idx) == '1' }
    val doneCount = states.count { it }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thói Quen Hàng Ngày", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("$doneCount/4 (${doneCount * 25}%)", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                HabitRowChip(habits[0], states[0]) { onToggleHabit(0) }
                HabitRowChip(habits[1], states[1]) { onToggleHabit(1) }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                HabitRowChip(habits[2], states[2]) { onToggleHabit(2) }
                HabitRowChip(habits[3], states[3]) { onToggleHabit(3) }
            }
        }

        LinearProgressIndicator(
            progress = { doneCount / 4f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF10B981),
            trackColor = Color(0x33FFFFFF)
        )
    }
}

@Composable
private fun HabitRowChip(label: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (checked) Color(0x2810B981) else Color(0x22FFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (checked) Color(0xFF10B981) else Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color.White, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Feature #11: Mini Pocket Calculator Widget
 */
@Composable
fun MiniCalculatorWidget() {
    var display by remember { mutableStateOf("0") }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var firstVal by remember { mutableStateOf(0.0) }
    var startNew by remember { mutableStateOf(true) }

    fun pressDigit(d: String) {
        display = if (startNew || display == "0") d else (display + d).take(9)
        startNew = false
    }

    fun pressOp(op: String) {
        firstVal = display.toDoubleOrNull() ?: 0.0
        pendingOp = op
        startNew = true
    }

    fun evaluate() {
        val second = display.toDoubleOrNull() ?: 0.0
        if (pendingOp == "÷" && second == 0.0) {
            display = "Lỗi"
            pendingOp = null
            startNew = true
            return
        }
        val res = when (pendingOp) {
            "+" -> firstVal + second
            "-" -> firstVal - second
            "×" -> firstVal * second
            "÷" -> firstVal / second
            else -> second
        }
        display = when {
            res.isNaN() || res.isInfinite() -> "Lỗi"
            res % 1.0 == 0.0 && kotlin.math.abs(res) < Long.MAX_VALUE.toDouble() -> res.toLong().toString()
            else -> String.format(Locale.US, "%.2f", res)
        }
        pendingOp = null
        startNew = true
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x44000000))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
            Text(display, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        val rows = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        rows.forEach { r ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                r.forEach { key ->
                    val isOp = key in listOf("÷", "×", "-", "+", "=")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (key) {
                                    "=" -> Color(0xFF38BDF8)
                                    "C" -> Color(0x55EF4444)
                                    else -> if (isOp) Color(0x44A855F7) else Color(0x28FFFFFF)
                                }
                            )
                            .clickable {
                                when (key) {
                                    "C" -> { display = "0"; pendingOp = null; startNew = true }
                                    "=" -> evaluate()
                                    "+", "-", "×", "÷" -> pressOp(key)
                                    else -> pressDigit(key)
                                }
                            }
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = if (key == "=") Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Feature #12: Calendar & Daily Quote Widget
 */
@Composable
fun QuoteCalendarWidget() {
    val quotes = listOf(
        "Mỗi ngày mới là một cơ hội tuyệt vời để bắt đầu lại.",
        "Tập trung vào tiến bộ nhỏ mỗi ngày sẽ tạo nên kỳ tích.",
        "Đừng chờ đợi cơ hội, hãy tự tạo ra cơ hội cho mình.",
        "Kiên trì là chìa khóa mở mọi cánh cửa thành công.",
        "Bình yên bắt đầu từ một nụ cười nhẹ nhàng buổi sáng."
    )
    var quoteIdx by remember { mutableIntStateOf(0) }
    val dateStr = remember {
        SimpleDateFormat("EEE, dd/MM/yyyy", Locale("vi", "VN")).format(Date()).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { quoteIdx = (quoteIdx + 1) % quotes.size },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FormatQuote, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(dateStr, color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("Chạm đổi câu nói", color = Color(0xFF94A3B8), fontSize = 9.sp)
        }

        Text(
            text = "\"${quotes[quoteIdx]}\"",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Text("Sddnin Daily Wisdom • Âm lịch an lành", color = Color(0xFF38BDF8), fontSize = 10.sp)
    }
}

/**
 * Feature #13: Quick Daily Expense Tracker Widget
 */
@Composable
fun ExpenseTrackerWidget(
    totalToday: Long,
    onAddExpense: (Long) -> Unit,
    onResetExpense: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Chi Tiêu Hôm Nay", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Đặt lại",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                modifier = Modifier.clickable { onResetExpense() }
            )
        }

        Text(
            text = String.format(Locale.US, "%,d đ", totalToday),
            color = Color(0xFF10B981),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                "+15k Cafe" to 15000L,
                "+35k Ăn" to 35000L,
                "+50k Xăng" to 50000L
            ).forEach { (label, amount) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x3310B981))
                        .border(1.dp, Color(0x5510B981), RoundedCornerShape(10.dp))
                        .clickable { onAddExpense(amount) }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
