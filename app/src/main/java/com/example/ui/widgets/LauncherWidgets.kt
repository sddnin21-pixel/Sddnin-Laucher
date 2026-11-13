package com.example.ui.widgets

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppInfo
import com.example.data.model.HomeItem
import com.example.data.model.LauncherPreferences
import com.example.data.model.WidgetType
import com.example.ui.components.AppIconView
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LauncherWidgetCard(
    widget: HomeItem.WidgetItem,
    preferences: LauncherPreferences,
    allApps: List<AppInfo> = emptyList(),
    pomodoroRunning: Boolean = false,
    pomodoroSecondsLeft: Int = 25 * 60,
    onTogglePomodoro: () -> Unit = {},
    onResetPomodoro: () -> Unit = {},
    onToggleHabit: (Int) -> Unit = {},
    onAdjustCountdown: (Int) -> Unit = {},
    onAddExpense: (Long) -> Unit = {},
    onResetExpense: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onOpenClock: () -> Unit = {},
    onAppClick: (AppInfo) -> Unit = {},
    onOpenSmartStackEditor: () -> Unit = {},
    onUpdateTaskNotes: (String) -> Unit = {},
    onUpdateDoodle: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x331E293B))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
            .padding(12.dp)
    ) {
        when (widget.type) {
            WidgetType.CLOCK_DATE -> ClockDateWidget(onOpenClock = onOpenClock)
            WidgetType.WEATHER -> WeatherWidget()
            WidgetType.SYSTEM_STATS -> SystemStatsWidget()
            WidgetType.MUSIC_PLAYER -> MusicPlayerWidget()
            WidgetType.QUICK_NOTES -> QuickNotesWidget()
            WidgetType.GOOGLE_SEARCH -> GoogleSearchWidget(onSearchClick = onSearchClick)
            WidgetType.SMART_STACK -> SmartStackHomeWidget(
                preferences = preferences,
                onOpenEditor = onOpenSmartStackEditor,
                onUpdateTasks = onUpdateTaskNotes,
                onUpdateDoodle = onUpdateDoodle
            )
            WidgetType.QUICK_CONTROLS -> QuickControlsHomeWidget()
            WidgetType.APP_GLANCE -> AppGlanceWidget(
                widget = widget,
                allApps = allApps,
                preferences = preferences,
                onAppClick = onAppClick
            )
            WidgetType.POMODORO -> PomodoroWidget(
                isRunning = pomodoroRunning,
                secondsLeft = pomodoroSecondsLeft,
                onToggle = onTogglePomodoro,
                onReset = onResetPomodoro
            )
            WidgetType.COUNTDOWN -> CountdownWidget(
                title = preferences.countdownTitle,
                daysLeft = preferences.countdownDaysLeft,
                onAdjustDays = onAdjustCountdown
            )
            WidgetType.HABIT_TRACKER -> HabitTrackerWidget(
                habitStates = preferences.habitStates,
                onToggleHabit = onToggleHabit
            )
            WidgetType.MINI_CALCULATOR -> MiniCalculatorWidget()
            WidgetType.QUOTE_CALENDAR -> QuoteCalendarWidget()
            WidgetType.EXPENSE_TRACKER -> ExpenseTrackerWidget(
                totalToday = preferences.dailyExpenseTotal,
                onAddExpense = onAddExpense,
                onResetExpense = onResetExpense
            )
        }
    }
}

/**
 * App Glance Widget: Can be added for ANY app installed on device without restriction!
 */
@Composable
fun AppGlanceWidget(
    widget: HomeItem.WidgetItem,
    allApps: List<AppInfo>,
    preferences: LauncherPreferences,
    onAppClick: (AppInfo) -> Unit
) {
    val app = allApps.find { it.packageName == widget.appPackage }
    val title = widget.customTitle ?: app?.displayLabel ?: "Ứng dụng"

    if (app == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Launch, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Nhấn để mở", color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onAppClick(app) },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconView(
                    app = app,
                    iconShape = preferences.iconShape,
                    size = 36.dp,
                    showBadge = preferences.showNotificationBadges,
                    iconPackStyle = preferences.iconPackStyle
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = app.displayLabel,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Widget 2.0 • Sẵn sàng",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3338BDF8))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text("Mở", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Quick interactive buttons row inside app glance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable { onAppClick(app) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Launch, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Truy cập", color = Color.White, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable { onAppClick(app) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thông báo", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Smart Stack 2.0 ("Smart Attack"):
 * - Handwritten drawing canvas ("ghi chú tay")
 * - Task notes with checkboxes ("note công việc của bạn")
 * - Photo memory card ("hình ảnh")
 * - Video memory card ("video")
 * - Live Weather
 * - Music Player
 * - System Stats
 * - Can edit, add, or remove widgets in the stack ("chỉnh sửa bỏ widget trong đó")
 */
@Composable
fun SmartStackHomeWidget(
    preferences: LauncherPreferences,
    onOpenEditor: () -> Unit = {},
    onUpdateTasks: (String) -> Unit = {},
    onUpdateDoodle: (String) -> Unit = {}
) {
    val activeCards = preferences.smartStackCards.ifEmpty {
        listOf("HANDWRITING", "TASKS", "PHOTO", "VIDEO", "WEATHER", "MUSIC")
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val safeIndex = currentIndex.coerceIn(0, (activeCards.size - 1).coerceAtLeast(0))
    val currentCardId = activeCards.getOrElse(safeIndex) { "TASKS" }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Active Card Content
        when (currentCardId) {
            "HANDWRITING" -> HandwritingCard(onDoodleChanged = onUpdateDoodle)
            "TASKS" -> TaskNotesCard(
                savedNotes = preferences.smartStackTaskNotes,
                onNotesChanged = onUpdateTasks
            )
            "PHOTO" -> PhotoMemoryCard(photoUri = preferences.smartStackPhotoUri)
            "VIDEO" -> VideoMemoryCard(videoUri = preferences.smartStackVideoUri)
            "WEATHER" -> WeatherWidget()
            "MUSIC" -> MusicPlayerWidget()
            "SYSTEM" -> SystemStatsWidget()
            else -> TaskNotesCard(savedNotes = preferences.smartStackTaskNotes, onNotesChanged = onUpdateTasks)
        }

        // Top-Right Controls: Stack Navigation Dots + "Sửa Smart Stack" gear button
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Next card mini button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0x55000000))
                    .clickable {
                        currentIndex = (safeIndex + 1) % activeCards.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Thẻ kế tiếp",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }

            // Edit / Customize Smart Stack ("chỉnh sửa bỏ widget")
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0x5538BDF8))
                    .clickable { onOpenEditor() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Chỉnh sửa Smart Stack",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Bottom-Center Dots Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            activeCards.forEachIndexed { idx, _ ->
                val isSel = idx == safeIndex
                Box(
                    modifier = Modifier
                        .size(if (isSel) 6.dp else 4.dp)
                        .clip(CircleShape)
                        .background(if (isSel) Color(0xFF38BDF8) else Color(0x66FFFFFF))
                        .clickable { currentIndex = idx }
                )
            }
        }
    }
}

/**
 * 1. Ghi chú tay (Handwritten drawing memo canvas)
 */
data class DrawPath(val path: Path, val color: Color, val strokeWidth: Float = 6f)

@Composable
fun HandwritingCard(
    onDoodleChanged: (String) -> Unit = {}
) {
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var redrawTick by remember { mutableIntStateOf(0) }
    var selectedColor by remember { mutableStateOf(Color(0xFF38BDF8)) }

    val colors = listOf(
        Color(0xFF38BDF8), // Cyan
        Color(0xFFFBBF24), // Yellow
        Color(0xFFF43F5E), // Rose Pink
        Color(0xFF10B981), // Emerald
        Color.White
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Draw, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ghi chú tay (Vẽ trực tiếp)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (selectedColor == c) 2.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = c }
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable {
                            paths.clear()
                            currentPath = null
                            redrawTick++
                            onDoodleChanged("")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Xóa bảng vẽ", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Drawing Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x440F172A))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                .pointerInput(selectedColor) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val p = Path().apply { moveTo(offset.x, offset.y) }
                            currentPath = p
                            paths.add(DrawPath(p, selectedColor))
                            redrawTick++
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPath?.lineTo(change.position.x, change.position.y)
                            redrawTick++
                        },
                        onDragEnd = {
                            currentPath = null
                            redrawTick++
                            onDoodleChanged("has_doodle_${paths.size}")
                        },
                        onDragCancel = {
                            currentPath = null
                            redrawTick++
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Read redrawTick so in-place Path mutations trigger immediate Canvas redraws
                val tick = redrawTick
                if (tick >= 0) {
                    paths.forEach { drawItem ->
                        drawPath(
                            path = drawItem.path,
                            color = drawItem.color,
                            style = Stroke(
                                width = drawItem.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            if (paths.isEmpty()) {
                Text(
                    text = "Dùng ngón tay vẽ hoặc ghi chú nhanh tại đây...",
                    color = Color(0x66FFFFFF),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * 2. Note công việc của bạn (Task / Todo Checklist)
 */
data class TaskItem(val id: Int, val text: String, var isDone: Boolean = false)

@Composable
fun TaskNotesCard(
    savedNotes: String,
    onNotesChanged: (String) -> Unit
) {
    val initialList = remember(savedNotes) {
        val parts = savedNotes.split("|").filter { it.isNotBlank() }
        if (parts.isNotEmpty()) {
            parts.mapIndexed { idx, str ->
                val isDone = str.startsWith("[X]")
                val cleanText = if (isDone) str.removePrefix("[X]") else str
                TaskItem(idx, cleanText, isDone)
            }.toMutableList()
        } else {
            mutableListOf(
                TaskItem(0, "Hoàn thành dự án Launcher", true),
                TaskItem(1, "Tập thể dục 30 phút", false),
                TaskItem(2, "Đọc sách buổi tối", false)
            )
        }
    }

    val taskList = remember { mutableStateListOf<TaskItem>().apply { addAll(initialList) } }
    LaunchedEffect(initialList) {
        val currentEncoded = taskList.joinToString("|") { if (it.isDone) "[X]${it.text}" else it.text }
        val nextEncoded = initialList.joinToString("|") { if (it.isDone) "[X]${it.text}" else it.text }
        if (currentEncoded != nextEncoded) {
            taskList.clear()
            taskList.addAll(initialList)
        }
    }
    var newTaskText by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    fun syncNotes() {
        val encoded = taskList.joinToString("|") {
            if (it.isDone) "[X]${it.text}" else it.text
        }
        onNotesChanged(encoded)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Note Công Việc", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                val doneCount = taskList.count { it.isDone }
                Text("$doneCount/${taskList.size}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3310B981))
                    .clickable { isAdding = !isAdding }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                    Text("Thêm", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isAdding) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    placeholder = { Text("Việc mới...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981))
                        .clickable {
                            if (newTaskText.isNotBlank()) {
                                taskList.add(TaskItem(taskList.size + 1, newTaskText.trim(), false))
                                newTaskText = ""
                                isAdding = false
                                syncNotes()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(taskList) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.isDone) Color(0x1A10B981) else Color(0x22FFFFFF))
                        .clickable {
                            taskList[index] = item.copy(isDone = !item.isDone)
                            syncNotes()
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.isDone) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (item.isDone) Color(0xFF10B981) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.text,
                        color = if (item.isDone) Color(0xFF94A3B8) else Color.White,
                        fontSize = 11.sp,
                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            taskList.removeAt(index)
                            syncNotes()
                        },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color(0x66FFFFFF), modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

/**
 * 3. Hình ảnh (Photo Memory Card)
 */
@Composable
fun PhotoMemoryCard(photoUri: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (!photoUri.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(photoUri),
                contentDescription = "Hình ảnh kỷ niệm",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Default sloth photo
            Image(
                painter = painterResource(id = R.drawable.wallpaper_sloth),
                contentDescription = "Ảnh chú lười bình yên",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay gradient & caption
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA0F172A))
                    )
                )
                .padding(8.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Khoảnh khắc bình yên",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 4. Video (Video Memory Card)
 */
@Composable
fun VideoMemoryCard(videoUri: String?) {
    var isMuted by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        if (!videoUri.isNullOrBlank()) {
            var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        tag = videoUri
                        setVideoURI(Uri.parse(videoUri))
                        setOnErrorListener { _, _, _ -> true }
                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            mp.isLooping = true
                            if (isMuted) mp.setVolume(0f, 0f) else mp.setVolume(1f, 1f)
                            start()
                        }
                    }
                },
                update = { view ->
                    if (view.tag != videoUri) {
                        view.tag = videoUri
                        view.setVideoURI(Uri.parse(videoUri))
                        view.start()
                    }
                    try {
                        val vol = if (isMuted) 0f else 1f
                        mediaPlayerRef?.setVolume(vol, vol)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Animated Cyberpunk/Ambient Video Graphic
            val transition = rememberInfiniteTransition(label = "video_synth")
            val pulse by transition.animateFloat(
                initialValue = 0.2f, targetValue = 0.8f,
                animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
                label = "v_pulse"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFA855F7).copy(alpha = pulse),
                                Color(0xFF0F172A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Video Memory Loop", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Chạm nút sửa để chọn video", color = Color(0xFFCBD5E1), fontSize = 9.sp)
                }
            }
        }

        // Sound toggle badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0x66000000))
                .clickable { isMuted = !isMuted },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/**
 * Clock & Date Widget
 */
@Composable
fun ClockDateWidget(onOpenClock: () -> Unit) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val secFormat = SimpleDateFormat("ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, dd 'tháng' MM", Locale("vi", "VN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onOpenClock() },
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = timeFormat.format(currentTime),
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = secFormat.format(currentTime),
                color = Color(0xFF38BDF8),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Text(
            text = dateFormat.format(currentTime).replaceFirstChar { it.uppercase() },
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                tint = Color(0xFFA78BFA),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Báo thức: 06:30 • Cuộc hẹn 09:00",
                color = Color(0xFFA78BFA),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Weather Widget
 */
@Composable
fun WeatherWidget() {
    var isSunny by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { isSunny = !isSunny },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Hà Nội",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isSunny) "Nắng nhẹ • Ít mây" else "Mát mẻ • Gió nhẹ",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isSunny) "31° / 24° • Độ ẩm 65%" else "27° / 22° • AQI 35 Tốt",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isSunny) Icons.Default.WbSunny else Icons.Default.Cloud,
                contentDescription = null,
                tint = if (isSunny) Color(0xFFFBBF24) else Color(0xFF38BDF8),
                modifier = Modifier.size(38.dp)
            )
            Text(
                text = if (isSunny) "29°C" else "26°C",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * System Stats Widget
 */
@Composable
fun SystemStatsWidget() {
    var isCleaned by remember { mutableStateOf(false) }
    var ramUsed by remember { mutableStateOf(4.2f) }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text("Pin: 88%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Đang sạc nhanh", color = Color(0xFF6EE7B7), fontSize = 10.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text("RAM: ${String.format(Locale.US, "%.1f", ramUsed)} / 8 GB", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(if (isCleaned) "Đã tối ưu mượt" else "Trống 48%", color = Color(0xFF93C5FD), fontSize = 10.sp)
            }
        }

        IconButton(
            onClick = {
                ramUsed = 2.8f
                isCleaned = true
            },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        ) {
            Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = "Tối ưu RAM",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/**
 * Music Player Widget
 */
@Composable
fun MusicPlayerWidget() {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSong by remember { mutableStateOf("Nấu Ăn Cho Em") }
    var artist by remember { mutableStateOf("Đen Vâu ft. PiaLinh") }

    val infiniteTransition = rememberInfiniteTransition(label = "music_bar")
    val barHeight1 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "b1"
    )
    val barHeight2 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "b2"
    )
    val barHeight3 by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 28f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "b3"
    )

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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFA78BFA).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = currentSong,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = artist,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            if (isPlaying) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(modifier = Modifier.width(3.dp).height(barHeight1.dp).background(Color(0xFF38BDF8), RoundedCornerShape(2.dp)))
                    Box(modifier = Modifier.width(3.dp).height(barHeight2.dp).background(Color(0xFFA78BFA), RoundedCornerShape(2.dp)))
                    Box(modifier = Modifier.width(3.dp).height(barHeight3.dp).background(Color(0xFFF472B6), RoundedCornerShape(2.dp)))
                }
            }
        }

        LinearProgressIndicator(
            progress = { 0.42f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF38BDF8),
            trackColor = Color(0x33FFFFFF)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentSong = "Waiting For You"; artist = "MONO" },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.FastRewind, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }

            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38BDF8))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { currentSong = "Cắt Đôi Nỗi Sầu"; artist = "Tăng Duy Tân" },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.FastForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/**
 * Quick Notes Widget
 */
@Composable
fun QuickNotesWidget() {
    var noteText by remember { mutableStateOf("• Họp nhóm 10h sáng\n• Mua cafe & sách\n• Hoàn thành launcher") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ghi chú nhanh", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(15.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier.fillMaxSize(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, lineHeight = 15.sp)
        )
    }
}

/**
 * Google Search Widget
 */
@Composable
fun GoogleSearchWidget(onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0x33FFFFFF))
            .clickable { onSearchClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "G",
            color = Color(0xFF4285F4),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Tìm kiếm hoặc nhập lệnh...",
            color = Color(0xB3FFFFFF),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice",
            tint = Color(0xFFEA4335),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xFF34A853),
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Quick Controls Widget
 */
@Composable
fun QuickControlsHomeWidget() {
    var isWifi by remember { mutableStateOf(true) }
    var isBt by remember { mutableStateOf(true) }
    var isTorch by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { isWifi = !isWifi }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isWifi) Color(0xFF38BDF8) else Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = if (isWifi) Color.Black else Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("Wi-Fi", color = Color.White, fontSize = 10.sp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { isBt = !isBt }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isBt) Color(0xFF8B5CF6) else Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = if (isBt) Color.White else Color.Gray, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("Bluetooth", color = Color.White, fontSize = 10.sp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { isTorch = !isTorch }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isTorch) Color(0xFFFBBF24) else Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = if (isTorch) Color.Black else Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("Đèn pin", color = Color.White, fontSize = 10.sp)
        }
    }
}
