package com.example.ui.search

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView
import com.example.util.CalcResult
import com.example.util.MathEvaluator
import com.example.util.NlpCommandEngine
import com.example.util.NlpCommandResult
import com.example.util.VietnameseUnaccent

data class SettingShortcut(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val action: String
)

@Composable
fun GlobalSearchOverlay(
    isOpen: Boolean,
    query: String,
    apps: List<AppInfo>,
    secretPin: String,
    hiddenPackages: Set<String>,
    aliases: Map<String, String> = emptyMap(),
    iconShape: IconShape,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    onQueryChange: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onOpenHiddenVault: () -> Unit,
    onExecuteFeature: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        val calcResult: CalcResult? = remember(query) {
            MathEvaluator.evaluate(query)
        }

        // NLP Command Evaluation
        val nlpResult: NlpCommandResult? = remember(query, apps, aliases) {
            NlpCommandEngine.parseCommand(query, apps, aliases)
        }

        // Check if query is secret PIN to unlock vault
        val isSecretPinMatch = remember(query, secretPin) {
            val q = query.trim()
            q == secretPin || q == "##$secretPin" || q == "**$secretPin"
        }

        val matchingApps = remember(query, apps, hiddenPackages) {
            if (query.isBlank()) {
                apps.sortedByDescending { it.usageCount }.take(6)
            } else {
                apps.filter { app ->
                    !hiddenPackages.contains(app.packageName) &&
                            (VietnameseUnaccent.matchesFuzzy(app.displayLabel, query) ||
                                    app.packageName.contains(query, ignoreCase = true))
                }.take(8)
            }
        }

        val settingsShortcuts = listOf(
            SettingShortcut("Cài đặt Wifi", "Mạng & Internet", Icons.Default.Wifi, Settings.ACTION_WIFI_SETTINGS),
            SettingShortcut("Bluetooth", "Thiết bị đã kết nối", Icons.Default.Bluetooth, Settings.ACTION_BLUETOOTH_SETTINGS),
            SettingShortcut("Pin & Năng lượng", "Tiết kiệm pin, tình trạng pin", Icons.Default.BatteryFull, Settings.ACTION_BATTERY_SAVER_SETTINGS),
            SettingShortcut("Âm thanh & Rung", "Âm lượng, chuông báo", Icons.Default.VolumeUp, Settings.ACTION_SOUND_SETTINGS),
            SettingShortcut("Tất cả Cài đặt", "Cài đặt tổng quan thiết bị", Icons.Default.Settings, Settings.ACTION_SETTINGS)
        )

        val matchingSettings = remember(query) {
            if (query.isBlank()) emptyList()
            else settingsShortcuts.filter {
                VietnameseUnaccent.matchesFuzzy(it.title, query) ||
                        VietnameseUnaccent.matchesFuzzy(it.description, query)
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xF20F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                // Top Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Ra lệnh (bật wifi, báo thức 6h, ms, tính toán)...",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Xóa",
                                            tint = Color(0xFFCBD5E1),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    try {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói câu lệnh cho Sddnin Assistant...")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        onQueryChange("bật wifi")
                                    }
                                }) {
                                    Icon(Icons.Default.Mic, contentDescription = "Giọng nói", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0x331E293B),
                            unfocusedContainerColor = Color(0x331E293B)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Secret PIN Match Unlock Vault Card
                    if (isSecretPinMatch) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.25f))
                                    .clickable {
                                        onOpenHiddenVault()
                                        onClose()
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B5CF6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Mở Kho Ứng Dụng Ẩn",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Mã bí mật chính xác! Nhấn để truy cập danh sách app ẩn.",
                                        color = Color(0xFFC4B5FD),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // NLP Assistant Command Execution Card
                    if (nlpResult != null) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0284C7).copy(alpha = 0.3f))
                                    .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                                    .clickable {
                                        NlpCommandEngine.executeAction(context, nlpResult.action) {
                                            onExecuteFeature(it)
                                        }
                                        onClose()
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = nlpResult.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = nlpResult.description,
                                        color = Color(0xFFBAE6FD),
                                        fontSize = 11.sp
                                    )
                                }
                                Icon(Icons.Default.PlayArrow, contentDescription = "Chạy", tint = Color(0xFF38BDF8))
                            }
                        }
                    }

                    // Instant Math / Unit Converter Card
                    if (calcResult != null) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = calcResult.type,
                                        color = Color(0xFF7DD3FC),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = calcResult.result,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Matching Apps Header
                    if (matchingApps.isNotEmpty()) {
                        item {
                            Text(
                                text = if (query.isBlank()) "Gợi ý ứng dụng thường dùng" else "Ứng dụng phù hợp",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(matchingApps) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        onAppClick(app)
                                        onClose()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIconView(app = app, iconShape = iconShape, size = 44.dp, iconPackStyle = iconPackStyle)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = app.displayLabel,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = app.category.title,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Settings Shortcuts
                    if (matchingSettings.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Lối tắt cài đặt",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        items(matchingSettings) { shortcut ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        try {
                                            context.startActivity(
                                                Intent(shortcut.action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                            onClose()
                                        } catch (e: Exception) {
                                            try {
                                                context.startActivity(
                                                    Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                            } catch (_: Exception) {}
                                            onClose()
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x33FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = shortcut.icon,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(shortcut.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text(shortcut.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Web Search Card
                    if (query.isNotBlank()) {
                        item {
                            HorizontalDivider(
                                color = Color(0x22FFFFFF),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        try {
                                            val webIntent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(webIntent)
                                            onClose()
                                        } catch (e: Exception) {
                                            // Ignore
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Tìm kiếm trên Web cho: \"$query\"",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
