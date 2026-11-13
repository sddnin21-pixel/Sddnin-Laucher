package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActionType
import com.example.data.model.AppCategory
import com.example.data.model.AppInfo
import com.example.data.model.GestureType
import com.example.data.model.HomeItem
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.data.model.LauncherMacro
import com.example.data.model.LauncherPreferences
import com.example.data.model.ThemePreset
import com.example.data.model.TouchParticleEffect
import com.example.data.model.TransitionEffect
import com.example.data.model.UserProfile
import com.example.data.model.VaultAccessLog
import com.example.data.model.WidgetType
import com.example.data.repository.AppRepository
import com.example.data.repository.SettingsRepository
import com.example.util.AmbientAudioManager
import com.example.util.SoundFxHelper
import com.example.util.VietnameseUnaccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface DockItem {
    val id: String
    data class AppItem(override val id: String, val app: AppInfo) : DockItem
    data class FolderItem(override val id: String, val folder: HomeItem.FolderItem) : DockItem
}

sealed interface PinPromptState {
    data class UnlockApp(val app: AppInfo) : PinPromptState
    data class UnlockHiddenVault(val onUnlocked: () -> Unit) : PinPromptState
    data class ChangePin(val isConfirming: Boolean = false, val tempPin: String = "") : PinPromptState
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    val appRepository = AppRepository(application)
    val settingsRepository = SettingsRepository(application)
    val ambientAudio = AmbientAudioManager(application)
    val soundFx = SoundFxHelper(application)

    val preferences: StateFlow<LauncherPreferences> = settingsRepository.launcherPreferences
    val homeItems: StateFlow<List<HomeItem>> = settingsRepository.homeItems
    val installedApps: StateFlow<List<AppInfo>> = combine(
        appRepository.installedApps,
        preferences
    ) { apps, prefs ->
        apps.map { app ->
            app.copy(
                isLocked = prefs.lockedPackages.contains(app.packageName),
                isHidden = prefs.hiddenPackages.contains(app.packageName)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, appRepository.installedApps.value)

    // Navigation and Sheets state
    val isDrawerOpen = MutableStateFlow(false)
    val isGlobalSearchOpen = MutableStateFlow(false)
    val isSettingsOpen = MutableStateFlow(false)
    val isSimulatedLockActive = MutableStateFlow(false)
    val isEditMode = MutableStateFlow(false)

    // 2.0 & 3.0 Sheet & Dialog States
    val isFeedOpen = MutableStateFlow(false)
    val isControlsOpen = MutableStateFlow(false)
    val isGestureStudioOpen = MutableStateFlow(false)
    val isThemeEngineOpen = MutableStateFlow(false)
    val isVaultOpen = MutableStateFlow(false)
    val isAppManagerOpen = MutableStateFlow(false)
    val isToolboxOpen = MutableStateFlow(false)
    val isGestureDrawOpen = MutableStateFlow(false)
    val isNightstandOpen = MutableStateFlow(false)
    val isUsageInsightsOpen = MutableStateFlow(false)
    val isNotificationDigestOpen = MutableStateFlow(false)
    val isShortcutBuilderOpen = MutableStateFlow(false)
    val isAchievementsOpen = MutableStateFlow(false)

    // Live Pomodoro & Battery States
    val pomodoroRunning = MutableStateFlow(false)
    val pomodoroSecondsLeft = MutableStateFlow(25 * 60)
    val batteryPercent = MutableStateFlow(88)
    val isCharging = MutableStateFlow(false)

    // Dialog states
    val activeFolder = MutableStateFlow<HomeItem.FolderItem?>(null)
    val contextMenuApp = MutableStateFlow<AppInfo?>(null)
    val pinPrompt = MutableStateFlow<PinPromptState?>(null)
    val isAddWidgetDialogOpen = MutableStateFlow(false)
    val isFocusModeDialogOpen = MutableStateFlow(false)
    val isWallpaperCustomizerOpen = MutableStateFlow(false)
    val isSmartStackEditorOpen = MutableStateFlow(false)
    val toastMessage = MutableStateFlow<String?>("")

    init {
        viewModelScope.launch {
            var prevEnabled: Boolean? = null
            var prevPreset: String? = null
            var prevVolume: Float? = null
            preferences.collect { prefs ->
                val enabledChanged = prevEnabled != prefs.backgroundMusicEnabled
                val presetChanged = prevPreset != prefs.backgroundMusicPreset
                val volumeChanged = prevVolume != prefs.backgroundMusicVolume

                if (enabledChanged || presetChanged) {
                    if (prefs.backgroundMusicEnabled) {
                        ambientAudio.start(prefs.backgroundMusicPreset, prefs.backgroundMusicVolume)
                    } else {
                        ambientAudio.stop()
                    }
                } else if (volumeChanged && prefs.backgroundMusicEnabled) {
                    ambientAudio.setVolume(prefs.backgroundMusicVolume)
                }

                prevEnabled = prefs.backgroundMusicEnabled
                prevPreset = prefs.backgroundMusicPreset
                prevVolume = prefs.backgroundMusicVolume
            }
        }
        viewModelScope.launch {
            while (true) {
                try {
                    val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                    val pct = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 88
                    if (pct in 1..100) batteryPercent.value = pct
                    isCharging.value = bm?.isCharging ?: false
                } catch (_: Exception) {}
                delay(15000L)
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (pomodoroRunning.value) {
                    if (pomodoroSecondsLeft.value > 0) {
                        pomodoroSecondsLeft.value = pomodoroSecondsLeft.value - 1
                    } else {
                        pomodoroRunning.value = false
                        pomodoroSecondsLeft.value = 25 * 60
                        soundFx.triggerHaptic(preferences.value.hapticFeedbackEnabled, strong = true)
                        toastMessage.value = "⏰ Hoàn thành phiên tập trung Pomodoro 25 phút!"
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ambientAudio.stop()
        appRepository.cleanup()
    }

    // Vault Access Audit Logs
    val vaultAccessLogs = MutableStateFlow<List<VaultAccessLog>>(
        listOf(
            VaultAccessLog(System.currentTimeMillis() - 7200000L, "com.google.android.apps.photos", "Ảnh", true),
            VaultAccessLog(System.currentTimeMillis() - 14400000L, "com.zing.zalo", "Zalo", true)
        )
    )

    // Drawer state
    val drawerSearchQuery = MutableStateFlow("")
    val selectedDrawerCategory = MutableStateFlow(AppCategory.ALL)

    // Global Search Query
    val globalSearchQuery = MutableStateFlow("")

    // Filtered drawer apps with Multi-Profile and Dynamic Privacy rules
    val drawerApps: StateFlow<List<AppInfo>> = combine(
        installedApps,
        preferences,
        drawerSearchQuery,
        selectedDrawerCategory
    ) { apps, prefs, query, category ->
        val filtered = apps.filter { app ->
            // In GUEST profile, hide social & photo & finance
            if (prefs.currentProfile == UserProfile.GUEST) {
                if (app.category == AppCategory.SOCIAL || app.packageName.contains("photo") || app.packageName.contains("bank")) {
                    return@filter false
                }
            }

            // In KIDS profile, only allow games and tools
            if (prefs.currentProfile == UserProfile.KIDS) {
                if (app.category != AppCategory.GAMES && app.category != AppCategory.TOOLS) {
                    return@filter false
                }
            }

            // Skip hidden apps unless unlocked
            if (prefs.hiddenPackages.contains(app.packageName)) return@filter false

            // Category filter
            if (category != AppCategory.ALL && app.category != category) return@filter false

            // Search query filter with Vietnamese unaccent
            if (query.isNotBlank() && !VietnameseUnaccent.matchesFuzzy(app.displayLabel, query) && !app.packageName.contains(query, ignoreCase = true)) {
                return@filter false
            }
            true
        }

        when (prefs.drawerSortMode) {
            "MOST_USED" -> filtered.sortedByDescending { it.usageCount }
            "RECENT" -> filtered.sortedByDescending { it.lastUsedTimestamp }
            else -> filtered.sortedBy { it.displayLabel.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dock items supporting both Apps and Folders
    val dockItems: StateFlow<List<DockItem>> = combine(installedApps, homeItems, preferences) { apps, items, prefs ->
        prefs.dockPackages.mapNotNull { entry ->
            if (entry.startsWith("folder:")) {
                val fId = entry.removePrefix("folder:")
                val folder = items.filterIsInstance<HomeItem.FolderItem>().find { it.id == fId }
                if (folder != null) DockItem.FolderItem(fId, folder) else null
            } else {
                val app = apps.find { it.packageName == entry } ?: AppInfo(
                    packageName = entry,
                    activityName = "",
                    label = entry.substringAfterLast("."),
                    iconDrawable = null
                )
                DockItem.AppItem(entry, app)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Legacy dock apps
    val dockApps: StateFlow<List<AppInfo>> = combine(installedApps, preferences) { apps, prefs ->
        prefs.dockPackages.filterNot { it.startsWith("folder:") }.mapNotNull { pkg ->
            apps.find { it.packageName == pkg } ?: AppInfo(
                packageName = pkg,
                activityName = "",
                label = pkg.substringAfterLast("."),
                iconDrawable = null
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onAppClick(app: AppInfo) {
        val prefs = preferences.value
        soundFx.playTapSound(prefs.soundEffectsEnabled)
        soundFx.triggerHaptic(prefs.hapticFeedbackEnabled)
        // Check focus mode restriction
        if (prefs.focusModeActive && !prefs.focusModeAllowedPackages.contains(app.packageName)) {
            toastMessage.value = "Chế độ tập trung đang bật! Ứng dụng này tạm thời bị chặn để tránh xao nhãng."
            return
        }

        // Check PIN lock
        if (prefs.lockedPackages.contains(app.packageName)) {
            pinPrompt.value = PinPromptState.UnlockApp(app)
            return
        }

        launchAppDirectly(app.packageName)
    }

    private fun launchAppDirectly(packageName: String) {
        val success = appRepository.launchApp(packageName)
        if (!success) {
            toastMessage.value = "Không thể mở ứng dụng: $packageName"
        } else {
            // Close sheets
            isDrawerOpen.value = false
            isGlobalSearchOpen.value = false
            isVaultOpen.value = false
        }
    }

    fun onPinSubmitted(enteredPin: String) {
        val prompt = pinPrompt.value ?: return
        val currentPin = preferences.value.pinCode

        when (prompt) {
            is PinPromptState.UnlockApp -> {
                val app = prompt.app
                val isValid = enteredPin == currentPin
                val newLog = VaultAccessLog(System.currentTimeMillis(), app.packageName, app.displayLabel, isValid)
                vaultAccessLogs.value = listOf(newLog) + vaultAccessLogs.value

                if (isValid) {
                    pinPrompt.value = null
                    launchAppDirectly(app.packageName)
                } else {
                    toastMessage.value = "Mã PIN không chính xác!"
                }
            }
            is PinPromptState.UnlockHiddenVault -> {
                if (enteredPin == currentPin) {
                    pinPrompt.value = null
                    prompt.onUnlocked()
                } else {
                    toastMessage.value = "Mã PIN không chính xác!"
                }
            }
            is PinPromptState.ChangePin -> {
                // Handled in settings
            }
        }
    }

    // Navigation triggers
    fun openAppDrawer() { isDrawerOpen.value = true }
    fun closeAppDrawer() { isDrawerOpen.value = false; drawerSearchQuery.value = "" }

    fun openGlobalSearch() { isGlobalSearchOpen.value = true; globalSearchQuery.value = "" }
    fun closeGlobalSearch() { isGlobalSearchOpen.value = false; globalSearchQuery.value = "" }

    fun openSettings() { isSettingsOpen.value = true }
    fun closeSettings() { isSettingsOpen.value = false }

    fun openFeed() { isFeedOpen.value = true }
    fun closeFeed() { isFeedOpen.value = false }

    fun openControls() { isControlsOpen.value = true }
    fun closeControls() { isControlsOpen.value = false }

    fun openGestureStudio() { isGestureStudioOpen.value = true }
    fun closeGestureStudio() { isGestureStudioOpen.value = false }

    fun openThemeEngine() { isThemeEngineOpen.value = true }
    fun closeThemeEngine() { isThemeEngineOpen.value = false }

    fun openVault() { isVaultOpen.value = true }
    fun closeVault() { isVaultOpen.value = false }

    fun openAppManager() { isAppManagerOpen.value = true }
    fun closeAppManager() { isAppManagerOpen.value = false }

    fun lockScreenSimulated() { isSimulatedLockActive.value = true }
    fun unlockScreenSimulated() { isSimulatedLockActive.value = false }

    fun toggleEditMode() { isEditMode.value = !isEditMode.value }

    fun addAppToHome(packageName: String, page: Int = 0) {
        val added = settingsRepository.addAppToHome(packageName, page)
        if (added) toastMessage.value = "Đã thêm vào Màn hình chính"
    }

    fun removeHomeItem(id: String) {
        settingsRepository.removeHomeItem(id)
        toastMessage.value = "Đã xóa khỏi Màn hình chính"
    }

    fun moveHomeItem(id: String, newPage: Int, newRow: Int, newCol: Int) {
        settingsRepository.moveHomeItem(id, newPage, newRow, newCol)
    }

    fun addWidget(type: WidgetType, spanX: Int = type.defaultSpanX, spanY: Int = type.defaultSpanY, pageIndex: Int = 0) {
        settingsRepository.addWidget(type, pageIndex, spanX, spanY)
        isAddWidgetDialogOpen.value = false
        unlockAchievement("WIDGET_ARCHITECT")
        toastMessage.value = "Đã thêm widget: ${type.title}"
    }

    fun addAppWidget(packageName: String, label: String, spanX: Int, spanY: Int, pageIndex: Int = 0) {
        settingsRepository.addAppGlanceWidget(packageName, label, spanX, spanY, pageIndex)
        isAddWidgetDialogOpen.value = false
        unlockAchievement("WIDGET_ARCHITECT")
        toastMessage.value = "Đã thêm widget cho ứng dụng: $label"
    }

    fun resizeWidget(id: String, spanX: Int, spanY: Int) {
        settingsRepository.resizeWidget(id, spanX, spanY)
        toastMessage.value = "Đã thay đổi kích thước widget: ${spanX}x${spanY}"
    }

    fun openSmartStackEditor() { isSmartStackEditorOpen.value = true }
    fun closeSmartStackEditor() { isSmartStackEditorOpen.value = false }

    fun updateSmartStackCards(cards: List<String>) {
        settingsRepository.updatePreferences { it.copy(smartStackCards = cards) }
        toastMessage.value = "Đã cập nhật các thẻ trong Smart Stack (${cards.size} thẻ)"
    }

    fun updateSmartStackPhoto(uri: Uri) {
        settingsRepository.updatePreferences { it.copy(smartStackPhotoUri = uri.toString()) }
        toastMessage.value = "Đã đổi ảnh trong Smart Stack"
    }

    fun updateSmartStackVideo(uri: Uri) {
        settingsRepository.updatePreferences { it.copy(smartStackVideoUri = uri.toString()) }
        toastMessage.value = "Đã đổi video trong Smart Stack"
    }

    fun updateSmartStackTaskNotes(notes: String) {
        settingsRepository.updatePreferences { it.copy(smartStackTaskNotes = notes) }
    }

    fun updateSmartStackDoodle(doodle: String) {
        settingsRepository.updatePreferences { it.copy(smartStackDoodleLines = doodle) }
    }

    fun toggleAppLock(packageName: String) {
        settingsRepository.toggleAppLock(packageName)
        contextMenuApp.value = null
    }

    fun toggleAppHidden(packageName: String) {
        settingsRepository.toggleAppHidden(packageName)
        contextMenuApp.value = null
    }

    fun renameApp(packageName: String, newName: String) {
        appRepository.setCustomLabel(packageName, newName.takeIf { it.isNotBlank() })
        contextMenuApp.value = null
    }

    fun setDockApps(packages: List<String>) {
        settingsRepository.setDockApps(packages)
    }

    fun updateGrid(cols: Int, rows: Int) {
        settingsRepository.updatePreferences { it.copy(gridColumns = cols, gridRows = rows) }
    }

    fun updateIconShape(shape: IconShape) {
        settingsRepository.updatePreferences { it.copy(iconShape = shape) }
    }

    fun updateIconScale(scale: Float) {
        settingsRepository.updatePreferences { it.copy(iconScale = scale) }
    }

    fun updateAccentColor(hex: String) {
        settingsRepository.updatePreferences { it.copy(accentColorHex = hex) }
    }

    fun updateWallpaper(index: Int) {
        settingsRepository.updatePreferences { it.copy(wallpaperIndex = index) }
    }

    fun toggleShowLabels() {
        settingsRepository.updatePreferences { it.copy(showLabels = !it.showLabels) }
    }

    fun toggleFocusMode() {
        val next = !preferences.value.focusModeActive
        settingsRepository.updatePreferences { it.copy(focusModeActive = next) }
        toastMessage.value = if (next) "Đã bật Chế độ tập trung" else "Đã tắt Chế độ tập trung"
    }

    fun updateFocusAllowedPackages(packages: Set<String>) {
        settingsRepository.updatePreferences { it.copy(focusModeAllowedPackages = packages) }
    }

    fun updateTransitionEffect(effect: TransitionEffect) {
        settingsRepository.updatePreferences { it.copy(transitionEffect = effect) }
    }

    fun updatePinCode(newPin: String) {
        settingsRepository.updatePreferences { it.copy(pinCode = newPin) }
        toastMessage.value = "Đã cập nhật mã PIN mới"
    }

    fun updateDrawerSortMode(mode: String) {
        settingsRepository.updatePreferences { it.copy(drawerSortMode = mode) }
    }

    fun createFolder(name: String, colorHex: String, appPackages: List<String>, pageIndex: Int = 0, row: Int = 0, col: Int = 0) {
        settingsRepository.createFolder(name, colorHex, appPackages, pageIndex, row, col)
        toastMessage.value = "Đã tạo thư mục: $name"
    }

    fun updateFolder(folderId: String, name: String, colorHex: String, appPackages: List<String>) {
        settingsRepository.updateFolder(folderId, name, colorHex, appPackages)
        if (appPackages.isEmpty()) {
            activeFolder.value = null
        } else {
            activeFolder.value = activeFolder.value?.copy(name = name, colorHex = colorHex, appPackages = appPackages)
        }
    }

    // 2.0 Feature Implementations
    fun toggleGameMode() {
        val next = !preferences.value.gameModeActive
        settingsRepository.updatePreferences { it.copy(gameModeActive = next) }
        toastMessage.value = if (next) "🎮 Game Boost: ON! Đã giải phóng RAM & chặn thông báo" else "Đã tắt Game Boost"
    }

    fun toggleBatterySaver() {
        val next = !preferences.value.ultraBatterySaver
        settingsRepository.updatePreferences { it.copy(ultraBatterySaver = next) }
        toastMessage.value = if (next) "⚡ Chế độ tiết kiệm pin AMOLED: BẬT" else "Đã tắt Tiết kiệm pin"
    }

    fun toggleOneHanded() {
        val next = !preferences.value.oneHandedMode
        settingsRepository.updatePreferences { it.copy(oneHandedMode = next) }
        toastMessage.value = if (next) "Chế độ một tay: BẬT" else "Đã tắt Chế độ một tay"
    }

    fun selectProfile(profile: UserProfile) {
        settingsRepository.updatePreferences { it.copy(currentProfile = profile) }
        toastMessage.value = "Đã chuyển sang Hồ sơ: ${profile.title}"
    }

    fun toggleDynamicPrivacy() {
        val next = !preferences.value.dynamicPrivacyLowBattery
        settingsRepository.updatePreferences { it.copy(dynamicPrivacyLowBattery = next) }
    }

    fun toggleLiveTheme() {
        val next = !preferences.value.liveThemeEnabled
        settingsRepository.updatePreferences { it.copy(liveThemeEnabled = next) }
        if (next) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val wallIdx = when (hour) {
                in 6..11 -> 0 // Space Morning
                in 12..17 -> 2 // Emerald Daylight
                in 18..21 -> 4 // Sunset Dusk
                else -> 5 // AMOLED Midnight
            }
            updateWallpaper(wallIdx)
            toastMessage.value = "Live Theme: Đã cập nhật theo giờ hiện tại"
        } else {
            toastMessage.value = "Đã tắt Live Theme"
        }
    }

    fun generateAiWallpaper() {
        val newIdx = ((preferences.value.wallpaperIndex + 1) % 6)
        updateWallpaper(newIdx)
        toastMessage.value = "✨ AI Wallpaper đã tạo & áp dụng hình nền phong cách mới!"
    }

    fun applyThemePreset(preset: ThemePreset) {
        settingsRepository.updatePreferences {
            it.copy(
                accentColorHex = preset.accentColorHex,
                wallpaperIndex = preset.wallpaperIndex,
                iconShape = preset.iconShape
            )
        }
        toastMessage.value = "Đã áp dụng toàn bộ Theme: ${preset.name}"
    }

    fun updateGesture(gesture: GestureType, action: ActionType) {
        settingsRepository.updatePreferences {
            when (gesture) {
                GestureType.SWIPE_UP -> it.copy(swipeUpAction = action.name)
                GestureType.SWIPE_DOWN -> it.copy(swipeDownAction = action.name)
                GestureType.DOUBLE_TAP -> it.copy(doubleTapAction = action.name)
                GestureType.TWO_FINGER_SWIPE_UP -> it.copy(twoFingerSwipeUpAction = action.name)
                GestureType.TWO_FINGER_SWIPE_DOWN -> it.copy(twoFingerSwipeDownAction = action.name)
                GestureType.PINCH_IN -> it.copy(pinchInAction = action.name)
                GestureType.PINCH_OUT -> it.copy(pinchOutAction = action.name)
            }
        }
        toastMessage.value = "Đã gán: ${gesture.title} -> ${action.title}"
    }

    fun runMacro(macro: LauncherMacro) {
        when (macro.id) {
            "home_relax" -> {
                toggleOneHanded()
                toastMessage.value = "⚡ Đã chạy Macro: ${macro.name}"
            }
            "deep_work" -> {
                if (!preferences.value.focusModeActive) toggleFocusMode()
                toastMessage.value = "⚡ Đã chạy Macro: ${macro.name}"
            }
            "ultra_battery" -> {
                toggleBatterySaver()
                toastMessage.value = "⚡ Đã chạy Macro: ${macro.name}"
            }
            "game_boost" -> {
                if (!preferences.value.gameModeActive) toggleGameMode()
                toastMessage.value = "⚡ Đã chạy Macro: ${macro.name}"
            }
            else -> {
                toastMessage.value = "⚡ Đã chạy Macro: ${macro.name}"
            }
        }
    }

    fun executeGestureAction(actionName: String, onExpandNotifications: () -> Unit = {}) {
        when (actionName) {
            ActionType.OPEN_DRAWER.name, "APP_DRAWER" -> openAppDrawer()
            ActionType.GLOBAL_SEARCH.name -> openGlobalSearch()
            ActionType.LOCK_SCREEN.name -> lockScreenSimulated()
            ActionType.OPEN_FEED.name -> openFeed()
            ActionType.OPEN_CONTROLS.name -> openControls()
            ActionType.TOGGLE_ONE_HANDED.name -> toggleOneHanded()
            ActionType.GAME_BOOST.name -> toggleGameMode()
            ActionType.BATTERY_SAVER.name -> toggleBatterySaver()
            ActionType.TOGGLE_FOCUS.name -> toggleFocusMode()
            ActionType.OPEN_SETTINGS.name -> openSettings()
            ActionType.RUN_MACRO.name -> toggleFocusMode()
            "NOTIFICATIONS" -> {
                onExpandNotifications()
                openGlobalSearch()
            }
            else -> openGlobalSearch()
        }
    }

    fun executeFeature(featureCode: String) {
        when {
            featureCode == "GAME_BOOST" -> toggleGameMode()
            featureCode == "BATTERY_SAVER" -> toggleBatterySaver()
            featureCode == "ONE_HANDED" -> toggleOneHanded()
            featureCode == "FOCUS_MODE" -> toggleFocusMode()
            featureCode == "LOCK_SCREEN" -> lockScreenSimulated()
            featureCode == "FEED" -> openFeed()
            featureCode == "CONTROLS" -> openControls()
            featureCode == "TOOLBOX" -> openToolbox()
            featureCode == "GESTURE_DRAW" -> openGestureDraw()
            featureCode == "NIGHTSTAND" -> openNightstand()
            featureCode == "USAGE_INSIGHTS" -> openUsageInsights()
            featureCode == "NOTIFICATION_DIGEST" -> openNotificationDigest()
            featureCode == "ACHIEVEMENTS" -> openAchievements()
            featureCode.startsWith("COUNTDOWN_TITLE:") -> {
                val title = featureCode.removePrefix("COUNTDOWN_TITLE:")
                setCountdownTitle(title)
            }
            else -> toastMessage.value = "Đã kích hoạt: $featureCode"
        }
    }

    fun exportBackup(): String {
        return settingsRepository.exportBackupJson()
    }

    fun restoreBackup(json: String): Boolean {
        val ok = settingsRepository.restoreBackupJson(json)
        if (ok) {
            toastMessage.value = "Khôi phục bố cục và cài đặt 2.0 thành công!"
        } else {
            toastMessage.value = "Lỗi khi khôi phục bản sao lưu."
        }
        return ok
    }

    // Wallpaper & Ambient Sound Management
    fun openWallpaperCustomizer() { isWallpaperCustomizerOpen.value = true }
    fun closeWallpaperCustomizer() { isWallpaperCustomizerOpen.value = false }

    fun setCustomWallpaper(uri: Uri, isVideo: Boolean) {
        val uriString = if (uri == Uri.EMPTY) null else uri.toString()
        settingsRepository.updatePreferences {
            it.copy(
                wallpaperType = if (isVideo) "VIDEO" else "CUSTOM_IMAGE",
                customWallpaperUri = uriString
            )
        }
        toastMessage.value = if (isVideo) {
            if (uriString == null) "Đã bật hình nền động Cực Quang Aurora!" else "Đã áp dụng Video làm hình nền động!"
        } else {
            "Đã áp dụng ảnh hình nền thành công!"
        }
    }

    fun setPresetImage(presetId: String) {
        settingsRepository.updatePreferences {
            it.copy(
                wallpaperType = "PRESET_IMAGE",
                presetImageId = presetId,
                customWallpaperUri = null
            )
        }
        val name = when (presetId) {
            "sloth" -> "Chú lười bình yên"
            "nature" -> "Rừng ngọc lục bảo"
            "cyber" -> "Thành phố Cyberpunk"
            "sunset" -> "Hoàng hôn lãng mạn"
            else -> presetId
        }
        toastMessage.value = "Đã áp dụng hình nền: $name"
    }

    fun setWallpaperGradient(index: Int) {
        settingsRepository.updatePreferences {
            it.copy(
                wallpaperType = "GRADIENT",
                wallpaperIndex = index,
                customWallpaperUri = null
            )
        }
        toastMessage.value = "Đã áp dụng màu nền"
    }

    fun toggleBackgroundMusic(enabled: Boolean) {
        settingsRepository.updatePreferences { it.copy(backgroundMusicEnabled = enabled) }
        if (enabled) unlockAchievement("AMBIENT_CHILL")
        toastMessage.value = if (enabled) "Đã bật nhạc nền thư giãn" else "Đã tắt nhạc nền"
    }

    fun setMusicPreset(preset: String) {
        settingsRepository.updatePreferences { it.copy(backgroundMusicPreset = preset) }
        ambientAudio.start(preset, preferences.value.backgroundMusicVolume)
        val name = when (preset) {
            "LOFI" -> "Lofi Chill Beats"
            "RAIN" -> "Mưa Rừng Nhiệt Đới"
            "COSMIC" -> "Âm Hưởng Không Gian"
            "PIANO" -> "Piano Hoàng Hôn"
            else -> preset
        }
        toastMessage.value = "Đã đổi nhạc nền: $name"
    }

    fun setMusicVolume(volume: Float) {
        settingsRepository.updatePreferences { it.copy(backgroundMusicVolume = volume) }
        ambientAudio.setVolume(volume)
    }

    fun toggleVideoSound(enabled: Boolean) {
        settingsRepository.updatePreferences { it.copy(videoSoundEnabled = enabled) }
    }

    fun toggleLiveAnimation(enabled: Boolean) {
        settingsRepository.updatePreferences { it.copy(liveAnimationEnabled = enabled) }
    }

    // Folder Merging and App Management
    fun mergeTwoAppsIntoFolder(
        pkg1: String,
        pkg2: String,
        page: Int = 0,
        row: Int = 0,
        col: Int = 0,
        id1ToRemove: String? = null,
        id2ToRemove: String? = null
    ) {
        val app1 = installedApps.value.find { it.packageName == pkg1 }
        val app2 = installedApps.value.find { it.packageName == pkg2 }
        val folderName = if (app1 != null && app2 != null) {
            if (app1.category == app2.category && app1.category != AppCategory.TOOLS) {
                app1.category.title
            } else "Thư mục"
        } else "Thư mục mới"

        settingsRepository.mergeAppsIntoFolder(pkg1, pkg2, page, row, col, id1ToRemove, id2ToRemove, folderName)
        toastMessage.value = "Đã gộp 2 ứng dụng thành thư mục: $folderName"
    }

    fun addAppToExistingFolder(pkg: String, folderId: String, removeAppItemId: String? = null) {
        settingsRepository.addAppToFolder(pkg, folderId, removeAppItemId)
        toastMessage.value = "Đã thêm ứng dụng vào thư mục"
    }

    fun removeAppFromFolder(pkg: String, folderId: String) {
        val folder = homeItems.value.filterIsInstance<HomeItem.FolderItem>().find { it.id == folderId }
        if (folder != null) {
            val updatedApps = folder.appPackages.filterNot { it == pkg }
            updateFolder(folderId, folder.name, folder.colorHex, updatedApps)
            // Add back to home screen
            settingsRepository.addAppToHome(pkg, folder.pageIndex)
            toastMessage.value = "Đã đưa ứng dụng ra ngoài màn hình chính"
        }
    }

    fun removeAppFromHome(pkg: String) {
        settingsRepository.removeAppFromHomeByPackage(pkg)
        toastMessage.value = "Đã xóa khỏi Màn hình chính"
    }

    fun toggleDockApp(pkg: String) {
        val inDockBefore = preferences.value.dockPackages.contains(pkg)
        settingsRepository.toggleDockApp(pkg)
        toastMessage.value = if (!inDockBefore) "Đã thêm vào thanh Dock" else "Đã xóa khỏi thanh Dock"
    }

    // =========================================================================
    // Launcher 3.0 Feature Controls (20+ New Features)
    // =========================================================================
    fun openToolbox() { isToolboxOpen.value = true }
    fun closeToolbox() { isToolboxOpen.value = false }

    fun openGestureDraw() { isGestureDrawOpen.value = true }
    fun closeGestureDraw() { isGestureDrawOpen.value = false }

    fun openNightstand() {
        isNightstandOpen.value = true
        unlockAchievement("AMBIENT_CHILL")
    }
    fun closeNightstand() { isNightstandOpen.value = false }

    fun openUsageInsights() { isUsageInsightsOpen.value = true }
    fun closeUsageInsights() { isUsageInsightsOpen.value = false }

    fun openNotificationDigest() { isNotificationDigestOpen.value = true }
    fun closeNotificationDigest() { isNotificationDigestOpen.value = false }

    fun openShortcutBuilder() { isShortcutBuilderOpen.value = true }
    fun closeShortcutBuilder() { isShortcutBuilderOpen.value = false }

    fun openAchievements() { isAchievementsOpen.value = true }
    fun closeAchievements() { isAchievementsOpen.value = false }

    fun togglePomodoro() {
        pomodoroRunning.value = !pomodoroRunning.value
        soundFx.playTapSound(preferences.value.soundEffectsEnabled, 1.25f)
        if (pomodoroRunning.value) {
            unlockAchievement("POMODORO_MASTER")
            toastMessage.value = "🍅 Bắt đầu tập trung Pomodoro!"
        }
    }

    fun resetPomodoro() {
        pomodoroRunning.value = false
        pomodoroSecondsLeft.value = 25 * 60
        toastMessage.value = "Đã đặt lại Pomodoro 25:00"
    }

    fun toggleDynamicIsland() {
        val next = !preferences.value.dynamicIslandEnabled
        settingsRepository.updatePreferences { it.copy(dynamicIslandEnabled = next) }
        toastMessage.value = if (next) "Đã bật Dynamic Island" else "Đã ẩn Dynamic Island"
    }

    fun toggleEdgeSidebar() {
        val next = !preferences.value.edgeSidebarEnabled
        settingsRepository.updatePreferences { it.copy(edgeSidebarEnabled = next) }
        toastMessage.value = if (next) "Đã bật Thanh Cạnh Edge Sidebar" else "Đã tắt Thanh Cạnh"
    }

    fun toggleZenMode() {
        val next = !preferences.value.zenModeEnabled
        settingsRepository.updatePreferences { it.copy(zenModeEnabled = next) }
        if (next) unlockAchievement("ZEN_EXPLORER")
        toastMessage.value = if (next) "🧘 Chế độ Zen Tối Giản: BẬT" else "Đã thoát chế độ Zen"
    }

    fun setTouchParticleEffect(effect: TouchParticleEffect) {
        settingsRepository.updatePreferences { it.copy(touchParticleEffect = effect) }
        toastMessage.value = "Hiệu ứng màn hình: ${effect.title}"
    }

    fun setBlueLightFilter(intensity: Float) {
        settingsRepository.updatePreferences { it.copy(blueLightFilterIntensity = intensity.coerceIn(0f, 0.45f)) }
    }

    fun setIconPackStyle(style: IconPackStyle) {
        settingsRepository.updatePreferences { it.copy(iconPackStyle = style) }
        toastMessage.value = "Phong cách Icon: ${style.title}"
    }

    fun toggleDockNeonBorder() {
        val next = !preferences.value.dockNeonBorderEnabled
        settingsRepository.updatePreferences { it.copy(dockNeonBorderEnabled = next) }
    }

    fun toggleAutoProfileByTime() {
        val next = !preferences.value.autoProfileByTime
        settingsRepository.updatePreferences { it.copy(autoProfileByTime = next) }
        if (next) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val suggested = when (hour) {
                in 8..17 -> UserProfile.WORK
                else -> UserProfile.PERSONAL
            }
            selectProfile(suggested)
        }
    }

    fun toggleSoundEffects() {
        val next = !preferences.value.soundEffectsEnabled
        settingsRepository.updatePreferences { it.copy(soundEffectsEnabled = next) }
        if (next) soundFx.playTapSound(true, 1.3f)
    }

    fun toggleHapticFeedback() {
        val next = !preferences.value.hapticFeedbackEnabled
        settingsRepository.updatePreferences { it.copy(hapticFeedbackEnabled = next) }
        if (next) soundFx.triggerHaptic(true)
    }

    fun toggleHabit(index: Int) {
        val chars = preferences.value.habitStates.padEnd(4, '0').toCharArray()
        if (index in 0..3) {
            chars[index] = if (chars[index] == '1') '0' else '1'
            settingsRepository.updatePreferences { it.copy(habitStates = String(chars)) }
            soundFx.playTapSound(preferences.value.soundEffectsEnabled, 1.35f)
        }
    }

    fun adjustCountdownDays(days: Int) {
        settingsRepository.updatePreferences { it.copy(countdownDaysLeft = days.coerceAtLeast(0)) }
    }

    fun setCountdownTitle(title: String) {
        settingsRepository.updatePreferences { it.copy(countdownTitle = title.ifBlank { "Sự kiện quan trọng" }) }
    }

    fun addDailyExpense(amount: Long) {
        settingsRepository.updatePreferences { it.copy(dailyExpenseTotal = it.dailyExpenseTotal + amount) }
        soundFx.playTapSound(preferences.value.soundEffectsEnabled, 1.15f)
    }

    fun resetDailyExpense() {
        settingsRepository.updatePreferences { it.copy(dailyExpenseTotal = 0L) }
        toastMessage.value = "Đã đặt lại chi tiêu hôm nay về 0 đ"
    }

    fun unlockAchievement(id: String) {
        val current = preferences.value.unlockedAchievements
        if (!current.contains(id)) {
            settingsRepository.updatePreferences { it.copy(unlockedAchievements = current + id) }
        }
    }

    fun handleDrawnLetter(letter: String) {
        isGestureDrawOpen.value = false
        unlockAchievement("GESTURE_WIZARD")
        soundFx.playTapSound(preferences.value.soundEffectsEnabled, 1.4f)
        when (letter) {
            "O" -> {
                val browser = installedApps.value.firstOrNull {
                    it.packageName.contains("chrome") || it.packageName.contains("browser")
                }
                if (browser != null) onAppClick(browser) else openGlobalSearch()
            }
            "C" -> openSettings()
            "M" -> toggleBackgroundMusic(!preferences.value.backgroundMusicEnabled)
            "W" -> openToolbox()
            "Z" -> toggleZenMode()
            else -> openGlobalSearch()
        }
    }
}

