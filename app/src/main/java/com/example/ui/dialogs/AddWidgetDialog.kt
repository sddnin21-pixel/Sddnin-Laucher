package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppInfo
import com.example.data.model.IconShape
import com.example.data.model.WidgetType
import com.example.ui.components.AppIconView

@Composable
fun AddWidgetDialog(
    allApps: List<AppInfo>,
    iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    onDismiss: () -> Unit,
    onWidgetSelected: (WidgetType, spanX: Int, spanY: Int) -> Unit,
    onAppWidgetSelected: (packageName: String, label: String, spanX: Int, spanY: Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mọi ứng dụng (All Apps), 1: Tiện ích hệ thống (System & Stack)
    var searchQuery by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var selectedAppSpan by remember { mutableStateOf(Pair(2, 2)) }

    val filteredApps = remember(allApps, searchQuery) {
        if (searchQuery.isBlank()) allApps
        else allApps.filter {
            it.displayLabel.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Widget 2.0 Đa Năng",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Thêm widget mọi app & Smart Stack không giới hạn",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm ứng dụng hoặc tiện ích...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Xóa tìm kiếm", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dual Tabs: "Mọi Ứng Dụng" vs "Tiện Ích Đa Năng"
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF38BDF8),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF38BDF8)
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Widget Mọi App (${filteredApps.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Smart Stack & Hệ Thống", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content View according to Tab
                if (selectedTab == 0) {
                    // TAB 0: Widget Mọi Ứng Dụng (App Widgets)
                    if (selectedApp == null) {
                        Text(
                            text = "CHỌN BẤT KỲ ỨNG DỤNG NÀO ĐỂ TẠO WIDGET:",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredApps) { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1E293B))
                                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                                        .clickable { selectedApp = app }
                                        .padding(8.dp)
                                ) {
                                    AppIconView(
                                        app = app,
                                        iconShape = iconShape,
                                        size = 44.dp,
                                        showBadge = false
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = app.displayLabel,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "+ Widget",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // Configuration Panel for Selected App
                        val app = selectedApp!!
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1E293B))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AppIconView(app = app, iconShape = iconShape, size = 42.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(app.displayLabel, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Text(app.packageName, color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        }
                                    }

                                    IconButton(onClick = { selectedApp = null }) {
                                        Icon(Icons.Default.Close, contentDescription = "Đổi app khác", tint = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("CHỌN KÍCH THƯỚC WIDGET:", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val sizes = listOf(
                                        Triple("2x2 Tiêu chuẩn", 2, 2),
                                        Triple("4x2 Mở rộng", 4, 2),
                                        Triple("4x1 Thanh ngang", 4, 1)
                                    )

                                    sizes.forEach { (label, sx, sy) ->
                                        val isSel = selectedAppSpan.first == sx && selectedAppSpan.second == sy
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSel) Color(0xFF38BDF8) else Color(0x33FFFFFF))
                                                .clickable { selectedAppSpan = Pair(sx, sy) }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "${sx}x${sy}",
                                                    color = if (isSel) Color.Black else Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = label.substringAfter(" "),
                                                    color = if (isSel) Color.Black else Color(0xFFCBD5E1),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Live Widget Preview Frame
                                Text("XEM TRƯỚC GIAO DIỆN WIDGET:", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0x33000000))
                                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(20.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AppIconView(app = app, iconShape = iconShape, size = 48.dp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(app.displayLabel, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                Text("Widget 2.0 • Sẵn sàng tương tác", color = Color(0xFF38BDF8), fontSize = 11.sp)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF38BDF8))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Mở Ngay", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    onAppWidgetSelected(app.packageName, app.displayLabel, selectedAppSpan.first, selectedAppSpan.second)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Thêm Widget Này Vào Màn Hình", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    // TAB 1: Smart Stack & Tiện ích hệ thống đa năng
                    val systemOptions = listOf(
                        WidgetOptionData(
                            type = WidgetType.SMART_STACK,
                            title = "Smart Stack 2.0 (Smart Attack)",
                            description = "Hộp xoay đa năng: Vẽ tay, Note việc, Ảnh kỷ niệm, Video loop, Nhạc",
                            icon = Icons.Default.Layers,
                            color = Color(0xFF8B5CF6),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.CLOCK_DATE,
                            title = "Đồng hồ & Lịch Neon",
                            description = "Hiển thị giờ lớn, giây chạy trực tiếp, thứ ngày và báo thức",
                            icon = Icons.Default.Alarm,
                            color = Color(0xFF38BDF8),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.WEATHER,
                            title = "Thời tiết trực tiếp Live",
                            description = "Nhiệt độ thời gian thực, độ ẩm, chất lượng không khí AQI",
                            icon = Icons.Default.Cloud,
                            color = Color(0xFFFBBF24),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.SYSTEM_STATS,
                            title = "Tối ưu RAM & Pin Siêu tốc",
                            description = "Theo dõi pin, RAM đã dùng và nút 1 chạm dọn dẹp tối ưu máy",
                            icon = Icons.Default.Memory,
                            color = Color(0xFF10B981),
                            defaultSpanX = 4,
                            defaultSpanY = 1
                        ),
                        WidgetOptionData(
                            type = WidgetType.MUSIC_PLAYER,
                            title = "Trình phát nhạc Waveform",
                            description = "Hiệu ứng sóng âm thanh động, chuyển bài và tạm dừng nhạc",
                            icon = Icons.Default.MusicNote,
                            color = Color(0xFFA78BFA),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.QUICK_NOTES,
                            title = "Ghi chú nhanh",
                            description = "Ghi lại ý tưởng hoặc checklist công việc tức thì",
                            icon = Icons.Default.EditNote,
                            color = Color(0xFFF472B6),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.GOOGLE_SEARCH,
                            title = "Tìm kiếm Google & Lệnh AI",
                            description = "Thanh tìm kiếm Google neon tích hợp Voice và lệnh tắt",
                            icon = Icons.Default.Search,
                            color = Color(0xFF3B82F6),
                            defaultSpanX = 4,
                            defaultSpanY = 1
                        ),
                        WidgetOptionData(
                            type = WidgetType.QUICK_CONTROLS,
                            title = "Bảng điều khiển nhanh",
                            description = "Bật tắt Wi-Fi, Bluetooth, đèn pin chỉ với 1 chạm",
                            icon = Icons.Default.Tune,
                            color = Color(0xFF06B6D4),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.POMODORO,
                            title = "Đồng hồ Pomodoro 3.0",
                            description = "Đếm ngược 25p tập trung, đồng bộ lên Dynamic Island",
                            icon = Icons.Default.Timer,
                            color = Color(0xFFF43F5E),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.HABIT_TRACKER,
                            title = "Theo dõi Thói quen",
                            description = "Tích chọn uống nước, đọc sách, thể dục mỗi ngày",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF10B981),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.MINI_CALCULATOR,
                            title = "Máy tính bỏ túi Mini",
                            description = "Tính nhanh cộng trừ nhân chia ngay trên Home",
                            icon = Icons.Default.Calculate,
                            color = Color(0xFF38BDF8),
                            defaultSpanX = 2,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.COUNTDOWN,
                            title = "Đếm ngược Sự kiện",
                            description = "Đếm số ngày tới sinh nhật, kỳ thi, ngày lễ lớn",
                            icon = Icons.Default.Event,
                            color = Color(0xFFA855F7),
                            defaultSpanX = 2,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.QUOTE_CALENDAR,
                            title = "Lịch & Câu nói hay",
                            description = "Lịch ngày và câu trích dẫn truyền động lực mỗi ngày",
                            icon = Icons.Default.FormatQuote,
                            color = Color(0xFFFBBF24),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        ),
                        WidgetOptionData(
                            type = WidgetType.EXPENSE_TRACKER,
                            title = "Ghi chép Chi tiêu nhanh",
                            description = "Ghi nhanh +15k Cafe, +35k Ăn và xem tổng chi hôm nay",
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF22C55E),
                            defaultSpanX = 4,
                            defaultSpanY = 2
                        )
                    )

                    val filteredSystem = remember(systemOptions, searchQuery) {
                        if (searchQuery.isBlank()) systemOptions
                        else systemOptions.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.description.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSystem) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                                    .clickable {
                                        onWidgetSelected(item.type, item.defaultSpanX, item.defaultSpanY)
                                        onDismiss()
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(item.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.title,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x33FFFFFF))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${item.defaultSpanX}x${item.defaultSpanY}",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(item.color.copy(alpha = 0.25f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = item.color, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Thêm", color = item.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class WidgetOptionData(
    val type: WidgetType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val defaultSpanX: Int,
    val defaultSpanY: Int
)
