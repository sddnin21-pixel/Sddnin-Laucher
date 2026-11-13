package com.example.ui.vault

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.IconShape
import com.example.data.model.LauncherPreferences
import com.example.data.model.UserProfile
import com.example.data.model.VaultAccessLog
import com.example.ui.components.AppIconView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EncryptedVaultScreen(
    isOpen: Boolean,
    preferences: LauncherPreferences,
    allApps: List<AppInfo>,
    accessLogs: List<VaultAccessLog>,
    onSelectProfile: (UserProfile) -> Unit,
    onToggleDynamicPrivacy: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var selectedTab by remember { mutableIntStateOf(0) }

    val vaultApps = remember(allApps, preferences.hiddenPackages, preferences.lockedPackages) {
        allApps.filter { preferences.hiddenPackages.contains(it.packageName) || preferences.lockedPackages.contains(it.packageName) }
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
                        Text("Két Mã Hóa & Riêng Tư Động", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Bảo vệ ứng dụng & Chuyển đổi hồ sơ", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF38BDF8)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Kho ứng dụng", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Hồ sơ & Bảo vệ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Nhật ký", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // VAULT APPS GRID
                    if (vaultApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Chưa có app nào trong két mã hóa", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                Text("Nhấn giữ icon bất kỳ trên màn hình để thêm vào két", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(vaultApps) { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onAppClick(app) }
                                        .padding(4.dp)
                                ) {
                                    AppIconView(app = app, iconShape = preferences.iconShape, size = 52.dp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = app.displayLabel,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // USER PROFILES & DYNAMIC PRIVACY
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Hồ sơ người dùng (Đa không gian):", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        items(UserProfile.entries.toTypedArray()) { profile ->
                            val isSelected = preferences.currentProfile == profile
                            val icon = when (profile) {
                                UserProfile.PERSONAL -> Icons.Default.Person
                                UserProfile.WORK -> Icons.Default.Work
                                UserProfile.KIDS -> Icons.Default.ChildCare
                                UserProfile.GUEST -> Icons.Default.Shield
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0xFF0284C7).copy(alpha = 0.35f) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF38BDF8) else Color(0x22FFFFFF),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onSelectProfile(profile) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color(0x22FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color.Black else Color.White, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(profile.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(profile.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                                if (isSelected) {
                                    Text("Đang dùng", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Riêng tư động (Dynamic Privacy):", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Tự ẩn app nhạy cảm khi pin yếu (< 20%)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Bảo vệ dữ liệu cá nhân khi cho người khác mượn máy lúc cạn pin", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = preferences.dynamicPrivacyLowBattery,
                                        onCheckedChange = { onToggleDynamicPrivacy() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF38BDF8)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // ACCESS AUDIT LOGS
                    if (accessLogs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Chưa có lịch sử mở app bị khóa.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    } else {
                        val timeFormat = SimpleDateFormat("HH:mm:ss • dd/MM", Locale.getDefault())
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(accessLogs) { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(log.appName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(timeFormat.format(Date(log.timestamp)), color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (log.success) Color(0x3310B981) else Color(0x33EF4444))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            if (log.success) "Mở thành công" else "Sai mã PIN",
                                            color = if (log.success) Color(0xFF34D399) else Color(0xFFEF4444),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
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
}
