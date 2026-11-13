package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppInfo
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView

@Composable
fun FocusModeDialog(
    isActive: Boolean,
    allowedPackages: Set<String>,
    allApps: List<AppInfo>,
    iconShape: IconShape,
    onToggleActive: () -> Unit,
    onUpdateAllowed: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Chế độ tập trung",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isActive) "Đang bật (Chỉ app được chọn)" else "Đang tắt",
                                color = if (isActive) Color(0xFF34D399) else Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF8B5CF6)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chọn các ứng dụng được phép mở khi đang tập trung:",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allApps) { app ->
                        val isAllowed = allowedPackages.contains(app.packageName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAllowed) Color(0x228B5CF6) else Color(0xFF1E293B))
                                .clickable {
                                    val updated = allowedPackages.toMutableSet()
                                    if (isAllowed) updated.remove(app.packageName) else updated.add(app.packageName)
                                    onUpdateAllowed(updated)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIconView(app = app, iconShape = iconShape, size = 36.dp, showBadge = false)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = app.displayLabel,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Checkbox(
                                checked = isAllowed,
                                onCheckedChange = { checked ->
                                    val updated = allowedPackages.toMutableSet()
                                    if (checked) updated.add(app.packageName) else updated.remove(app.packageName)
                                    onUpdateAllowed(updated)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF8B5CF6),
                                    uncheckedColor = Color(0xFF64748B)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}
