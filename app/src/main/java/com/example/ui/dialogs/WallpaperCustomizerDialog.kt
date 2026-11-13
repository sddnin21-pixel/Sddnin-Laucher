package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LauncherPreferences
import com.example.ui.home.LiveAnimatedWallpaper
import com.example.ui.theme.WallpaperPresets

@Composable
fun WallpaperCustomizerDialog(
    isOpen: Boolean,
    preferences: LauncherPreferences,
    onDismiss: () -> Unit,
    onSelectPresetImage: (String) -> Unit,
    onSelectCustomMedia: (Uri, Boolean) -> Unit, // uri, isVideo
    onSelectGradient: (Int) -> Unit,
    onToggleBackgroundMusic: (Boolean) -> Unit,
    onSelectMusicPreset: (String) -> Unit,
    onUpdateMusicVolume: (Float) -> Unit,
    onToggleLiveAnimation: (Boolean) -> Unit,
    onToggleVideoSound: (Boolean) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Android Zero-Permission Photo/Video Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            onSelectCustomMedia(it, false)
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            onSelectCustomMedia(it, true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xF70B1120),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(640.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tùy Chỉnh Hình Nền & Nhạc",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ảnh, Video lặp, Gradient và Nhạc nền thư giãn",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
                    }
                }

                // Mini Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when (preferences.wallpaperType) {
                        "CUSTOM_IMAGE" -> {
                            if (!preferences.customWallpaperUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = preferences.customWallpaperUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.wallpaper_sloth),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        "VIDEO" -> {
                            LiveAnimatedWallpaper(modifier = Modifier.fillMaxSize())
                        }
                        "GRADIENT" -> {
                            val idx = preferences.wallpaperIndex.coerceIn(0, WallpaperPresets.size - 1)
                            Box(modifier = Modifier.fillMaxSize().background(WallpaperPresets[idx].second))
                        }
                        else -> { // PRESET_IMAGE
                            Image(
                                painter = painterResource(id = R.drawable.wallpaper_sloth),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Mock Clock & Status on Preview
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0x33000000))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Text("20:34 • 26°C", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Đang áp dụng hình nền này", color = Color(0xCCFFFFFF), fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF38BDF8)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Ảnh", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Video", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Màu", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Gradient, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Có nhạc", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                // Tab Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        // TAB 0: ẢNH (IMAGES)
                        0 -> {
                            item {
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Chọn ảnh từ Bộ nhớ máy (Gallery)...", fontSize = 13.sp)
                                }
                            }

                            item {
                                Text(
                                    text = "Hình ảnh mẫu có sẵn:",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            item {
                                // Preset 1: Sloth Sanctuary (matching screenshot)
                                ImagePresetCard(
                                    title = "Chú lười bình yên (Sloth)",
                                    subtitle = "Hình nền chú lười bám cành cây xanh thư thái",
                                    isSelected = preferences.wallpaperType == "PRESET_IMAGE" && preferences.presetImageId == "sloth",
                                    onClick = { onSelectPresetImage("sloth") }
                                )
                            }

                            item {
                                ImagePresetCard(
                                    title = "Rừng Ngọc Lục Bảo",
                                    subtitle = "Cây cối và thiên nhiên mát mẻ dịu mắt",
                                    isSelected = preferences.wallpaperType == "PRESET_IMAGE" && preferences.presetImageId == "nature",
                                    onClick = { onSelectPresetImage("nature") }
                                )
                            }

                            item {
                                ImagePresetCard(
                                    title = "Thành phố Cyberpunk",
                                    subtitle = "Ánh sáng neon tím và xanh đậm chất tương lai",
                                    isSelected = preferences.wallpaperType == "PRESET_IMAGE" && preferences.presetImageId == "cyber",
                                    onClick = { onSelectPresetImage("cyber") }
                                )
                            }

                            item {
                                ImagePresetCard(
                                    title = "Hoàng Hôn Núi Non",
                                    subtitle = "Ráng chiều ấm áp và huyền ảo",
                                    isSelected = preferences.wallpaperType == "PRESET_IMAGE" && preferences.presetImageId == "sunset",
                                    onClick = { onSelectPresetImage("sunset") }
                                )
                            }
                        }

                        // TAB 1: VIDEO & ĐỘNG
                        1 -> {
                            item {
                                Button(
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Chọn video từ máy làm hình nền động...", fontSize = 13.sp)
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x331E293B))
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Âm thanh từ Video", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Bật tiếng của video khi chạy làm hình nền", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.videoSoundEnabled,
                                        onCheckedChange = { onToggleVideoSound(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF8B5CF6))
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = "Hình nền động cực quang / Vũ trụ:",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            item {
                                ImagePresetCard(
                                    title = "Cực Quang Aurora Phát Sáng",
                                    subtitle = "Dòng chảy ánh sáng dạ quang chuyển động liên tục",
                                    isSelected = preferences.wallpaperType == "VIDEO" && preferences.customWallpaperUri == null,
                                    onClick = {
                                        onSelectCustomMedia(Uri.EMPTY, true)
                                    }
                                )
                            }
                        }

                        // TAB 2: MÀU GRADIENT
                        2 -> {
                            items(WallpaperPresets.indices.toList()) { idx ->
                                val preset = WallpaperPresets[idx]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x331E293B))
                                        .clickable { onSelectGradient(idx) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(preset.second)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(preset.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Gradient đa sắc độ sâu", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    if (preferences.wallpaperType == "GRADIENT" && preferences.wallpaperIndex == idx) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
                                    }
                                }
                            }
                        }

                        // TAB 3: CÓ NHẠC (BACKGROUND MUSIC)
                        3 -> {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x331E293B))
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Phát nhạc nền MH chính", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("Âm thanh thư giãn lofi, tiếng mưa nhẹ nhàng", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.backgroundMusicEnabled,
                                        onCheckedChange = { onToggleBackgroundMusic(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                                    )
                                }
                            }

                            if (preferences.backgroundMusicEnabled) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0x331E293B))
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Âm lượng nhạc:", color = Color.White, fontSize = 13.sp)
                                            Text("${(preferences.backgroundMusicVolume * 100).toInt()}%", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = preferences.backgroundMusicVolume,
                                            onValueChange = { onUpdateMusicVolume(it) },
                                            valueRange = 0f..1f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFF38BDF8),
                                                activeTrackColor = Color(0xFF0284C7)
                                            )
                                        )
                                    }
                                }

                                item {
                                    Text("Giai điệu nhạc nền:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                val presets = listOf(
                                    Pair("LOFI", Pair("Lofi Chill Beats", "Hợp âm lofi êm dịu, ấm áp như quán cà phê")),
                                    Pair("RAIN", Pair("Mưa Rừng Nhiệt Đới", "Tiếng mưa rơi nhẹ nhàng trên lá rừng thư thái")),
                                    Pair("COSMIC", Pair("Âm Hưởng Không Gian Vũ Trụ", "Âm thanh drone huyền ảo và thiền định sâu")),
                                    Pair("PIANO", Pair("Piano Hoàng Hôn", "Từng phím piano mộc du dương và bình yên"))
                                )

                                items(presets) { (code, info) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (preferences.backgroundMusicPreset == code) Color(0x550284C7) else Color(0x331E293B)
                                            )
                                            .clickable { onSelectMusicPreset(code) }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF38BDF8))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(info.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(info.second, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                        }
                                        if (preferences.backgroundMusicPreset == code) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                                        }
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x331E293B))
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Hiệu ứng Đom đóm bay", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Các đốm sáng lơ lửng nhẹ nhàng trên hình nền", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.liveAnimationEnabled,
                                        onCheckedChange = { onToggleLiveAnimation(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFBBF24))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePresetCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0x550284C7) else Color(0x331E293B))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF38BDF8))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
        }
    }
}
