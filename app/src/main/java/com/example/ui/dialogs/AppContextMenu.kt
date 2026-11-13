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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppInfo
import com.example.ui.components.AppIconView

/**
 * Compact, sleek context menu matching the user's reference screenshots.
 * Designed with a small pill width (~240dp), top 4 horizontal quick actions
 * (Remove, Uninstall, Select, Folder), followed by compact vertical rows.
 */
@Composable
fun AppContextMenuDialog(
    app: AppInfo,
    allApps: List<AppInfo> = emptyList(),
    isInDock: Boolean = false,
    onDismiss: () -> Unit,
    onOpenApp: () -> Unit,
    onAddToHome: () -> Unit,
    onRemoveFromHome: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleHidden: () -> Unit,
    onRename: (String) -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onCreateFolderWith: ((String) -> Unit)? = null,
    onToggleDock: (() -> Unit)? = null
) {
    var showRenameDialog by remember(app.packageName) { mutableStateOf(false) }
    var renameText by remember(app.packageName) { mutableStateOf(app.displayLabel) }
    var showSelectOtherAppDialog by remember(app.packageName) { mutableStateOf(false) }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Đổi tên ứng dụng") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Tên hiển thị mới") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRename(renameText)
                    showRenameDialog = false
                    onDismiss()
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showSelectOtherAppDialog) {
        val candidates = remember(allApps, app) {
            allApps.filterNot { it.packageName == app.packageName }
        }
        AlertDialog(
            onDismissRequest = { showSelectOtherAppDialog = false },
            title = { Text("Chọn app để gộp thành thư mục") },
            text = {
                LazyColumn(modifier = Modifier.size(width = 280.dp, height = 300.dp)) {
                    items(candidates) { candidate ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onCreateFolderWith?.invoke(candidate.packageName)
                                    showSelectOtherAppDialog = false
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp, horizontal = 6.dp)
                        ) {
                            AppIconView(app = candidate, size = 32.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = candidate.displayLabel,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSelectOtherAppDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xF20F172A), // Deep dark glassmorphic card
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            shadowElevation = 12.dp,
            modifier = Modifier.width(248.dp) // Compact width matching user's screenshot
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: 4 Horizontal Quick Actions as shown in user screenshots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Remove
                    TopActionItem(
                        icon = Icons.Default.Close,
                        label = "Xóa",
                        tint = Color(0xFFF87171),
                        onClick = {
                            onRemoveFromHome()
                            onDismiss()
                        }
                    )

                    // 2. Uninstall
                    TopActionItem(
                        icon = Icons.Default.Delete,
                        label = "Gỡ c.đặt",
                        tint = Color(0xFFEF4444),
                        onClick = {
                            onUninstall()
                            onDismiss()
                        }
                    )

                    // 3. Select
                    TopActionItem(
                        icon = Icons.Default.Check,
                        label = "Chọn",
                        tint = Color(0xFF38BDF8),
                        onClick = {
                            onDismiss()
                        }
                    )

                    // 4. Folder / Merge
                    TopActionItem(
                        icon = Icons.Default.CreateNewFolder,
                        label = "Thư mục",
                        tint = Color(0xFFA78BFA),
                        onClick = {
                            showSelectOtherAppDialog = true
                        }
                    )
                }

                HorizontalDivider(
                    color = Color(0x22FFFFFF),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Compact List items with double-dash icon on right
                CompactListItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Mở ứng dụng",
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        onOpenApp()
                        onDismiss()
                    }
                )

                CompactListItem(
                    icon = Icons.Default.Folder,
                    title = "Gộp vào Thư mục...",
                    tint = Color(0xFFA78BFA),
                    onClick = {
                        showSelectOtherAppDialog = true
                    }
                )

                CompactListItem(
                    icon = Icons.Default.Edit,
                    title = "Đổi tên hiển thị",
                    tint = Color(0xFFFBBF24),
                    onClick = {
                        showRenameDialog = true
                    }
                )

                CompactListItem(
                    icon = if (app.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    title = if (app.isLocked) "Mở khóa PIN" else "Khóa bằng PIN",
                    tint = Color(0xFFF59E0B),
                    onClick = {
                        onToggleLock()
                        onDismiss()
                    }
                )

                if (onToggleDock != null) {
                    CompactListItem(
                        icon = Icons.Default.ViewSidebar,
                        title = if (isInDock) "Xóa khỏi Dock" else "Thêm vào Dock",
                        tint = Color(0xFF34D399),
                        onClick = {
                            onToggleDock()
                            onDismiss()
                        }
                    )
                }

                CompactListItem(
                    icon = Icons.Default.Add,
                    title = "Thêm vào MH chính",
                    tint = Color(0xFF38BDF8),
                    onClick = {
                        onAddToHome()
                        onDismiss()
                    }
                )

                CompactListItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Ẩn ứng dụng",
                    tint = Color(0xFF94A3B8),
                    onClick = {
                        onToggleHidden()
                        onDismiss()
                    }
                )

                CompactListItem(
                    icon = Icons.Default.Info,
                    title = "Thông tin chi tiết",
                    tint = Color(0xFF94A3B8),
                    onClick = {
                        onAppInfo()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun TopActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun CompactListItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                color = Color(0xFFE2E8F0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Double dash drag indicator as seen in screenshots
        Text(
            text = "═",
            color = Color(0x66FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
