package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppCategory
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.data.model.WidgetType
import com.example.ui.components.AppIconView

/**
 * Feature #15: Usage Insights & Digital Wellbeing Score Dialog
 */
@Composable
fun UsageInsightsDialog(
    isOpen: Boolean,
    apps: List<AppInfo>,
    iconShape: IconShape,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    val topApps = remember(apps) { apps.sortedByDescending { it.usageCount }.take(6) }
    val totalLaunches = remember(apps) { apps.sumOf { it.usageCount }.coerceAtLeast(1) }

    val categoryCounts = remember(apps) {
        AppCategory.entries.filter { it != AppCategory.ALL }.map { cat ->
            cat to apps.filter { it.category == cat }.sumOf { it.usageCount }
        }
    }
    val maxCat = remember(categoryCounts) { categoryCounts.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1 }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.93f).fillMaxHeight(0.82f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sức Khỏe Số & Thống Kê", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x33FFFFFF))) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wellbeing Score Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E293B))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Điểm cân bằng hôm nay", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Text("86 / 100 • Rất tốt", color = Color(0xFF10B981), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Tổng lượt mở ứng dụng: $totalLaunches", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier.size(54.dp).clip(CircleShape).background(Color(0x3310B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A+", color = Color(0xFF10B981), fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("PHÂN BỐ THEO DANH MỤC", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryCounts.forEach { (cat, count) ->
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(cat.title, color = Color.White, fontSize = 12.sp)
                                Text("$count điểm", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { (count.toFloat() / maxCat).coerceIn(0.05f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF38BDF8),
                                trackColor = Color(0x22FFFFFF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("ỨNG DỤNG MỞ NHIỀU NHẤT", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(topApps) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconView(app = app, iconShape = iconShape, size = 36.dp, showBadge = false, iconPackStyle = iconPackStyle)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.displayLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(app.category.title, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            Text("${app.usageCount} lượt", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Feature #16: Smart Notification Digest Dialog
 */
data class DigestNotificationItem(val id: Int, val appName: String, val summary: String, val time: String, val priority: Boolean)

@Composable
fun NotificationDigestDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    val items = remember {
        mutableStateListOf(
            DigestNotificationItem(1, "Tin nhắn & Zalo", "3 tin nhắn mới từ nhóm Dự án & Gia đình", "2 phút trước", true),
            DigestNotificationItem(2, "Lịch & Pomodoro", "Nhắc nhở nghỉ mắt 5 phút sau phiên tập trung", "15 phút trước", true),
            DigestNotificationItem(3, "Hệ thống Sddnin 3.0", "Pin ổn định • Bộ nhớ đã được tối ưu mượt mà", "1 giờ trước", false),
            DigestNotificationItem(4, "Thời tiết", "Chiều nay trời mát mẻ, nhiệt độ khoảng 26°C", "2 giờ trước", false)
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tóm Tắt Thông Báo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x33FFFFFF))) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tuyệt vời! Bạn đã xử lý hết thông báo.", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEach { n ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, if (n.priority) Color(0x5538BDF8) else Color.Transparent, RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(n.appName, color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("• ${n.time}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(n.summary, color = Color.White, fontSize = 12.sp)
                                }
                                IconButton(onClick = { items.remove(n) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Đánh dấu xong", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { items.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đánh dấu tất cả đã đọc", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Feature #17: Quick Shortcut & Action Builder Dialog
 */
@Composable
fun ShortcutBuilderDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAddWidgetPreset: (WidgetType) -> Unit,
    onTriggerAction: (String) -> Unit
) {
    if (!isOpen) return
    var customTitle by remember { mutableStateOf("") }

    val quickPresets = listOf(
        Triple("Thêm Đồng hồ Pomodoro", "Đặt đồng hồ tập trung 25p ra màn hình chính", WidgetType.POMODORO),
        Triple("Thêm Máy Tính Mini", "Đặt máy tính bỏ túi ra màn hình chính", WidgetType.MINI_CALCULATOR),
        Triple("Thêm Bảng Thói Quen", "Theo dõi các thói quen tích cực hàng ngày", WidgetType.HABIT_TRACKER),
        Triple("Thêm Chi Tiêu Nhanh", "Ghi nhanh khoản chi tiêu trong ngày", WidgetType.EXPENSE_TRACKER),
        Triple("Thêm Đếm Ngược Sự Kiện", "Đếm ngày đến sự kiện quan trọng", WidgetType.COUNTDOWN),
        Triple("Thêm Lịch & Trích Dẫn", "Lịch và câu nói hay mỗi ngày", WidgetType.QUOTE_CALENDAR)
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddLink, contentDescription = null, tint = Color(0xFFA855F7))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tạo Lối Tắt & Widget Nhanh", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x33FFFFFF))) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    label = { Text("Tên sự kiện đếm ngược tùy chỉnh", color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFA855F7),
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                if (customTitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onTriggerAction("COUNTDOWN_TITLE:$customTitle")
                            onAddWidgetPreset(WidgetType.COUNTDOWN)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tạo Đếm Ngược: \"$customTitle\"", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickPresets.forEach { (title, desc, type) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    onAddWidgetPreset(type)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            Text("+ Thêm", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Feature #23: Launcher Achievements & Badges Dialog
 */
@Composable
fun AchievementsDialog(
    isOpen: Boolean,
    unlockedIds: Set<String>,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    val allBadges = listOf(
        Triple("WELCOME_3_0", "Người Tiên Phong 3.0", "Khởi chạy phiên bản nâng cấp Sddnin Launcher 3.0"),
        Triple("POMODORO_MASTER", "Bậc Thầy Tập Trung", "Bắt đầu phiên đồng hồ Pomodoro trên Dynamic Island / Widget"),
        Triple("ZEN_EXPLORER", "Tâm Tĩnh Lặng", "Kích hoạt chế độ Zen Mode tối giản"),
        Triple("WIDGET_ARCHITECT", "Kiến Trúc Sư Widget", "Thêm hoặc tùy chỉnh Widget 2.0 / 3.0"),
        Triple("GESTURE_WIZARD", "Pháp Sư Cử Chỉ", "Sử dụng tính năng vẽ chữ mở nhanh ứng dụng"),
        Triple("AMBIENT_CHILL", "Thư Giãn Tuyệt Đối", "Bật nhạc nền thư giãn hoặc Chế độ Bàn Đêm")
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFBBF24))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Thành Tựu Launcher", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Đã mở khóa ${unlockedIds.size} / ${allBadges.size} huy hiệu", color = Color(0xFFFBBF24), fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x33FFFFFF))) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allBadges.forEach { (id, title, desc) ->
                        val unlocked = unlockedIds.contains(id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (unlocked) Color(0xFF1E293B) else Color(0xFF131B2E))
                                .border(1.dp, if (unlocked) Color(0x66FBBF24) else Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (unlocked) Color(0x33FBBF24) else Color(0x1AFFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (unlocked) Color(0xFFFBBF24) else Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, color = if (unlocked) Color.White else Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                            if (unlocked) {
                                Text("ĐẠT", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
