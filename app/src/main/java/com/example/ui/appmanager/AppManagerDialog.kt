package com.example.ui.appmanager

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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.AppCategory
import com.example.data.model.AppInfo
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView

@Composable
fun AppManagerDialog(
    isOpen: Boolean,
    apps: List<AppInfo>,
    iconShape: IconShape,
    onAppInfo: (String) -> Unit,
    onUninstall: (String) -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var sortMode by remember { mutableStateOf("SIZE") } // SIZE, USAGE, NAME

    val sortedApps = remember(apps, sortMode) {
        when (sortMode) {
            "SIZE" -> apps.sortedByDescending { it.estimatedSizeBytes }
            "USAGE" -> apps.sortedBy { it.usageCount } // least used first for cleaning
            else -> apps.sortedBy { it.displayLabel.lowercase() }
        }
    }

    // Duplicate functionality detector
    val duplicateWarnings = remember(apps) {
        val browsers = apps.filter { it.packageName.contains("chrome") || it.packageName.contains("browser") }
        val musics = apps.filter { it.packageName.contains("spotify") || it.packageName.contains("music") || it.packageName.contains("zing") }
        val list = mutableListOf<String>()
        if (browsers.size > 1) list.add("Phát hiện ${browsers.size} trình duyệt web (có thể tối ưu bớt 1 app)")
        if (musics.size > 1) list.add("Phát hiện ${musics.size} ứng dụng nghe nhạc trực tuyến")
        list
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFF0F172A)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Quản Lý Ứng Dụng 2.0", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Dọn app ít dùng & Cảnh báo trùng lặp", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }

            // Duplicate Functionality Warning Banner
            if (duplicateWarnings.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gợi ý dọn dẹp:", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    duplicateWarnings.forEach { warn ->
                        Text("• $warn", color = Color(0xFFFEF3C7), fontSize = 11.sp, modifier = Modifier.padding(start = 26.dp, top = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Sort Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("SIZE", "Dung lượng lớn"),
                    Pair("USAGE", "Ít dùng nhất"),
                    Pair("NAME", "Tên (A-Z)")
                ).forEach { (key, label) ->
                    val isSel = sortMode == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                            .clickable { sortMode = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(label, color = if (isSel) Color.Black else Color.White, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedApps) { app ->
                    val mb = (app.estimatedSizeBytes / (1024 * 1024)).coerceAtLeast(18)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconView(app = app, iconShape = iconShape, size = 44.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.displayLabel, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Dung lượng ~${mb} MB • Đã mở ${app.usageCount} lần", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }

                        Row {
                            IconButton(onClick = { onAppInfo(app.packageName) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Info, contentDescription = "Chi tiết", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onUninstall(app.packageName) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Gỡ cài đặt", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
