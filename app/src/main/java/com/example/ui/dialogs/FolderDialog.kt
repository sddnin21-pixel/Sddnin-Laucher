package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppInfo
import com.example.data.model.HomeItem
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView
import com.example.ui.theme.AccentColorsList

@Composable
fun FolderDialog(
    folder: HomeItem.FolderItem,
    allApps: List<AppInfo>,
    iconShape: IconShape,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onUpdateFolder: (String, String, List<String>) -> Unit,
    onDeleteFolder: () -> Unit
) {
    var isRenaming by remember(folder.id) { mutableStateOf(false) }
    var folderName by remember(folder.id) { mutableStateOf(folder.name) }
    var selectedColorHex by remember(folder.id) { mutableStateOf(folder.colorHex) }
    var showAddAppsDialog by remember(folder.id) { mutableStateOf(false) }

    val folderApps = remember(folder.appPackages, allApps) {
        folder.appPackages.mapNotNull { pkg ->
            allApps.find { it.packageName == pkg }
        }
    }

    if (showAddAppsDialog) {
        val availableToAdd = allApps.filterNot { folder.appPackages.contains(it.packageName) }
        AlertDialog(
            onDismissRequest = { showAddAppsDialog = false },
            title = { Text("Thêm ứng dụng vào thư mục") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.size(width = 300.dp, height = 360.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableToAdd) { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val updated = folder.appPackages + app.packageName
                                    onUpdateFolder(folderName, selectedColorHex, updated)
                                    showAddAppsDialog = false
                                }
                                .padding(6.dp)
                        ) {
                            AppIconView(app = app, iconShape = iconShape, size = 44.dp, iconPackStyle = iconPackStyle)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = app.displayLabel,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddAppsDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Folder Name & Color Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRenaming) {
                        OutlinedTextField(
                            value = folderName,
                            onValueChange = { folderName = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            isRenaming = false
                            onUpdateFolder(folderName, selectedColorHex, folder.appPackages)
                        }) {
                            Text("Lưu", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isRenaming = true }
                        ) {
                            Text(
                                text = folderName,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Đổi tên",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { showAddAppsDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm app", tint = Color(0xFF34D399))
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color(0xFF94A3B8))
                        }
                    }
                }

                // Color accent bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Màu:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    AccentColorsList.forEach { (_, hex) ->
                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Blue }
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    selectedColorHex = hex
                                    onUpdateFolder(folderName, hex, folder.appPackages)
                                }
                                .border(
                                    if (isSelected) 2.dp else 0.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                        )
                    }
                }

                // Grid of apps in folder
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(folderApps) { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onAppClick(app)
                                    onDismiss()
                                }
                                .padding(4.dp)
                        ) {
                            Box {
                                AppIconView(app = app, iconShape = iconShape, size = 52.dp, iconPackStyle = iconPackStyle)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .clickable {
                                            val updated = folder.appPackages.filterNot { it == app.packageName }
                                            onUpdateFolder(folderName, selectedColorHex, updated)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Bỏ khỏi thư mục",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
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

                Spacer(modifier = Modifier.height(12.dp))
                // Delete folder button
                TextButton(
                    onClick = {
                        onDeleteFolder()
                        onDismiss()
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Giải tán thư mục", color = Color(0xFFEF4444))
                }
            }
        }
    }
}
