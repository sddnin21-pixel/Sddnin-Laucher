package com.example.ui.feed

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FeedScreen(
    isOpen: Boolean,
    apps: List<AppInfo>,
    iconShape: IconShape,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    onAppClick: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
    ) {
        val calendar = remember(isOpen) { Calendar.getInstance() }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..10 -> "Chào buổi sáng!"
            in 11..13 -> "Chào buổi trưa!"
            in 14..17 -> "Chào buổi chiều!"
            in 18..22 -> "Chào buổi tối!"
            else -> "Đêm muộn rồi, nghỉ ngơi thôi!"
        }

        val currentDateStr = remember(isOpen) {
            SimpleDateFormat("EEEE, dd 'tháng' MM", Locale("vi", "VN")).format(Date()).replaceFirstChar { it.uppercase() }
        }

        var waterGlasses by remember { mutableIntStateOf(5) }
        var isEyeRestDone by remember { mutableStateOf(false) }
        var isStretched by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xF20B0F19)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$currentDateStr • Feed AI & Smart Stack",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. SMART STACK 2.0 (Horizontal Rotating Cards)
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Smart Stack 2.0 (Vuốt chuyển thẻ)",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Tự động theo giờ", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            val stackPagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
                            HorizontalPager(
                                state = stackPagerState,
                                modifier = Modifier.fillMaxWidth().height(160.dp)
                            ) { page ->
                                when (page) {
                                    0 -> WeatherCard()
                                    1 -> AgendaCard()
                                    2 -> HealthHabitsCard(
                                        water = waterGlasses,
                                        onWaterChange = { waterGlasses = it },
                                        eyeDone = isEyeRestDone,
                                        onEyeToggle = { isEyeRestDone = !isEyeRestDone },
                                        stretchDone = isStretched,
                                        onStretchToggle = { isStretched = !isStretched }
                                    )
                                    3 -> QuoteCard()
                                }
                            }

                            // Smart Stack Dots Indicator
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(4) { idx ->
                                    val isCurrent = stackPagerState.currentPage == idx
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .size(if (isCurrent) 8.dp else 5.dp)
                                            .clip(CircleShape)
                                            .background(if (isCurrent) Color(0xFF38BDF8) else Color(0x55FFFFFF))
                                    )
                                }
                            }
                        }
                    }

                    // 2. SUGGESTED APPS BY CONTEXT (Gợi ý app ngữ cảnh)
                    item {
                        Column {
                            Text(
                                text = "Gợi ý thông minh cho bạn",
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            val topSuggestions = apps.take(5)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                items(topSuggestions) { app ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                onAppClick(app)
                                                onClose()
                                            }
                                            .padding(4.dp)
                                    ) {
                                        AppIconView(app = app, iconShape = iconShape, size = 48.dp, iconPackStyle = iconPackStyle)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = app.displayLabel,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. DAILY HABITS & WELLBEING (Thói quen & sức khỏe)
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x331E293B)),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Theo dõi thói quen hôm nay", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = Color(0xFF10B981))
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Uống đủ nước ($waterGlasses / 8 ly)", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { if (waterGlasses > 0) waterGlasses-- },
                                            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0x33FFFFFF))
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { if (waterGlasses < 12) waterGlasses++ },
                                            modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFF38BDF8))
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (waterGlasses.toFloat() / 8f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    // 4. QUICK STICKY NOTE (Ghi chú ghim trên Feed)
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x33F59E0B)),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0x44F59E0B), RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Ghi chú ghim • Đã đồng bộ", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "\"Thành công là tổng hòa của những nỗ lực nhỏ được lặp đi lặp lại ngày này qua ngày khác.\"",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0284C7))))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Thời tiết hiện tại", color = Color(0xFFBAE6FD), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("28°C", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("Hà Nội • Nắng nhẹ ít mây", color = Color(0xFFE0F2FE), fontSize = 12.sp)
                Text("Chất lượng không khí: AQI 32 (Tốt)", color = Color(0xFF86EFAC), fontSize = 11.sp)
            }
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFDE047), modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
private fun AgendaCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF4C1D95), Color(0xFF7C3AED))))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lịch hẹn tiếp theo", color = Color(0xFFDDD6FE), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFC4B5FD), modifier = Modifier.size(18.dp))
            }
            Column {
                Text("10:00 - 11:30", color = Color(0xFFFBCFE8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Họp nhóm tổng kết Sprint Launcher 2.0", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Google Meet • 5 người tham gia", color = Color(0xFFDDD6FE), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun HealthHabitsCard(
    water: Int,
    onWaterChange: (Int) -> Unit,
    eyeDone: Boolean,
    onEyeToggle: () -> Unit,
    stretchDone: Boolean,
    onStretchToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF059669))))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sức khỏe & Thói quen ngày", color = Color(0xFFA7F3D0), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Nước: $water/8 (+1)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable { onWaterChange((water + 1).coerceAtMost(12)) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (eyeDone) Color(0x3310B981) else Color(0x22000000))
                        .clickable { onEyeToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFA7F3D0), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nghỉ mắt 20s", color = Color.White, fontSize = 12.sp)
                    }
                    Text(
                        if (eyeDone) "✓ Đã xong" else "Chạm đánh dấu",
                        color = if (eyeDone) Color(0xFF6EE7B7) else Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (stretchDone) Color(0x3310B981) else Color(0x22000000))
                        .clickable { onStretchToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = Color(0xFFA7F3D0), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vận động giãn cơ", color = Color.White, fontSize = 12.sp)
                    }
                    Text(
                        if (stretchDone) "✓ Đã giãn cơ" else "Chạm đánh dấu",
                        color = if (stretchDone) Color(0xFF6EE7B7) else Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF701A75), Color(0xFF9D174D))))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cảm hứng hôm nay", color = Color(0xFFF5D0FE), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.FormatQuote, contentDescription = null, tint = Color(0xFFF5D0FE), modifier = Modifier.size(20.dp))
            }
            Text(
                "\"Sự đơn giản là tinh hoa của sự phức tạp đã được tinh chế.\"",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
            Text("— Steve Jobs", color = Color(0xFFF5D0FE), fontSize = 11.sp)
        }
    }
}
