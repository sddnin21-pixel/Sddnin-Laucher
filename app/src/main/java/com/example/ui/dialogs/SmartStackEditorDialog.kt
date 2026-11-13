package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LauncherPreferences

data class SmartCardMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

val ALL_SMART_CARDS = listOf(
    SmartCardMeta("HANDWRITING", "Ghi chú tay (Vẽ tay)", "Vẽ phác thảo, ký tên, ghi chú trực tiếp", Icons.Default.Draw, Color(0xFFF43F5E)),
    SmartCardMeta("TASKS", "Note công việc & Checklist", "Danh sách việc cần làm có nút tích chọn", Icons.Default.FormatListBulleted, Color(0xFF10B981)),
    SmartCardMeta("PHOTO", "Hình ảnh kỷ niệm", "Hiển thị ảnh đẹp tùy chọn từ bộ sưu tập", Icons.Default.Image, Color(0xFF38BDF8)),
    SmartCardMeta("VIDEO", "Video clip kỷ niệm", "Phát video ngắn hoặc ảnh động vòng lặp", Icons.Default.Movie, Color(0xFFA855F7)),
    SmartCardMeta("WEATHER", "Thời tiết trực tiếp", "Nhiệt độ, dự báo thời tiết real-time", Icons.Default.Cloud, Color(0xFFFBBF24)),
    SmartCardMeta("MUSIC", "Trình phát nhạc", "Điều khiển bài hát & hiệu ứng sóng nhạc", Icons.Default.MusicNote, Color(0xFFEC4899)),
    SmartCardMeta("SYSTEM", "Tối ưu RAM & Pin", "Hiển thị bộ nhớ, pin và nút dọn dẹp", Icons.Default.Memory, Color(0xFF06B6D4))
)

@Composable
fun SmartStackEditorDialog(
    preferences: LauncherPreferences,
    onDismiss: () -> Unit,
    onUpdateCards: (List<String>) -> Unit,
    onPickPhoto: (Uri) -> Unit,
    onPickVideo: (Uri) -> Unit
) {
    val context = LocalContext.current
    var currentCards by remember(preferences.smartStackCards) { mutableStateOf(preferences.smartStackCards.toMutableList()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            onPickPhoto(uri)
            if (!currentCards.contains("PHOTO")) {
                currentCards = (currentCards + "PHOTO").toMutableList()
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            onPickVideo(uri)
            if (!currentCards.contains("VIDEO")) {
                currentCards = (currentCards + "VIDEO").toMutableList()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 640.dp)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tùy Chỉnh Smart Stack 2.0",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bật/tắt, thêm hoặc bỏ widget trong hộp xoay",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Media Pickers shortcuts row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chọn Ảnh thẻ", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA855F7))
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chọn Video thẻ", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "CÁC THẺ TRONG SMART STACK (${currentCards.size} thẻ)",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card Toggle List
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ALL_SMART_CARDS) { card ->
                        val isEnabled = currentCards.contains(card.id)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isEnabled) Color(0xFF1E293B) else Color(0xFF141E33))
                                .border(
                                    width = 1.dp,
                                    color = if (isEnabled) card.color.copy(alpha = 0.5f) else Color(0x1AFFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (isEnabled) {
                                        if (currentCards.size > 1) {
                                            currentCards = currentCards.filter { it != card.id }.toMutableList()
                                        }
                                    } else {
                                        currentCards = (currentCards + card.id).toMutableList()
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(card.color.copy(alpha = if (isEnabled) 0.25f else 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = card.icon,
                                    contentDescription = null,
                                    tint = if (isEnabled) card.color else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.title,
                                    color = if (isEnabled) Color.White else Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = card.subtitle,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }

                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        if (!currentCards.contains(card.id)) {
                                            currentCards = (currentCards + card.id).toMutableList()
                                        }
                                    } else {
                                        if (currentCards.size > 1) {
                                            currentCards = currentCards.filter { it != card.id }.toMutableList()
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = card.color,
                                    checkedTrackColor = card.color.copy(alpha = 0.35f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onUpdateCards(currentCards)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lưu Thay Đổi", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
