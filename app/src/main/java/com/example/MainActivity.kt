package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppInfo
import com.example.data.model.HomeItem
import com.example.ui.LauncherViewModel
import com.example.ui.PinPromptState
import com.example.ui.appmanager.AppManagerDialog
import com.example.ui.controls.QuickControlCenter
import com.example.ui.dialogs.AchievementsDialog
import com.example.ui.dialogs.AddWidgetDialog
import com.example.ui.dialogs.AppContextMenuDialog
import com.example.ui.dialogs.FocusModeDialog
import com.example.ui.dialogs.FolderDialog
import com.example.ui.dialogs.NotificationDigestDialog
import com.example.ui.dialogs.PinLockDialog
import com.example.ui.dialogs.ShortcutBuilderDialog
import com.example.ui.dialogs.SmartStackEditorDialog
import com.example.ui.dialogs.ToolboxHubDialog
import com.example.ui.dialogs.UsageInsightsDialog
import com.example.ui.dialogs.WallpaperCustomizerDialog
import com.example.ui.drawer.AppDrawerSheet
import com.example.ui.feed.FeedScreen
import com.example.ui.gestures.GestureStudioDialog
import com.example.ui.home.GestureDrawOverlay
import com.example.ui.home.HomeScreen
import com.example.ui.lock.NightstandModeOverlay
import com.example.ui.lock.SimulatedLockScreen
import com.example.ui.search.GlobalSearchOverlay
import com.example.ui.settings.LauncherSettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeEngineDialog
import com.example.ui.vault.EncryptedVaultScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LauncherApp(
                    onExpandNotifications = { expandNotificationShade(this) }
                )
            }
        }
    }

    private fun expandNotificationShade(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val method = statusBarManager.getMethod("expandNotificationsPanel")
            method.invoke(statusBarService)
        } catch (e: Exception) {
            // Handled gracefully
        }
    }
}

@Composable
fun LauncherApp(
    onExpandNotifications: () -> Unit,
    viewModel: LauncherViewModel = viewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val homeItems by viewModel.homeItems.collectAsStateWithLifecycle()
    val allApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val drawerApps by viewModel.drawerApps.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
    val dockItems by viewModel.dockItems.collectAsStateWithLifecycle()

    val isDrawerOpen by viewModel.isDrawerOpen.collectAsStateWithLifecycle()
    val isGlobalSearchOpen by viewModel.isGlobalSearchOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isSimulatedLockActive by viewModel.isSimulatedLockActive.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()
    val isWallpaperCustomizerOpen by viewModel.isWallpaperCustomizerOpen.collectAsStateWithLifecycle()

    // 2.0 Sheet & Dialog States
    val isFeedOpen by viewModel.isFeedOpen.collectAsStateWithLifecycle()
    val isControlsOpen by viewModel.isControlsOpen.collectAsStateWithLifecycle()
    val isGestureStudioOpen by viewModel.isGestureStudioOpen.collectAsStateWithLifecycle()
    val isThemeEngineOpen by viewModel.isThemeEngineOpen.collectAsStateWithLifecycle()
    val isVaultOpen by viewModel.isVaultOpen.collectAsStateWithLifecycle()
    val isAppManagerOpen by viewModel.isAppManagerOpen.collectAsStateWithLifecycle()
    val vaultAccessLogs by viewModel.vaultAccessLogs.collectAsStateWithLifecycle()

    // 3.0 Sheet, Dialog & Live States
    val isToolboxOpen by viewModel.isToolboxOpen.collectAsStateWithLifecycle()
    val isGestureDrawOpen by viewModel.isGestureDrawOpen.collectAsStateWithLifecycle()
    val isNightstandOpen by viewModel.isNightstandOpen.collectAsStateWithLifecycle()
    val isUsageInsightsOpen by viewModel.isUsageInsightsOpen.collectAsStateWithLifecycle()
    val isNotificationDigestOpen by viewModel.isNotificationDigestOpen.collectAsStateWithLifecycle()
    val isShortcutBuilderOpen by viewModel.isShortcutBuilderOpen.collectAsStateWithLifecycle()
    val isAchievementsOpen by viewModel.isAchievementsOpen.collectAsStateWithLifecycle()
    val pomodoroRunning by viewModel.pomodoroRunning.collectAsStateWithLifecycle()
    val pomodoroSecondsLeft by viewModel.pomodoroSecondsLeft.collectAsStateWithLifecycle()
    val batteryPercent by viewModel.batteryPercent.collectAsStateWithLifecycle()
    val isCharging by viewModel.isCharging.collectAsStateWithLifecycle()

    val activeFolder by viewModel.activeFolder.collectAsStateWithLifecycle()
    val contextMenuApp by viewModel.contextMenuApp.collectAsStateWithLifecycle()
    val pinPrompt by viewModel.pinPrompt.collectAsStateWithLifecycle()
    val isAddWidgetDialogOpen by viewModel.isAddWidgetDialogOpen.collectAsStateWithLifecycle()
    val isFocusModeDialogOpen by viewModel.isFocusModeDialogOpen.collectAsStateWithLifecycle()
    val isSmartStackEditorOpen by viewModel.isSmartStackEditorOpen.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val drawerSearchQuery by viewModel.drawerSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedDrawerCategory.collectAsStateWithLifecycle()
    val globalSearchQuery by viewModel.globalSearchQuery.collectAsStateWithLifecycle()

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        val msg = toastMessage
        if (!msg.isNullOrBlank()) {
            Toast.makeText(viewModel.getApplication(), msg, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage.value = null
        }
    }

    // Handle back button hierarchically
    BackHandler(
        enabled = isSimulatedLockActive || isSettingsOpen || isGlobalSearchOpen || isDrawerOpen ||
                isFeedOpen || isControlsOpen || isGestureStudioOpen ||
                isThemeEngineOpen || isVaultOpen || isAppManagerOpen ||
                isToolboxOpen || isGestureDrawOpen || isNightstandOpen ||
                isUsageInsightsOpen || isNotificationDigestOpen ||
                isShortcutBuilderOpen || isAchievementsOpen ||
                activeFolder != null || isEditMode || isWallpaperCustomizerOpen
    ) {
        when {
            isSimulatedLockActive -> { /* Keep simulated lock screen active on Back press */ }
            isNightstandOpen -> viewModel.closeNightstand()
            isGestureDrawOpen -> viewModel.closeGestureDraw()
            isToolboxOpen -> viewModel.closeToolbox()
            isUsageInsightsOpen -> viewModel.closeUsageInsights()
            isNotificationDigestOpen -> viewModel.closeNotificationDigest()
            isShortcutBuilderOpen -> viewModel.closeShortcutBuilder()
            isAchievementsOpen -> viewModel.closeAchievements()
            isWallpaperCustomizerOpen -> viewModel.closeWallpaperCustomizer()
            isGestureStudioOpen -> viewModel.closeGestureStudio()
            isThemeEngineOpen -> viewModel.closeThemeEngine()
            isVaultOpen -> viewModel.closeVault()
            isAppManagerOpen -> viewModel.closeAppManager()
            isControlsOpen -> viewModel.closeControls()
            isFeedOpen -> viewModel.closeFeed()
            isSettingsOpen -> viewModel.closeSettings()
            isGlobalSearchOpen -> viewModel.closeGlobalSearch()
            isDrawerOpen -> viewModel.closeAppDrawer()
            activeFolder != null -> viewModel.activeFolder.value = null
            isEditMode -> viewModel.toggleEditMode()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Home Screen
        HomeScreen(
            preferences = preferences,
            homeItems = homeItems,
            dockItems = dockItems,
            dockApps = dockApps,
            allApps = allApps,
            isEditMode = isEditMode,
            onAppClick = { viewModel.onAppClick(it) },
            onAppLongClick = { viewModel.contextMenuApp.value = it },
            onFolderClick = { viewModel.activeFolder.value = it },
            onOpenDrawer = {
                viewModel.executeGestureAction(preferences.swipeUpAction, onExpandNotifications)
            },
            onOpenSearch = { viewModel.openGlobalSearch() },
            onOpenSettings = { viewModel.openSettings() },
            onOpenFeed = { viewModel.openFeed() },
            onOpenControls = { viewModel.openControls() },
            onToggleOneHanded = { viewModel.toggleOneHanded() },
            onDoubleTap = {
                viewModel.executeGestureAction(preferences.doubleTapAction, onExpandNotifications)
            },
            onSwipeDown = {
                viewModel.executeGestureAction(preferences.swipeDownAction, onExpandNotifications)
            },
            onToggleEditMode = { viewModel.toggleEditMode() },
            onRemoveHomeItem = { viewModel.removeHomeItem(it) },
            onOpenAddWidget = { viewModel.isAddWidgetDialogOpen.value = true },
            onOpenWallpaperCustomizer = { viewModel.openWallpaperCustomizer() },
            onMergeApps = { pkg1, pkg2, page, row, col, id1, id2 ->
                viewModel.mergeTwoAppsIntoFolder(pkg1, pkg2, page, row, col, id1, id2)
            },
            onAddAppToFolder = { pkg, folderId, removeId ->
                viewModel.addAppToExistingFolder(pkg, folderId, removeId)
            },
            onOpenSmartStackEditor = { viewModel.openSmartStackEditor() },
            onResizeWidget = { id, sx, sy -> viewModel.resizeWidget(id, sx, sy) },
            onMoveHomeItem = { id, page, row, col -> viewModel.moveHomeItem(id, page, row, col) },
            onUpdateTaskNotes = { viewModel.updateSmartStackTaskNotes(it) },
            onUpdateDoodle = { viewModel.updateSmartStackDoodle(it) },
            pomodoroRunning = pomodoroRunning,
            pomodoroSecondsLeft = pomodoroSecondsLeft,
            batteryPercent = batteryPercent,
            isCharging = isCharging,
            onTogglePomodoro = { viewModel.togglePomodoro() },
            onResetPomodoro = { viewModel.resetPomodoro() },
            onToggleMusic = { viewModel.toggleBackgroundMusic(!preferences.backgroundMusicEnabled) },
            onToggleZenMode = { viewModel.toggleZenMode() },
            onOpenToolbox = { viewModel.openToolbox() },
            onOpenGestureDraw = { viewModel.openGestureDraw() },
            onOpenNightstand = { viewModel.openNightstand() },
            onOpenUsageInsights = { viewModel.openUsageInsights() },
            onOpenNotifications = { viewModel.openNotificationDigest() },
            onOpenShortcutBuilder = { viewModel.openShortcutBuilder() },
            onOpenAchievements = { viewModel.openAchievements() },
            onToggleHabit = { viewModel.toggleHabit(it) },
            onAdjustCountdown = { viewModel.adjustCountdownDays(it) },
            onAddExpense = { viewModel.addDailyExpense(it) },
            onResetExpense = { viewModel.resetDailyExpense() }
        )

        // 2. App Drawer
        AppDrawerSheet(
            isOpen = isDrawerOpen,
            apps = drawerApps,
            searchQuery = drawerSearchQuery,
            selectedCategory = selectedCategory,
            sortMode = preferences.drawerSortMode,
            iconShape = preferences.iconShape,
            iconPackStyle = preferences.iconPackStyle,
            showLabels = preferences.showLabels,
            onSearchChange = { viewModel.drawerSearchQuery.value = it },
            onCategoryChange = { viewModel.selectedDrawerCategory.value = it },
            onSortChange = { viewModel.updateDrawerSortMode(it) },
            onAppClick = { viewModel.onAppClick(it) },
            onAppLongClick = { viewModel.contextMenuApp.value = it },
            onClose = { viewModel.closeAppDrawer() }
        )

        // 3. Global Search Overlay with NLP Assistant
        GlobalSearchOverlay(
            isOpen = isGlobalSearchOpen,
            query = globalSearchQuery,
            apps = allApps,
            secretPin = preferences.pinCode,
            hiddenPackages = preferences.hiddenPackages,
            aliases = preferences.customAliases,
            iconShape = preferences.iconShape,
            iconPackStyle = preferences.iconPackStyle,
            onQueryChange = { viewModel.globalSearchQuery.value = it },
            onAppClick = { viewModel.onAppClick(it) },
            onOpenHiddenVault = { viewModel.openVault() },
            onExecuteFeature = { viewModel.executeFeature(it) },
            onClose = { viewModel.closeGlobalSearch() }
        )

        // 4. Feed AI & Smart Stack
        FeedScreen(
            isOpen = isFeedOpen,
            apps = allApps,
            iconShape = preferences.iconShape,
            iconPackStyle = preferences.iconPackStyle,
            onAppClick = { viewModel.onAppClick(it) },
            onClose = { viewModel.closeFeed() }
        )

        // 5. Quick Controls Center
        QuickControlCenter(
            isOpen = isControlsOpen,
            gameModeActive = preferences.gameModeActive,
            batterySaverActive = preferences.ultraBatterySaver,
            oneHandedActive = preferences.oneHandedMode,
            focusModeActive = preferences.focusModeActive,
            onToggleGameMode = { viewModel.toggleGameMode() },
            onToggleBatterySaver = { viewModel.toggleBatterySaver() },
            onToggleOneHanded = { viewModel.toggleOneHanded() },
            onToggleFocusMode = { viewModel.toggleFocusMode() },
            onLockScreen = { viewModel.lockScreenSimulated() },
            onClose = { viewModel.closeControls() }
        )

        // 6. Launcher Settings Screen
        LauncherSettingsScreen(
            isOpen = isSettingsOpen,
            preferences = preferences,
            allApps = allApps,
            onClose = { viewModel.closeSettings() },
            onUpdateGrid = { cols, rows -> viewModel.updateGrid(cols, rows) },
            onUpdateIconShape = { viewModel.updateIconShape(it) },
            onUpdateIconScale = { viewModel.updateIconScale(it) },
            onUpdateAccentColor = { viewModel.updateAccentColor(it) },
            onUpdateWallpaper = { viewModel.updateWallpaper(it) },
            onToggleShowLabels = { viewModel.toggleShowLabels() },
            onUpdateTransition = { viewModel.updateTransitionEffect(it) },
            onUpdatePin = { viewModel.updatePinCode(it) },
            onToggleAppLock = { viewModel.toggleAppLock(it) },
            onToggleAppHidden = { viewModel.toggleAppHidden(it) },
            onToggleFocusMode = { viewModel.toggleFocusMode() },
            onOpenFocusDialog = { viewModel.isFocusModeDialogOpen.value = true },
            onOpenGestureStudio = { viewModel.openGestureStudio() },
            onOpenThemeEngine = { viewModel.openThemeEngine() },
            onOpenVault = { viewModel.openVault() },
            onOpenAppManager = { viewModel.openAppManager() },
            onOpenWallpaperCustomizer = { viewModel.openWallpaperCustomizer() },
            onToggleDynamicIsland = { viewModel.toggleDynamicIsland() },
            onToggleEdgeSidebar = { viewModel.toggleEdgeSidebar() },
            onToggleZenMode = { viewModel.toggleZenMode() },
            onSelectTouchParticle = { viewModel.setTouchParticleEffect(it) },
            onUpdateBlueLightFilter = { viewModel.setBlueLightFilter(it) },
            onSelectIconPackStyle = { viewModel.setIconPackStyle(it) },
            onToggleDockNeonBorder = { viewModel.toggleDockNeonBorder() },
            onToggleAutoProfileByTime = { viewModel.toggleAutoProfileByTime() },
            onToggleSoundEffects = { viewModel.toggleSoundEffects() },
            onToggleHapticFeedback = { viewModel.toggleHapticFeedback() },
            onOpenToolbox = { viewModel.openToolbox() },
            onOpenGestureDraw = { viewModel.openGestureDraw() },
            onOpenNightstand = { viewModel.openNightstand() },
            onOpenUsageInsights = { viewModel.openUsageInsights() },
            onOpenNotificationDigest = { viewModel.openNotificationDigest() },
            onOpenAchievements = { viewModel.openAchievements() },
            onExportBackup = { viewModel.exportBackup() },
            onRestoreBackup = { viewModel.restoreBackup(it) }
        )

        // 7. Gesture Studio Dialog
        GestureStudioDialog(
            isOpen = isGestureStudioOpen,
            preferences = preferences,
            onUpdateGesture = { g, a -> viewModel.updateGesture(g, a) },
            onRunMacro = { viewModel.runMacro(it) },
            onClose = { viewModel.closeGestureStudio() }
        )

        // 8. Theme Engine Dialog
        ThemeEngineDialog(
            isOpen = isThemeEngineOpen,
            preferences = preferences,
            onApplyPreset = { viewModel.applyThemePreset(it) },
            onGenerateAiWallpaper = { viewModel.generateAiWallpaper() },
            onToggleLiveTheme = { viewModel.toggleLiveTheme() },
            onClose = { viewModel.closeThemeEngine() }
        )

        // 9. Encrypted Vault Screen
        EncryptedVaultScreen(
            isOpen = isVaultOpen,
            preferences = preferences,
            allApps = allApps,
            accessLogs = vaultAccessLogs,
            onSelectProfile = { viewModel.selectProfile(it) },
            onToggleDynamicPrivacy = { viewModel.toggleDynamicPrivacy() },
            onAppClick = { viewModel.onAppClick(it) },
            onClose = { viewModel.closeVault() }
        )

        // 10. Advanced App Manager Dialog
        AppManagerDialog(
            isOpen = isAppManagerOpen,
            apps = allApps,
            iconShape = preferences.iconShape,
            onAppInfo = { viewModel.appRepository.openAppInfo(it) },
            onUninstall = { viewModel.appRepository.requestUninstall(it) },
            onClose = { viewModel.closeAppManager() }
        )

        // 11. AMOLED Lock Screen Simulation
        SimulatedLockScreen(
            isActive = isSimulatedLockActive,
            onUnlock = { viewModel.unlockScreenSimulated() }
        )

        // 12. Active Folder Dialog
        activeFolder?.let { folder ->
            FolderDialog(
                folder = folder,
                allApps = allApps,
                iconShape = preferences.iconShape,
                iconPackStyle = preferences.iconPackStyle,
                onDismiss = { viewModel.activeFolder.value = null },
                onAppClick = { viewModel.onAppClick(it) },
                onUpdateFolder = { name, colorHex, apps ->
                    viewModel.updateFolder(folder.id, name, colorHex, apps)
                },
                onDeleteFolder = {
                    viewModel.removeHomeItem(folder.id)
                    viewModel.activeFolder.value = null
                }
            )
        }

        // 13. Context Menu Dialog (Compact Popup matching reference)
        contextMenuApp?.let { app ->
            AppContextMenuDialog(
                app = app,
                allApps = allApps,
                isInDock = preferences.dockPackages.contains(app.packageName),
                onDismiss = { viewModel.contextMenuApp.value = null },
                onOpenApp = { viewModel.onAppClick(app) },
                onAddToHome = { viewModel.addAppToHome(app.packageName) },
                onRemoveFromHome = { viewModel.removeAppFromHome(app.packageName) },
                onToggleLock = { viewModel.toggleAppLock(app.packageName) },
                onToggleHidden = { viewModel.toggleAppHidden(app.packageName) },
                onRename = { newName -> viewModel.renameApp(app.packageName, newName) },
                onAppInfo = { viewModel.appRepository.openAppInfo(app.packageName) },
                onUninstall = { viewModel.appRepository.requestUninstall(app.packageName) },
                onCreateFolderWith = { otherPkg -> viewModel.mergeTwoAppsIntoFolder(app.packageName, otherPkg) },
                onToggleDock = { viewModel.toggleDockApp(app.packageName) }
            )
        }

        // 14. PIN Prompt Dialog
        pinPrompt?.let { prompt ->
            val promptTitle = when (prompt) {
                is PinPromptState.UnlockApp -> "Khóa ứng dụng: ${prompt.app.displayLabel}"
                is PinPromptState.UnlockHiddenVault -> "Mở Kho Ứng Dụng Ẩn"
                is PinPromptState.ChangePin -> "Nhập mã PIN"
            }
            PinLockDialog(
                title = promptTitle,
                subtitle = "Nhập mã PIN 4 số để tiếp tục",
                onDismiss = { viewModel.pinPrompt.value = null },
                onPinEntered = { viewModel.onPinSubmitted(it) }
            )
        }

        // 15. Add Widget Dialog (Widget 2.0 with Any App & Smart Stack)
        if (isAddWidgetDialogOpen) {
            AddWidgetDialog(
                allApps = allApps,
                iconShape = preferences.iconShape,
                onDismiss = { viewModel.isAddWidgetDialogOpen.value = false },
                onWidgetSelected = { type, sx, sy -> viewModel.addWidget(type, sx, sy) },
                onAppWidgetSelected = { pkg, label, sx, sy -> viewModel.addAppWidget(pkg, label, sx, sy) }
            )
        }

        // 16. Focus Mode Config Dialog
        if (isFocusModeDialogOpen) {
            FocusModeDialog(
                isActive = preferences.focusModeActive,
                allowedPackages = preferences.focusModeAllowedPackages,
                allApps = allApps,
                iconShape = preferences.iconShape,
                onToggleActive = { viewModel.toggleFocusMode() },
                onUpdateAllowed = { viewModel.updateFocusAllowedPackages(it) },
                onDismiss = { viewModel.isFocusModeDialogOpen.value = false }
            )
        }

        // 17. Wallpaper & Ambient Music Customizer Dialog
        WallpaperCustomizerDialog(
            isOpen = isWallpaperCustomizerOpen,
            preferences = preferences,
            onDismiss = { viewModel.closeWallpaperCustomizer() },
            onSelectPresetImage = { viewModel.setPresetImage(it) },
            onSelectCustomMedia = { uri, isVideo -> viewModel.setCustomWallpaper(uri, isVideo) },
            onSelectGradient = { viewModel.setWallpaperGradient(it) },
            onToggleBackgroundMusic = { viewModel.toggleBackgroundMusic(it) },
            onSelectMusicPreset = { viewModel.setMusicPreset(it) },
            onUpdateMusicVolume = { viewModel.setMusicVolume(it) },
            onToggleLiveAnimation = { viewModel.toggleLiveAnimation(it) },
            onToggleVideoSound = { viewModel.toggleVideoSound(it) }
        )

        // 18. Smart Stack 2.0 Editor Dialog (Chỉnh sửa / Bỏ / Thêm widget trong Smart Stack)
        if (isSmartStackEditorOpen) {
            SmartStackEditorDialog(
                preferences = preferences,
                onDismiss = { viewModel.closeSmartStackEditor() },
                onUpdateCards = { viewModel.updateSmartStackCards(it) },
                onPickPhoto = { viewModel.updateSmartStackPhoto(it) },
                onPickVideo = { viewModel.updateSmartStackVideo(it) }
            )
        }

        // 19. Toolbox Hub 3.0 (Unit Converter, Stopwatch, Sensor Compass & Bubble Level, QR)
        ToolboxHubDialog(
            isOpen = isToolboxOpen,
            onDismiss = { viewModel.closeToolbox() }
        )

        // 20. Gesture Letter Draw Overlay
        GestureDrawOverlay(
            isOpen = isGestureDrawOpen,
            onClose = { viewModel.closeGestureDraw() },
            onRecognizedLetter = { viewModel.handleDrawnLetter(it) }
        )

        // 21. AMOLED Nightstand Flip Clock Mode
        NightstandModeOverlay(
            isOpen = isNightstandOpen,
            batteryPercent = batteryPercent,
            musicEnabled = preferences.backgroundMusicEnabled,
            musicPreset = preferences.backgroundMusicPreset,
            onToggleMusic = { viewModel.toggleBackgroundMusic(!preferences.backgroundMusicEnabled) },
            onClose = { viewModel.closeNightstand() }
        )

        // 22. Usage Insights & Digital Wellbeing Score
        UsageInsightsDialog(
            isOpen = isUsageInsightsOpen,
            apps = allApps,
            iconShape = preferences.iconShape,
            iconPackStyle = preferences.iconPackStyle,
            onDismiss = { viewModel.closeUsageInsights() }
        )

        // 23. Smart Notification Digest
        NotificationDigestDialog(
            isOpen = isNotificationDigestOpen,
            onDismiss = { viewModel.closeNotificationDigest() }
        )

        // 24. Quick Shortcut & Widget Builder
        ShortcutBuilderDialog(
            isOpen = isShortcutBuilderOpen,
            onDismiss = { viewModel.closeShortcutBuilder() },
            onAddWidgetPreset = { viewModel.addWidget(it) },
            onTriggerAction = { viewModel.executeFeature(it) }
        )

        // 25. Launcher Achievements & Badges
        AchievementsDialog(
            isOpen = isAchievementsOpen,
            unlockedIds = preferences.unlockedAchievements,
            onDismiss = { viewModel.closeAchievements() }
        )
    }
}
