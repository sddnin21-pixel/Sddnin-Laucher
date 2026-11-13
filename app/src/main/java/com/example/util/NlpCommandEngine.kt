package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import com.example.data.model.AppInfo

sealed interface NlpAction {
    data class LaunchApp(val packageName: String) : NlpAction
    data class OpenSettings(val action: String) : NlpAction
    data class SetAlarm(val hour: Int, val minute: Int, val message: String) : NlpAction
    data class Translate(val text: String, val targetLang: String) : NlpAction
    data class LauncherFeature(val featureCode: String) : NlpAction
    data class WebSearch(val query: String) : NlpAction
}

data class NlpCommandResult(
    val query: String,
    val title: String,
    val description: String,
    val action: NlpAction,
    val iconName: String = "auto_awesome"
)

object NlpCommandEngine {

    fun parseCommand(input: String, apps: List<AppInfo>, aliases: Map<String, String>): NlpCommandResult? {
        val raw = input.trim()
        if (raw.isBlank()) return null
        val lower = VietnameseUnaccent.removeAccent(raw)

        // 1. Quick Aliases: ms -> Messenger, yt -> YouTube, etc.
        val aliasPkg = aliases[raw.lowercase()]
        if (aliasPkg != null) {
            val targetApp = apps.find { it.packageName == aliasPkg || it.packageName.contains(raw.lowercase()) }
            val label = targetApp?.displayLabel ?: aliasPkg.substringAfterLast(".")
            return NlpCommandResult(
                query = raw,
                title = "Mở nhanh: $label",
                description = "Lệnh tắt alias '$raw'",
                action = NlpAction.LaunchApp(aliasPkg),
                iconName = "bolt"
            )
        }

        // 2. Wi-Fi & Bluetooth & Settings
        if (lower.contains("bat wifi") || lower.contains("mo wifi") || lower.contains("tat wifi") || lower == "wifi") {
            return NlpCommandResult(
                query = raw,
                title = "Cài đặt Wi-Fi",
                description = "Mở bảng điều khiển kết nối mạng & Wi-Fi",
                action = NlpAction.OpenSettings(Settings.ACTION_WIFI_SETTINGS),
                iconName = "wifi"
            )
        }
        if (lower.contains("bat bluetooth") || lower.contains("mo bluetooth") || lower.contains("tat bluetooth") || lower == "bluetooth") {
            return NlpCommandResult(
                query = raw,
                title = "Cài đặt Bluetooth",
                description = "Mở bảng quản lý thiết bị Bluetooth",
                action = NlpAction.OpenSettings(Settings.ACTION_BLUETOOTH_SETTINGS),
                iconName = "bluetooth"
            )
        }
        if (lower.contains("am thanh") || lower.contains("chuong") || lower.contains("am luong")) {
            return NlpCommandResult(
                query = raw,
                title = "Cài đặt Âm thanh",
                description = "Điều chỉnh âm lượng, chuông báo",
                action = NlpAction.OpenSettings(Settings.ACTION_SOUND_SETTINGS),
                iconName = "volume_up"
            )
        }
        if (lower.contains("man hinh") || lower.contains("do sang")) {
            return NlpCommandResult(
                query = raw,
                title = "Cài đặt Màn hình",
                description = "Độ sáng, thời gian sáng màn hình",
                action = NlpAction.OpenSettings(Settings.ACTION_DISPLAY_SETTINGS),
                iconName = "brightness_6"
            )
        }

        // 3. Alarms and Clock: "dat hen 5 gio", "bao thuc 6h", "bao thuc 6:30"
        if (lower.contains("bao thuc") || lower.contains("dat hen") || lower.contains("hen gio")) {
            val timeRegex = Regex("""(\d{1,2})(?:h|:|\s*gio\s*)(\d{0,2})""")
            val match = timeRegex.find(lower)
            if (match != null) {
                val hour = (match.groupValues[1].toIntOrNull() ?: 7).coerceIn(0, 23)
                val minute = (match.groupValues[2].takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0).coerceIn(0, 59)
                val formattedTime = String.format("%02d:%02d", hour, minute)
                return NlpCommandResult(
                    query = raw,
                    title = "Đặt báo thức lúc $formattedTime",
                    description = "Tự động thiết lập báo thức trong ứng dụng Đồng hồ",
                    action = NlpAction.SetAlarm(hour, minute, "Báo thức Sddnin Launcher"),
                    iconName = "alarm"
                )
            }
        }

        // 4. Launcher Features & Modes
        if (lower.contains("che do choi game") || lower.contains("tang toc game") || lower.contains("game mode")) {
            return NlpCommandResult(
                query = raw,
                title = "Kích hoạt Chế độ chơi game",
                description = "Tối ưu hóa RAM, chặn thông báo xao nhãng",
                action = NlpAction.LauncherFeature("GAME_BOOST"),
                iconName = "sports_esports"
            )
        }
        if (lower.contains("tiet kiem pin") || lower.contains("pin yeu") || lower.contains("amoled")) {
            return NlpCommandResult(
                query = raw,
                title = "Bật Chế độ siêu tiết kiệm pin",
                description = "Nền đen AMOLED, giảm hiệu ứng chuyển động",
                action = NlpAction.LauncherFeature("BATTERY_SAVER"),
                iconName = "battery_saver"
            )
        }
        if (lower.contains("mot tay") || lower.contains("one hand")) {
            return NlpCommandResult(
                query = raw,
                title = "Bật Chế độ một tay",
                description = "Thu gọn giao diện xuống nửa dưới màn hình",
                action = NlpAction.LauncherFeature("ONE_HANDED"),
                iconName = "swipe_down"
            )
        }
        if (lower.contains("tap trung") || lower.contains("focus mode")) {
            return NlpCommandResult(
                query = raw,
                title = "Bật/Tắt Chế độ tập trung",
                description = "Chỉ mở các ứng dụng học tập/làm việc cần thiết",
                action = NlpAction.LauncherFeature("FOCUS_MODE"),
                iconName = "center_focus_strong"
            )
        }
        if (lower.contains("khoa man hinh") || lower.contains("tat man hinh") || lower.contains("ngu")) {
            return NlpCommandResult(
                query = raw,
                title = "Khóa màn hình (Ambient Display)",
                description = "Màn hình đen tiết kiệm điện kèm đồng hồ",
                action = NlpAction.LauncherFeature("LOCK_SCREEN"),
                iconName = "lock"
            )
        }

        // 5. Quick Translation: "dich [text] sang [lang]"
        if (lower.startsWith("dich ") || lower.contains(" sang tieng ")) {
            val targetLang = when {
                lower.contains("tieng anh") || lower.contains("english") -> "Tiếng Anh"
                lower.contains("tieng nhat") || lower.contains("japanese") -> "Tiếng Nhật"
                lower.contains("tieng trung") || lower.contains("chinese") -> "Tiếng Trung"
                lower.contains("tieng han") || lower.contains("korean") -> "Tiếng Hàn"
                else -> "Tiếng Anh"
            }
            val textToTranslate = raw.substringAfter("dich ").substringBefore(" sang ")
            return NlpCommandResult(
                query = raw,
                title = "Dịch nhanh sang $targetLang",
                description = "\"$textToTranslate\"",
                action = NlpAction.Translate(textToTranslate, targetLang),
                iconName = "translate"
            )
        }

        // 6. Direct App Launch: "mo [app name]", "open [app name]", or "khoi dong [app name]"
        if (lower.startsWith("mo ") || lower.startsWith("open ") || lower.startsWith("khoi dong ")) {
            val appQuery = when {
                lower.startsWith("mo ") -> lower.removePrefix("mo ")
                lower.startsWith("open ") -> lower.removePrefix("open ")
                lower.startsWith("khoi dong ") -> lower.removePrefix("khoi dong ")
                else -> lower
            }.trim()
            if (appQuery.isNotBlank()) {
                val matched = apps.find { app ->
                    VietnameseUnaccent.matchesFuzzy(app.displayLabel, appQuery) ||
                            app.packageName.contains(appQuery, ignoreCase = true)
                }
                if (matched != null) {
                    return NlpCommandResult(
                        query = raw,
                        title = "Mở ứng dụng: ${matched.displayLabel}",
                        description = "Khởi chạy ứng dụng ngay lập tức",
                        action = NlpAction.LaunchApp(matched.packageName),
                        iconName = "launch"
                    )
                }
            }
        }

        return null
    }

    fun executeAction(context: Context, action: NlpAction, onFeature: (String) -> Unit) {
        when (action) {
            is NlpAction.LaunchApp -> {
                try {
                    val pm = context.packageManager
                    val intent = pm.getLaunchIntentForPackage(action.packageName)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                } catch (_: Exception) {}
            }
            is NlpAction.OpenSettings -> {
                try {
                    val intent = Intent(action.action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (_: Exception) {
                    try {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } catch (_: Exception) {}
                }
            }
            is NlpAction.SetAlarm -> {
                try {
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(AlarmClock.EXTRA_HOUR, action.hour)
                        putExtra(AlarmClock.EXTRA_MINUTES, action.minute)
                        putExtra(AlarmClock.EXTRA_MESSAGE, action.message)
                        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    try {
                        context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } catch (_: Exception) {}
                }
            }
            is NlpAction.Translate -> {
                val tl = when (action.targetLang) {
                    "Tiếng Nhật" -> "ja"
                    "Tiếng Trung" -> "zh-CN"
                    "Tiếng Hàn" -> "ko"
                    else -> "en"
                }
                val url = "https://translate.google.com/?sl=auto&tl=$tl&text=${Uri.encode(action.text)}&op=translate"
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
            is NlpAction.LauncherFeature -> {
                onFeature(action.featureCode)
            }
            is NlpAction.WebSearch -> {
                val url = "https://www.google.com/search?q=${Uri.encode(action.query)}"
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: Exception) {}
            }
        }
    }
}
