package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.Brush
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.HomeItem
import com.example.data.model.LauncherPreferences
import com.example.data.model.TransitionEffect
import com.example.ui.DockItem
import com.example.ui.components.AppIconView
import com.example.ui.widgets.LauncherWidgetCard
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    preferences: LauncherPreferences,
    homeItems: List<HomeItem>,
    dockItems: List<DockItem>,
    dockApps: List<AppInfo>,
    allApps: List<AppInfo>,
    isEditMode: Boolean,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onFolderClick: (HomeItem.FolderItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFeed: () -> Unit,
    onOpenControls: () -> Unit,
    onToggleOneHanded: () -> Unit,
    onDoubleTap: () -> Unit,
    onSwipeDown: () -> Unit,
    onToggleEditMode: () -> Unit,
    onRemoveHomeItem: (String) -> Unit,
    onOpenAddWidget: () -> Unit,
    onOpenWallpaperCustomizer: () -> Unit,
    onMergeApps: (pkg1: String, pkg2: String, page: Int, row: Int, col: Int, id1: String, id2: String) -> Unit,
    onAddAppToFolder: (pkg: String, folderId: String, removeItemId: String) -> Unit,
    onOpenSmartStackEditor: () -> Unit = {},
    onResizeWidget: (id: String, spanX: Int, spanY: Int) -> Unit = { _, _, _ -> },
    onMoveHomeItem: (id: String, page: Int, row: Int, col: Int) -> Unit = { _, _, _, _ -> },
    onUpdateTaskNotes: (String) -> Unit = {},
    onUpdateDoodle: (String) -> Unit = {},
    batteryPercent: Int = 88,
    isCharging: Boolean = false,
    pomodoroRunning: Boolean = false,
    pomodoroSecondsLeft: Int = 25 * 60,
    onTogglePomodoro: () -> Unit = {},
    onResetPomodoro: () -> Unit = {},
    onToggleHabit: (Int) -> Unit = {},
    onAdjustCountdown: (Int) -> Unit = {},
    onAddExpense: (Long) -> Unit = {},
    onResetExpense: () -> Unit = {},
    onToggleMusic: () -> Unit = {},
    onOpenToolbox: () -> Unit = {},
    onOpenGestureDraw: () -> Unit = {},
    onOpenUsageInsights: () -> Unit = {},
    onOpenNightstand: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenShortcutBuilder: () -> Unit = {},
    onToggleZenMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pageCount = remember(homeItems) {
        val maxPage = homeItems.maxOfOrNull { it.pageIndex } ?: 0
        (maxPage + 2).coerceAtLeast(2)
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. Full-screen Wallpaper Renderer (Image/Sloth, Video, Gradient, Live Particles)
        WallpaperRenderer(
            preferences = preferences,
            modifier = Modifier.fillMaxSize()
        )

        // 1B. Ambient Touch Particles & Blue Light Eye Filter Overlay
        AmbientEffectsAndBlueFilterOverlay(
            effect = preferences.touchParticleEffect,
            blueLightIntensity = preferences.blueLightFilterIntensity,
            modifier = Modifier.fillMaxSize()
        )

        // Main Home Screen Container with Gestures
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onDoubleTap() },
                        onLongPress = { onToggleEditMode() }
                    )
                }
                .pointerInput(Unit) {
                    var totalDragY = 0f
                    var gestureFired = false
                    detectVerticalDragGestures(
                        onDragStart = {
                            totalDragY = 0f
                            gestureFired = false
                        },
                        onDragEnd = {
                            totalDragY = 0f
                            gestureFired = false
                        },
                        onDragCancel = {
                            totalDragY = 0f
                            gestureFired = false
                        }
                    ) { _, dragAmount ->
                        totalDragY += dragAmount
                        if (!gestureFired) {
                            if (totalDragY < -70f) {
                                gestureFired = true
                                onOpenDrawer()
                            } else if (totalDragY > 70f) {
                                gestureFired = true
                                onSwipeDown()
                            }
                        }
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Feature #1: Interactive Dynamic Island Pill at top
                if (preferences.dynamicIslandEnabled && !isEditMode) {
                    DynamicIslandPill(
                        preferences = preferences,
                        batteryPercent = batteryPercent,
                        isCharging = isCharging,
                        pomodoroRunning = pomodoroRunning,
                        pomodoroSecondsLeft = pomodoroSecondsLeft,
                        onTogglePomodoro = onTogglePomodoro,
                        onToggleMusic = onToggleMusic,
                        onOpenToolbox = onOpenToolbox,
                        onOpenNightstand = onOpenNightstand,
                        onToggleZenMode = onToggleZenMode,
                        onOpenNotifications = onOpenNotifications
                    )
                }

                // Edit Mode Header
                AnimatedVisibility(visible = isEditMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xE60F172A))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chỉnh sửa màn hình • Kéo 2 app để gộp thư mục",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            IconButton(onClick = onOpenWallpaperCustomizer) {
                                Icon(Icons.Default.Palette, contentDescription = "Hình nền", tint = Color(0xFFFBBF24))
                            }
                            IconButton(onClick = onOpenAddWidget) {
                                Icon(Icons.Default.Widgets, contentDescription = "Thêm Widget", tint = Color(0xFF38BDF8))
                            }
                            IconButton(onClick = onToggleEditMode) {
                                Icon(Icons.Default.Check, contentDescription = "Hoàn tất", tint = Color(0xFF10B981))
                            }
                        }
                    }
                }

                // Ambient Music Indicator Floating Pill (if playing music)
                if (preferences.backgroundMusicEnabled && !isEditMode) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x990F172A))
                            .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(16.dp))
                            .clickable { onOpenWallpaperCustomizer() }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nhạc nền: ${preferences.backgroundMusicPreset} • ${(preferences.backgroundMusicVolume * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!isEditMode) {
                    // Top Quick Bar (Feed AI, Search Bar, Quick Controls, Settings)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onOpenFeed,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = "Feed AI", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Color(0x331E293B))
                                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(26.dp))
                                .clickable { onOpenSearch() }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sddnin Launcher • Tìm kiếm & AI...",
                                    color = Color(0xCCFFFFFF),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onOpenControls,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Bảng điều khiển", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onOpenWallpaperCustomizer,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Đổi hình nền & nhạc", tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Feature #6: Minimalist Zen Mode Home View OR Full HorizontalPager Grid
                if (preferences.zenModeEnabled && !isEditMode) {
                    var zenNow by remember { mutableStateOf(Date()) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            zenNow = Date()
                            delay(15000L)
                        }
                    }
                    val zenTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(zenNow)
                    val zenDate = SimpleDateFormat("EEEE, dd 'tháng' MM", Locale("vi", "VN")).format(zenNow).replaceFirstChar { it.uppercase() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(zenTime, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.ExtraLight)
                        Text(zenDate, color = Color(0xFFCBD5E1), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\"Ít phân tâm hơn, nhiều bình yên hơn.\"",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(26.dp))
                        // 4 Essential Zen Apps
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            allApps.take(4).forEach { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onAppClick(app) }
                                        .padding(6.dp)
                                ) {
                                    AppIconView(
                                        app = app,
                                        iconShape = preferences.iconShape,
                                        size = 48.dp,
                                        showBadge = false,
                                        iconPackStyle = preferences.iconPackStyle
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(app.displayLabel, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x33FFFFFF))
                                .border(1.dp, Color(0x5538BDF8), RoundedCornerShape(20.dp))
                                .clickable { onToggleZenMode() }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Thoát chế độ Zen", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { page ->
                        val pageItems = remember(homeItems, page) {
                            homeItems.filter { it.pageIndex == page }
                        }

                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val modifierWithEffect = when (preferences.transitionEffect) {
                            TransitionEffect.CUBE -> Modifier.graphicsLayer {
                                rotationY = pageOffset * 45f
                                cameraDistance = 8f * density
                            }
                            TransitionEffect.ZOOM -> Modifier.graphicsLayer {
                                val scale = 1f - (pageOffset.absoluteValue * 0.15f).coerceIn(0f, 0.3f)
                                scaleX = scale
                                scaleY = scale
                            }
                            TransitionEffect.DEPTH -> Modifier.graphicsLayer {
                                val scale = 1f - (pageOffset.absoluteValue * 0.2f).coerceIn(0f, 0.5f)
                                scaleX = scale
                                scaleY = scale
                                alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            }
                            TransitionEffect.FADE -> Modifier.graphicsLayer {
                                alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            }
                            TransitionEffect.CARD_STACK -> Modifier.graphicsLayer {
                                val absOff = pageOffset.absoluteValue.coerceIn(0f, 1f)
                                val scale = 1f - absOff * 0.12f
                                scaleX = scale
                                scaleY = scale
                                translationX = if (pageOffset < 0f) -pageOffset * size.width * 0.45f else 0f
                                alpha = 1f - absOff * 0.4f
                            }
                            TransitionEffect.CAROUSEL -> Modifier.graphicsLayer {
                                val absOff = pageOffset.absoluteValue.coerceIn(0f, 1f)
                                rotationY = pageOffset * 28f
                                scaleX = 1f - absOff * 0.18f
                                scaleY = 1f - absOff * 0.18f
                                cameraDistance = 12f * density
                            }
                            TransitionEffect.WHEEL -> Modifier.graphicsLayer {
                                rotationZ = pageOffset * -14f
                                translationY = pageOffset.absoluteValue * 48f
                                alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 0.7f)
                            }
                            TransitionEffect.CLASSIC -> Modifier
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(modifierWithEffect)
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            HomePageGrid(
                                page = page,
                                items = pageItems,
                                allApps = allApps,
                                preferences = preferences,
                                isEditMode = isEditMode,
                                pomodoroRunning = pomodoroRunning,
                                pomodoroSecondsLeft = pomodoroSecondsLeft,
                                onTogglePomodoro = onTogglePomodoro,
                                onResetPomodoro = onResetPomodoro,
                                onToggleHabit = onToggleHabit,
                                onAdjustCountdown = onAdjustCountdown,
                                onAddExpense = onAddExpense,
                                onResetExpense = onResetExpense,
                                onAppClick = onAppClick,
                                onAppLongClick = onAppLongClick,
                                onFolderClick = onFolderClick,
                                onRemoveItem = onRemoveHomeItem,
                                onOpenSearch = onOpenSearch,
                                onOpenClock = onOpenNightstand,
                                onMergeApps = onMergeApps,
                                onAddAppToFolder = onAddAppToFolder,
                                onOpenSmartStackEditor = onOpenSmartStackEditor,
                                onResizeWidget = onResizeWidget,
                                onMoveHomeItem = onMoveHomeItem,
                                onUpdateTaskNotes = onUpdateTaskNotes,
                                onUpdateDoodle = onUpdateDoodle
                            )
                        }
                    }
                }

                // Page Indicator Dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pageCount) { idx ->
                        val isSelected = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 8.dp else 4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color(0x66FFFFFF))
                        )
                    }
                }

                // Quick Floating Action Row above Dock (Toolbox, Shortcut Builder, Wallpaper/Settings)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenShortcutBuilder,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLink,
                            contentDescription = "Tạo lối tắt & widget nhanh",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenToolbox,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Hộp công cụ 3.0",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Cài đặt Launcher",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Dock Bar (thanh dưới hàng) Supporting Apps, Folders & Neon Border
                val dockBorderBrush = if (preferences.dockNeonBorderEnabled) {
                    Brush.horizontalGradient(listOf(Color(0x8838BDF8), Color(0x88A855F7), Color(0x88F472B6)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0x22FFFFFF), Color(0x22FFFFFF)))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF0F172A).copy(alpha = preferences.dockAlpha))
                        .border(1.2.dp, dockBorderBrush, RoundedCornerShape(32.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dockItems.isNotEmpty()) {
                            dockItems.forEach { item ->
                                when (item) {
                                    is DockItem.AppItem -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .combinedClickable(
                                                    onClick = { onAppClick(item.app) },
                                                    onLongClick = { onAppLongClick(item.app) }
                                                )
                                                .padding(2.dp)
                                        ) {
                                            AppIconView(
                                                app = item.app,
                                                iconShape = preferences.iconShape,
                                                size = (48 * preferences.iconScale).dp,
                                                showBadge = preferences.showNotificationBadges,
                                                iconPackStyle = preferences.iconPackStyle
                                            )
                                        }
                                    }
                                    is DockItem.FolderItem -> {
                                        // Mini 2x2 Folder Icon in Bottom Dock (Matching Screenshot 1 & 3)
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { onFolderClick(item.folder) }
                                                .padding(2.dp)
                                        ) {
                                            FolderIconPreview(
                                                folder = item.folder,
                                                allApps = allApps,
                                                preferences = preferences
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Fallback to legacy dockApps
                            dockApps.forEach { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .combinedClickable(
                                            onClick = { onAppClick(app) },
                                            onLongClick = { onAppLongClick(app) }
                                        )
                                        .padding(2.dp)
                                ) {
                                    AppIconView(
                                        app = app,
                                        iconShape = preferences.iconShape,
                                        size = (48 * preferences.iconScale).dp,
                                        showBadge = preferences.showNotificationBadges,
                                        iconPackStyle = preferences.iconPackStyle
                                    )
                                }
                            }
                        }

                        // Swipe Up / Open Drawer Button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onOpenDrawer() }
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Ngăn kéo ứng dụng",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Feature #2: Floating Edge Sidebar Panel on right edge
            EdgeSidebarPanel(
                enabled = preferences.edgeSidebarEnabled && !isEditMode,
                favoriteApps = dockApps.ifEmpty { allApps.take(3) },
                iconShape = preferences.iconShape,
                iconPackStyle = preferences.iconPackStyle,
                zenModeActive = preferences.zenModeEnabled,
                onAppClick = onAppClick,
                onOpenToolbox = onOpenToolbox,
                onOpenGestureDraw = onOpenGestureDraw,
                onOpenUsageInsights = onOpenUsageInsights,
                onOpenNightstand = onOpenNightstand,
                onOpenAchievements = onOpenAchievements,
                onToggleZenMode = onToggleZenMode,
                onLockScreen = onDoubleTap,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 2x2 Mini Folder Preview as seen in user screenshots 1 and 3.
 */
@Composable
fun FolderIconPreview(
    folder: HomeItem.FolderItem,
    allApps: List<AppInfo>,
    preferences: LauncherPreferences,
    modifier: Modifier = Modifier
) {
    val folderColor = try {
        Color(android.graphics.Color.parseColor(folder.colorHex))
    } catch (e: Exception) {
        Color(0xFF3B82F6)
    }

    Box(
        modifier = modifier
            .size((48 * preferences.iconScale).dp)
            .clip(RoundedCornerShape(16.dp))
            .background(folderColor.copy(alpha = 0.35f))
            .border(1.5.dp, folderColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        val miniApps = folder.appPackages.take(4).mapNotNull { pkg ->
            allApps.find { it.packageName == pkg }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                miniApps.getOrNull(0)?.let { AppIconView(it, preferences.iconShape, size = 16.dp, showBadge = false, iconPackStyle = preferences.iconPackStyle) }
                miniApps.getOrNull(1)?.let { AppIconView(it, preferences.iconShape, size = 16.dp, showBadge = false, iconPackStyle = preferences.iconPackStyle) }
            }
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                miniApps.getOrNull(2)?.let { AppIconView(it, preferences.iconShape, size = 16.dp, showBadge = false, iconPackStyle = preferences.iconPackStyle) }
                miniApps.getOrNull(3)?.let { AppIconView(it, preferences.iconShape, size = 16.dp, showBadge = false, iconPackStyle = preferences.iconPackStyle) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomePageGrid(
    page: Int,
    items: List<HomeItem>,
    allApps: List<AppInfo>,
    preferences: LauncherPreferences,
    isEditMode: Boolean,
    pomodoroRunning: Boolean = false,
    pomodoroSecondsLeft: Int = 25 * 60,
    onTogglePomodoro: () -> Unit = {},
    onResetPomodoro: () -> Unit = {},
    onToggleHabit: (Int) -> Unit = {},
    onAdjustCountdown: (Int) -> Unit = {},
    onAddExpense: (Long) -> Unit = {},
    onResetExpense: () -> Unit = {},
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onFolderClick: (HomeItem.FolderItem) -> Unit,
    onRemoveItem: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenClock: () -> Unit = {},
    onMergeApps: (pkg1: String, pkg2: String, page: Int, row: Int, col: Int, id1: String, id2: String) -> Unit,
    onAddAppToFolder: (pkg: String, folderId: String, removeItemId: String) -> Unit,
    onOpenSmartStackEditor: () -> Unit = {},
    onResizeWidget: (id: String, spanX: Int, spanY: Int) -> Unit = { _, _, _ -> },
    onMoveHomeItem: (id: String, page: Int, row: Int, col: Int) -> Unit = { _, _, _, _ -> },
    onUpdateTaskNotes: (String) -> Unit = {},
    onUpdateDoodle: (String) -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalCols = preferences.gridColumns
        val totalRows = preferences.gridRows

        val cellWidth = maxWidth / totalCols
        val cellHeight = maxHeight / totalRows

        // Spacious Grid Guide Cells for drag & drop in Edit Mode
        if (isEditMode) {
            for (r in 0 until totalRows) {
                for (c in 0 until totalCols) {
                    Box(
                        modifier = Modifier
                            .offset(x = cellWidth * c, y = cellHeight * r)
                            .size(width = cellWidth, height = cellHeight)
                            .padding(2.dp)
                            .border(1.dp, Color(0x2638BDF8), RoundedCornerShape(14.dp))
                    )
                }
            }
        }

        // Drag & Drop State for moving apps or merging 2 apps into a folder
        var draggingAppItem by remember { mutableStateOf<HomeItem.AppItem?>(null) }
        var dragOffset by remember { mutableStateOf(Offset.Zero) }
        var currentDragPos by remember { mutableStateOf<Offset?>(null) }
        var hoveredTargetItem by remember { mutableStateOf<HomeItem?>(null) }
        var hoveredTargetCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

        items.forEach { item ->
            val isHoveredTarget = hoveredTargetItem?.id == item.id

            when (item) {
                is HomeItem.AppItem -> {
                    val app = allApps.find { it.packageName == item.packageName }
                    if (app != null) {
                        Box(
                            modifier = Modifier
                                .offset(x = cellWidth * item.col, y = cellHeight * item.row)
                                .size(width = cellWidth, height = cellHeight)
                                .then(
                                    if (isHoveredTarget) {
                                        Modifier
                                            .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                                            .background(Color(0x330284C7), RoundedCornerShape(16.dp))
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .combinedClickable(
                                        onClick = { onAppClick(app) },
                                        onLongClick = { onAppLongClick(app) }
                                    )
                                    .pointerInput(item.id, items, totalCols, totalRows, cellWidth, cellHeight, isEditMode) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { startOffset ->
                                                draggingAppItem = item
                                                dragOffset = Offset.Zero
                                                val cellLeft = (cellWidth * item.col).toPx()
                                                val cellTop = (cellHeight * item.row).toPx()
                                                currentDragPos = Offset(cellLeft + startOffset.x, cellTop + startOffset.y)
                                                hoveredTargetCell = item.row to item.col
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffset += dragAmount
                                                currentDragPos = currentDragPos?.plus(dragAmount)

                                                // Detect which cell finger is over
                                                currentDragPos?.let { pos ->
                                                    val cellW = cellWidth.toPx()
                                                    val cellH = cellHeight.toPx()
                                                    val targetCol = (pos.x / cellW).toInt().coerceIn(0, totalCols - 1)
                                                    val targetRow = (pos.y / cellH).toInt().coerceIn(0, totalRows - 1)
                                                    hoveredTargetCell = targetRow to targetCol

                                                    val target = items.find { it.id != item.id && it.row == targetRow && it.col == targetCol }
                                                    hoveredTargetItem = target
                                                }
                                            },
                                            onDragEnd = {
                                                val target = hoveredTargetItem
                                                val dragged = draggingAppItem
                                                val cell = hoveredTargetCell
                                                if (dragged != null && target != null) {
                                                    when (target) {
                                                        is HomeItem.AppItem -> {
                                                            // Gộp 2 app thành 1 thư mục!
                                                            onMergeApps(
                                                                dragged.packageName,
                                                                target.packageName,
                                                                page,
                                                                target.row,
                                                                target.col,
                                                                dragged.id,
                                                                target.id
                                                            )
                                                        }
                                                        is HomeItem.FolderItem -> {
                                                            // Thêm app vào thư mục có sẵn
                                                            onAddAppToFolder(dragged.packageName, target.id, dragged.id)
                                                        }
                                                        else -> {}
                                                    }
                                                } else if (dragged != null && cell != null && isEditMode) {
                                                    val (tRow, tCol) = cell
                                                    val occupied = items.any { other ->
                                                        other.id != dragged.id && when (other) {
                                                            is HomeItem.WidgetItem -> tRow in other.row until (other.row + other.spanY) && tCol in other.col until (other.col + other.spanX)
                                                            else -> other.row == tRow && other.col == tCol
                                                        }
                                                    }
                                                    if (!occupied && (tRow != dragged.row || tCol != dragged.col)) {
                                                        onMoveHomeItem(dragged.id, page, tRow, tCol)
                                                    }
                                                }
                                                draggingAppItem = null
                                                dragOffset = Offset.Zero
                                                currentDragPos = null
                                                hoveredTargetItem = null
                                                hoveredTargetCell = null
                                            },
                                            onDragCancel = {
                                                draggingAppItem = null
                                                dragOffset = Offset.Zero
                                                currentDragPos = null
                                                hoveredTargetItem = null
                                                hoveredTargetCell = null
                                            }
                                        )
                                    }
                                    .padding(2.dp)
                            ) {
                                AppIconView(
                                    app = app,
                                    iconShape = preferences.iconShape,
                                    size = (48 * preferences.iconScale).dp,
                                    showBadge = preferences.showNotificationBadges,
                                    iconPackStyle = preferences.iconPackStyle
                                )
                                if (preferences.showLabels) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = app.displayLabel,
                                        color = Color.White,
                                        fontSize = preferences.labelSizeSp.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(cellWidth - 4.dp)
                                    )
                                }
                            }

                            // If another app is dragged over this app, show "Gộp thư mục" hint
                            if (isHoveredTarget) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = (-4).dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xE60284C7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Gộp thư mục", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (isEditMode) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .clickable { onRemoveItem(item.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                is HomeItem.FolderItem -> {
                    Box(
                        modifier = Modifier
                            .offset(x = cellWidth * item.col, y = cellHeight * item.row)
                            .size(width = cellWidth, height = cellHeight)
                            .then(
                                if (isHoveredTarget) {
                                    Modifier
                                        .border(2.dp, Color(0xFFA78BFA), RoundedCornerShape(16.dp))
                                        .background(Color(0x338B5CF6), RoundedCornerShape(16.dp))
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFolderClick(item) }
                                .padding(2.dp)
                        ) {
                            FolderIconPreview(
                                folder = item,
                                allApps = allApps,
                                preferences = preferences
                            )

                            if (preferences.showLabels) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    color = Color.White,
                                    fontSize = preferences.labelSizeSp.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(cellWidth - 4.dp)
                                )
                            }
                        }

                        if (isHoveredTarget) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-4).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xE68B5CF6))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("+ Thêm vào", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (isEditMode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                                    .clickable { onRemoveItem(item.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Xóa",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                is HomeItem.WidgetItem -> {
                    val clampedSpanX = item.spanX.coerceAtMost(totalCols - item.col)
                    val clampedSpanY = item.spanY.coerceAtMost(totalRows - item.row)
                    val wWidth = cellWidth * clampedSpanX
                    val wHeight = cellHeight * clampedSpanY

                    Box(
                        modifier = Modifier
                            .offset(x = cellWidth * item.col, y = cellHeight * item.row)
                            .size(width = wWidth, height = wHeight)
                            .padding(4.dp)
                            .then(
                                if (isEditMode) {
                                    Modifier
                                        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(24.dp))
                                        .pointerInput(item.id) {
                                            detectDragGesturesAfterLongPress(
                                                onDrag = { change, _ -> change.consume() },
                                                onDragEnd = {
                                                    // Drop handled
                                                }
                                            )
                                        }
                                } else Modifier
                            )
                    ) {
                        LauncherWidgetCard(
                            widget = item,
                            preferences = preferences,
                            allApps = allApps,
                            pomodoroRunning = pomodoroRunning,
                            pomodoroSecondsLeft = pomodoroSecondsLeft,
                            onTogglePomodoro = onTogglePomodoro,
                            onResetPomodoro = onResetPomodoro,
                            onToggleHabit = onToggleHabit,
                            onAdjustCountdown = onAdjustCountdown,
                            onAddExpense = onAddExpense,
                            onResetExpense = onResetExpense,
                            onSearchClick = onOpenSearch,
                            onOpenClock = onOpenClock,
                            onAppClick = onAppClick,
                            onOpenSmartStackEditor = onOpenSmartStackEditor,
                            onUpdateTaskNotes = onUpdateTaskNotes,
                            onUpdateDoodle = onUpdateDoodle
                        )

                        if (isEditMode) {
                            // Top-Left: Widget Resize Pill (2x2, 4x2, 4x1)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = 6.dp, y = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xE638BDF8))
                                    .clickable {
                                        val (nextX, nextY) = when {
                                            item.spanX == 4 && item.spanY == 2 -> Pair(2, 2)
                                            item.spanX == 2 && item.spanY == 2 -> Pair(4, 1)
                                            item.spanX == 4 && item.spanY == 1 -> Pair(4, 2)
                                            else -> Pair(4, 2)
                                        }
                                        onResizeWidget(item.id, nextX, nextY)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${item.spanX}x${item.spanY} ⟳",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Top-Right: Delete Widget Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                                    .clickable { onRemoveItem(item.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Xóa Widget",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating drag preview that follows touch during drag
        draggingAppItem?.let { draggedItem ->
            val app = allApps.find { it.packageName == draggedItem.packageName }
            currentDragPos?.let { pos ->
                Box(
                    modifier = Modifier
                        .offset { IntOffset(pos.x.roundToInt() - 30, pos.y.roundToInt() - 30) }
                        .size(60.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xCC0F172A))
                        .border(2.dp, Color(0xFF38BDF8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (app != null) {
                        AppIconView(app, preferences.iconShape, size = 44.dp, iconPackStyle = preferences.iconPackStyle)
                    }
                }
            }
        }
    }
}
