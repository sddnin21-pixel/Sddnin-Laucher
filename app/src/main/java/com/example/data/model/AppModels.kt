package com.example.data.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

enum class AppCategory(val title: String, val iconName: String) {
    ALL("Tất cả", "apps"),
    SOCIAL("Mạng xã hội", "chat"),
    WORK("Công việc", "work"),
    ENTERTAINMENT("Giải trí", "movie"),
    TOOLS("Tiện ích", "build"),
    GAMES("Trò chơi", "sports_esports")
}

enum class IconShape(val title: String) {
    ROUNDED_SQUARE("Bo tròn"),
    CIRCLE("Hình tròn"),
    SQUIRCLE("Squircle"),
    TEARDROP("Giọt nước"),
    HEXAGON("Lục giác")
}

enum class TransitionEffect(val title: String) {
    CLASSIC("Cổ điển"),
    CUBE("Lập phương 3D"),
    DEPTH("Chiều sâu"),
    ZOOM("Phóng to thu nhỏ"),
    FADE("Mờ dần"),
    CARD_STACK("Xếp chồng thẻ"),
    CAROUSEL("Băng chuyền 3D"),
    WHEEL("Bánh xe nghiêng")
}

enum class IconPackStyle(val title: String, val subtitle: String) {
    DEFAULT("Mặc định", "Biểu tượng gốc sắc nét"),
    NEON_GLOW("Viền Neon", "Tỏa sáng viền xanh cyan hiện đại"),
    PASTEL_RING("Pastel Êm dịu", "Khung viền màu pastel tinh tế"),
    GLASS_FROST("Kính mờ 3D", "Hiệu ứng thủy tinh trong suốt"),
    CYBER_MONO("Đơn sắc Cyber", "Tương phản cao tối giản")
}

enum class TouchParticleEffect(val title: String) {
    NONE("Tắt hiệu ứng"),
    NEON_SPARKS("Tia sáng Neon"),
    SAKURA_PETALS("Hoa anh đào rơi"),
    SNOW_FALL("Tuyết rơi mùa đông"),
    STARLIGHT("Bụi sao lấp lánh")
}

enum class UserProfile(val title: String, val description: String) {
    PERSONAL("Cá nhân", "Tất cả ứng dụng và giao diện chính"),
    WORK("Công việc", "Ưu tiên email, tài liệu, lịch họp"),
    KIDS("Trẻ em", "Chỉ hiển thị app học tập, khóa cài đặt"),
    GUEST("Khách", "Ẩn ảnh, tin nhắn, ngân hàng và mạng xã hội")
}

enum class GestureType(val title: String, val iconName: String) {
    SWIPE_UP("Vuốt lên", "arrow_upward"),
    SWIPE_DOWN("Vuốt xuống", "arrow_downward"),
    DOUBLE_TAP("Chạm 2 lần", "touch_app"),
    TWO_FINGER_SWIPE_UP("Vuốt 2 ngón lên", "expand_less"),
    TWO_FINGER_SWIPE_DOWN("Vuốt 2 ngón xuống", "expand_more"),
    PINCH_IN("Véo vào (Pinch In)", "pinch"),
    PINCH_OUT("Mở rộng (Pinch Out)", "zoom_out_map")
}

enum class ActionType(val title: String) {
    OPEN_DRAWER("Mở ngăn kéo ứng dụng"),
    GLOBAL_SEARCH("Tìm kiếm toàn cục & Trợ lý"),
    LOCK_SCREEN("Khóa màn hình (Ambient Display)"),
    OPEN_FEED("Mở Feed AI & Smart Stack"),
    OPEN_CONTROLS("Mở Bảng điều khiển nhanh"),
    TOGGLE_ONE_HANDED("Bật/Tắt Chế độ một tay"),
    GAME_BOOST("Tăng tốc Game (Game Mode)"),
    BATTERY_SAVER("Chế độ tiết kiệm pin AMOLED"),
    TOGGLE_FOCUS("Bật/Tắt Chế độ tập trung"),
    OPEN_SETTINGS("Mở Cài đặt Launcher"),
    RUN_MACRO("Chạy Macro tự động")
}

data class MacroStep(
    val actionName: String,
    val param: String = ""
)

data class LauncherMacro(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String = "bolt",
    val steps: List<MacroStep>
)

data class VaultAccessLog(
    val timestamp: Long,
    val packageName: String,
    val appName: String,
    val success: Boolean
)

data class ThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val accentColorHex: String,
    val wallpaperIndex: Int,
    val iconShape: IconShape,
    val fontScale: Float = 1.0f
)

enum class WidgetType(val title: String, val description: String, val defaultSpanX: Int, val defaultSpanY: Int) {
    CLOCK_DATE("Đồng hồ & Lịch", "Hiển thị giờ số neon, ngày tháng và báo thức", 4, 2),
    WEATHER("Thời tiết trực tiếp", "Dự báo thời tiết, nhiệt độ, độ ẩm và biểu tượng", 4, 2),
    SYSTEM_STATS("Trạng thái hệ thống", "Pin %, RAM còn trống, nhiệt độ máy", 4, 1),
    MUSIC_PLAYER("Trình phát nhạc", "Bảng điều khiển nhạc nhanh với thanh âm thanh", 4, 2),
    QUICK_NOTES("Ghi chú nhanh", "Ghi lại ý tưởng hoặc lời nhắc việc cần làm", 4, 2),
    GOOGLE_SEARCH("Thanh tìm kiếm Google", "Tìm kiếm Google, Voice và Phím tắt tính toán", 4, 1),
    SMART_STACK("Smart Stack 2.0", "Hộp xoay: Vẽ tay, Note việc, Ảnh, Video", 4, 2),
    QUICK_CONTROLS("Điều khiển nhanh", "Bật tắt Wi-Fi, Bluetooth, đèn pin, âm lượng", 4, 2),
    APP_GLANCE("Widget Ứng Dụng", "Tiện ích mở rộng tương tác cho mọi app", 2, 2),
    POMODORO("Đồng hồ Pomodoro", "Đếm ngược 25 phút tập trung / 5 phút nghỉ ngơi", 4, 2),
    COUNTDOWN("Đếm ngược sự kiện", "Theo dõi số ngày còn lại đến sinh nhật, kỳ thi, Tết", 2, 2),
    HABIT_TRACKER("Theo dõi thói quen", "Tích chọn thói quen hàng ngày (Uống nước, Đọc sách...)", 4, 2),
    MINI_CALCULATOR("Máy tính bỏ túi", "Máy tính nhanh trực tiếp ngay trên màn hình chính", 2, 2),
    QUOTE_CALENDAR("Lịch & Trích dẫn", "Lịch ngày và câu nói truyền cảm hứng mỗi ngày", 4, 2),
    EXPENSE_TRACKER("Chi tiêu nhanh", "Ghi nhanh chi tiêu hàng ngày và xem tổng hôm nay", 4, 2)
}

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val customLabel: String? = null,
    val iconDrawable: Drawable? = null,
    val category: AppCategory = AppCategory.TOOLS,
    val usageCount: Int = 0,
    val lastUsedTimestamp: Long = 0L,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false,
    val notificationCount: Int = 0,
    val estimatedSizeBytes: Long = 42_000_000L // 42MB default estimate
) {
    val displayLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: label
}

sealed interface HomeItem {
    val id: String
    val pageIndex: Int
    val row: Int
    val col: Int

    data class AppItem(
        override val id: String,
        val packageName: String,
        override val pageIndex: Int,
        override val row: Int,
        override val col: Int
    ) : HomeItem

    data class FolderItem(
        override val id: String,
        val name: String,
        val colorHex: String,
        val appPackages: List<String>,
        override val pageIndex: Int,
        override val row: Int,
        override val col: Int
    ) : HomeItem

    data class WidgetItem(
        override val id: String,
        val type: WidgetType,
        override val pageIndex: Int,
        override val row: Int,
        override val col: Int,
        val spanX: Int = 4,
        val spanY: Int = 2,
        val appPackage: String? = null,
        val customTitle: String? = null
    ) : HomeItem
}

data class LauncherPreferences(
    val gridColumns: Int = 4,
    val gridRows: Int = 5,
    val showLabels: Boolean = true,
    val iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    val iconScale: Float = 1.0f,
    val labelSizeSp: Float = 12f,
    val accentColorHex: String = "#3B82F6",
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val dockPackages: List<String> = emptyList(),
    val hiddenPackages: Set<String> = emptySet(),
    val lockedPackages: Set<String> = emptySet(),
    val pinCode: String = "1234",
    val wallpaperIndex: Int = 0,
    val focusModeActive: Boolean = false,
    val focusModeAllowedPackages: Set<String> = emptySet(),
    val transitionEffect: TransitionEffect = TransitionEffect.CUBE,
    val swipeUpAction: String = "OPEN_DRAWER",
    val swipeDownAction: String = "GLOBAL_SEARCH",
    val doubleTapAction: String = "LOCK_SCREEN",
    val twoFingerSwipeUpAction: String = "OPEN_CONTROLS",
    val twoFingerSwipeDownAction: String = "OPEN_FEED",
    val pinchInAction: String = "TOGGLE_ONE_HANDED",
    val pinchOutAction: String = "GAME_BOOST",
    val showNotificationBadges: Boolean = true,
    val drawerSortMode: String = "ALPHABETICAL", // ALPHABETICAL, MOST_USED, RECENT
    val dockAlpha: Float = 0.45f,

    // 2.0 Feature Additions
    val currentProfile: UserProfile = UserProfile.PERSONAL,
    val liveThemeEnabled: Boolean = true,
    val oneHandedMode: Boolean = false,
    val gameModeActive: Boolean = false,
    val ultraBatterySaver: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val dynamicPrivacyLowBattery: Boolean = true,
    val highContrastText: Boolean = false,
    val customAliases: Map<String, String> = mapOf(
        "ms" to "com.facebook.orca",
        "yt" to "com.google.android.youtube",
        "zl" to "com.zing.zalo",
        "st" to "com.android.settings",
        "gg" to "com.android.chrome",
        "cm" to "com.android.camera"
    ),

    // Wallpaper, Video & Background Music Customization
    val wallpaperType: String = "PRESET_IMAGE", // "PRESET_IMAGE", "CUSTOM_IMAGE", "VIDEO", "GRADIENT"
    val customWallpaperUri: String? = null,
    val presetImageId: String = "sloth", // "sloth", "nature", "cyber", "sunset", "galaxy"
    val backgroundMusicEnabled: Boolean = false,
    val backgroundMusicPreset: String = "LOFI", // "LOFI", "RAIN", "COSMIC", "PIANO"
    val backgroundMusicVolume: Float = 0.5f,
    val videoSoundEnabled: Boolean = false,
    val liveAnimationEnabled: Boolean = true,

    // Smart Stack 2.0 (Smart Attack) Customizations
    val smartStackCards: List<String> = listOf("HANDWRITING", "TASKS", "PHOTO", "VIDEO", "WEATHER", "MUSIC"),
    val smartStackPhotoUri: String? = null,
    val smartStackVideoUri: String? = null,
    val smartStackTaskNotes: String = "Hoàn thành kế hoạch launcher|Tập thể dục 30 phút|Đọc sách buổi tối",
    val smartStackDoodleLines: String = "",

    // Launcher 3.0 Feature Additions (20+ New Upgrades)
    val dynamicIslandEnabled: Boolean = true,
    val edgeSidebarEnabled: Boolean = true,
    val zenModeEnabled: Boolean = false,
    val touchParticleEffect: TouchParticleEffect = TouchParticleEffect.NEON_SPARKS,
    val blueLightFilterIntensity: Float = 0f,
    val iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    val dockNeonBorderEnabled: Boolean = true,
    val autoProfileByTime: Boolean = false,
    val habitStates: String = "1010", // 4 habits bitmask
    val countdownTitle: String = "Sự kiện quan trọng",
    val countdownDaysLeft: Int = 14,
    val dailyExpenseTotal: Long = 65000L,
    val unlockedAchievements: Set<String> = setOf("WELCOME_3_0")
)

