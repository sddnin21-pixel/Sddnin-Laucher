package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.HomeItem
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.data.model.LauncherPreferences
import com.example.data.model.TouchParticleEffect
import com.example.data.model.TransitionEffect
import com.example.data.model.UserProfile
import com.example.data.model.WidgetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SettingsRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("sddnin_launcher_settings", Context.MODE_PRIVATE)

    private val _launcherPreferences = MutableStateFlow(loadPreferences())
    val launcherPreferences: StateFlow<LauncherPreferences> = _launcherPreferences.asStateFlow()

    private val _homeItems = MutableStateFlow<List<HomeItem>>(loadHomeItems())
    val homeItems: StateFlow<List<HomeItem>> = _homeItems.asStateFlow()

    private fun loadPreferences(): LauncherPreferences {
        val jsonStr = prefs.getString("launcher_prefs_json", null)
        if (jsonStr != null) {
            try {
                val obj = JSONObject(jsonStr)
                val dockList = mutableListOf<String>()
                val dockArr = obj.optJSONArray("dockPackages")
                if (dockArr != null) {
                    for (i in 0 until dockArr.length()) dockList.add(dockArr.getString(i))
                } else {
                    dockList.addAll(defaultDockPackages())
                }

                val hiddenSet = mutableSetOf<String>()
                val hiddenArr = obj.optJSONArray("hiddenPackages")
                if (hiddenArr != null) {
                    for (i in 0 until hiddenArr.length()) hiddenSet.add(hiddenArr.getString(i))
                }

                val lockedSet = mutableSetOf<String>()
                val lockedArr = obj.optJSONArray("lockedPackages")
                if (lockedArr != null) {
                    for (i in 0 until lockedArr.length()) lockedSet.add(lockedArr.getString(i))
                }

                val focusAllowedSet = mutableSetOf<String>()
                val focusArr = obj.optJSONArray("focusAllowedPackages")
                if (focusArr != null) {
                    for (i in 0 until focusArr.length()) focusAllowedSet.add(focusArr.getString(i))
                } else {
                    focusAllowedSet.addAll(listOf("com.android.dialer", "com.google.android.apps.messaging", "com.google.android.calendar", "com.google.android.gm"))
                }

                return LauncherPreferences(
                    gridColumns = obj.optInt("gridColumns", 4).coerceIn(3, 6),
                    gridRows = obj.optInt("gridRows", 5).coerceIn(3, 7),
                    showLabels = obj.optBoolean("showLabels", true),
                    iconShape = try {
                        IconShape.valueOf(obj.optString("iconShape", IconShape.ROUNDED_SQUARE.name))
                    } catch (e: Exception) {
                        IconShape.ROUNDED_SQUARE
                    },
                    iconScale = obj.optDouble("iconScale", 1.0).toFloat().coerceIn(0.8f, 1.3f),
                    labelSizeSp = obj.optDouble("labelSizeSp", 12.0).toFloat(),
                    accentColorHex = obj.optString("accentColorHex", "#3B82F6"),
                    themeMode = obj.optString("themeMode", "SYSTEM"),
                    dockPackages = dockList,
                    hiddenPackages = hiddenSet,
                    lockedPackages = lockedSet,
                    pinCode = obj.optString("pinCode", "1234"),
                    wallpaperIndex = obj.optInt("wallpaperIndex", 0),
                    focusModeActive = obj.optBoolean("focusModeActive", false),
                    focusModeAllowedPackages = focusAllowedSet,
                    transitionEffect = try {
                        TransitionEffect.valueOf(obj.optString("transitionEffect", TransitionEffect.CUBE.name))
                    } catch (e: Exception) {
                        TransitionEffect.CUBE
                    },
                    swipeUpAction = obj.optString("swipeUpAction", "APP_DRAWER"),
                    swipeDownAction = obj.optString("swipeDownAction", "NOTIFICATIONS"),
                    doubleTapAction = obj.optString("doubleTapAction", "LOCK_SCREEN"),
                    showNotificationBadges = obj.optBoolean("showNotificationBadges", true),
                    drawerSortMode = obj.optString("drawerSortMode", "ALPHABETICAL"),
                    dockAlpha = obj.optDouble("dockAlpha", 0.45).toFloat(),
                    wallpaperType = obj.optString("wallpaperType", "PRESET_IMAGE"),
                    customWallpaperUri = if (obj.has("customWallpaperUri") && !obj.isNull("customWallpaperUri")) obj.optString("customWallpaperUri") else null,
                    presetImageId = obj.optString("presetImageId", "sloth"),
                    backgroundMusicEnabled = obj.optBoolean("backgroundMusicEnabled", false),
                    backgroundMusicPreset = obj.optString("backgroundMusicPreset", "LOFI"),
                    backgroundMusicVolume = obj.optDouble("backgroundMusicVolume", 0.5).toFloat(),
                    videoSoundEnabled = obj.optBoolean("videoSoundEnabled", false),
                    liveAnimationEnabled = obj.optBoolean("liveAnimationEnabled", true),
                    smartStackCards = run {
                        val arr = obj.optJSONArray("smartStackCards")
                        if (arr != null) {
                            val list = mutableListOf<String>()
                            for (i in 0 until arr.length()) list.add(arr.getString(i))
                            list
                        } else listOf("HANDWRITING", "TASKS", "PHOTO", "VIDEO", "WEATHER", "MUSIC")
                    },
                    smartStackPhotoUri = if (obj.has("smartStackPhotoUri") && !obj.isNull("smartStackPhotoUri")) obj.optString("smartStackPhotoUri") else null,
                    smartStackVideoUri = if (obj.has("smartStackVideoUri") && !obj.isNull("smartStackVideoUri")) obj.optString("smartStackVideoUri") else null,
                    smartStackTaskNotes = obj.optString("smartStackTaskNotes", "Hoàn thành kế hoạch launcher|Tập thể dục 30 phút|Đọc sách buổi tối"),
                    smartStackDoodleLines = obj.optString("smartStackDoodleLines", ""),
                    soundEffectsEnabled = obj.optBoolean("soundEffectsEnabled", true),
                    hapticFeedbackEnabled = obj.optBoolean("hapticFeedbackEnabled", true),
                    currentProfile = try {
                        UserProfile.valueOf(obj.optString("currentProfile", UserProfile.PERSONAL.name))
                    } catch (e: Exception) {
                        UserProfile.PERSONAL
                    },
                    liveThemeEnabled = obj.optBoolean("liveThemeEnabled", true),
                    oneHandedMode = obj.optBoolean("oneHandedMode", false),
                    gameModeActive = obj.optBoolean("gameModeActive", false),
                    ultraBatterySaver = obj.optBoolean("ultraBatterySaver", false),
                    dynamicPrivacyLowBattery = obj.optBoolean("dynamicPrivacyLowBattery", true),
                    twoFingerSwipeUpAction = obj.optString("twoFingerSwipeUpAction", "OPEN_CONTROLS"),
                    twoFingerSwipeDownAction = obj.optString("twoFingerSwipeDownAction", "OPEN_FEED"),
                    pinchInAction = obj.optString("pinchInAction", "TOGGLE_ONE_HANDED"),
                    pinchOutAction = obj.optString("pinchOutAction", "GAME_BOOST"),
                    dynamicIslandEnabled = obj.optBoolean("dynamicIslandEnabled", true),
                    edgeSidebarEnabled = obj.optBoolean("edgeSidebarEnabled", true),
                    zenModeEnabled = obj.optBoolean("zenModeEnabled", false),
                    touchParticleEffect = try {
                        TouchParticleEffect.valueOf(obj.optString("touchParticleEffect", TouchParticleEffect.NEON_SPARKS.name))
                    } catch (e: Exception) {
                        TouchParticleEffect.NEON_SPARKS
                    },
                    blueLightFilterIntensity = obj.optDouble("blueLightFilterIntensity", 0.0).toFloat(),
                    iconPackStyle = try {
                        IconPackStyle.valueOf(obj.optString("iconPackStyle", IconPackStyle.DEFAULT.name))
                    } catch (e: Exception) {
                        IconPackStyle.DEFAULT
                    },
                    dockNeonBorderEnabled = obj.optBoolean("dockNeonBorderEnabled", true),
                    autoProfileByTime = obj.optBoolean("autoProfileByTime", false),
                    habitStates = obj.optString("habitStates", "1010"),
                    countdownTitle = obj.optString("countdownTitle", "Sự kiện quan trọng"),
                    countdownDaysLeft = obj.optInt("countdownDaysLeft", 14),
                    dailyExpenseTotal = obj.optLong("dailyExpenseTotal", 65000L),
                    unlockedAchievements = run {
                        val arr = obj.optJSONArray("unlockedAchievements")
                        if (arr != null) {
                            val s = mutableSetOf<String>()
                            for (i in 0 until arr.length()) s.add(arr.getString(i))
                            s
                        } else setOf("WELCOME_3_0")
                    }
                )
            } catch (e: Exception) {
                Log.e("SettingsRepository", "Error parsing preferences", e)
            }
        }

        return LauncherPreferences(
            dockPackages = defaultDockPackages(),
            focusModeAllowedPackages = setOf("com.android.dialer", "com.google.android.apps.messaging", "com.google.android.calendar", "com.google.android.gm")
        )
    }

    private fun defaultDockPackages(): List<String> {
        return listOf(
            "com.android.dialer",
            "com.google.android.apps.messaging",
            "com.android.chrome",
            "com.google.android.apps.photos",
            "com.android.settings"
        )
    }

    private fun savePreferences(p: LauncherPreferences) {
        try {
            val obj = JSONObject().apply {
                put("gridColumns", p.gridColumns)
                put("gridRows", p.gridRows)
                put("showLabels", p.showLabels)
                put("iconShape", p.iconShape.name)
                put("iconScale", p.iconScale.toDouble())
                put("labelSizeSp", p.labelSizeSp.toDouble())
                put("accentColorHex", p.accentColorHex)
                put("themeMode", p.themeMode)
                put("pinCode", p.pinCode)
                put("wallpaperIndex", p.wallpaperIndex)
                put("focusModeActive", p.focusModeActive)
                put("transitionEffect", p.transitionEffect.name)
                put("swipeUpAction", p.swipeUpAction)
                put("swipeDownAction", p.swipeDownAction)
                put("doubleTapAction", p.doubleTapAction)
                put("showNotificationBadges", p.showNotificationBadges)
                put("drawerSortMode", p.drawerSortMode)
                put("dockAlpha", p.dockAlpha.toDouble())
                put("wallpaperType", p.wallpaperType)
                put("customWallpaperUri", p.customWallpaperUri)
                put("presetImageId", p.presetImageId)
                put("backgroundMusicEnabled", p.backgroundMusicEnabled)
                put("backgroundMusicPreset", p.backgroundMusicPreset)
                put("backgroundMusicVolume", p.backgroundMusicVolume.toDouble())
                put("videoSoundEnabled", p.videoSoundEnabled)
                put("liveAnimationEnabled", p.liveAnimationEnabled)

                val dockArr = JSONArray()
                p.dockPackages.forEach { dockArr.put(it) }
                put("dockPackages", dockArr)

                val hiddenArr = JSONArray()
                p.hiddenPackages.forEach { hiddenArr.put(it) }
                put("hiddenPackages", hiddenArr)

                val lockedArr = JSONArray()
                p.lockedPackages.forEach { lockedArr.put(it) }
                put("lockedPackages", lockedArr)

                val focusArr = JSONArray()
                p.focusModeAllowedPackages.forEach { focusArr.put(it) }
                put("focusAllowedPackages", focusArr)

                val stackArr = JSONArray()
                p.smartStackCards.forEach { stackArr.put(it) }
                put("smartStackCards", stackArr)
                put("smartStackPhotoUri", p.smartStackPhotoUri)
                put("smartStackVideoUri", p.smartStackVideoUri)
                put("smartStackTaskNotes", p.smartStackTaskNotes)
                put("smartStackDoodleLines", p.smartStackDoodleLines)
                put("soundEffectsEnabled", p.soundEffectsEnabled)
                put("hapticFeedbackEnabled", p.hapticFeedbackEnabled)
                put("currentProfile", p.currentProfile.name)
                put("liveThemeEnabled", p.liveThemeEnabled)
                put("oneHandedMode", p.oneHandedMode)
                put("gameModeActive", p.gameModeActive)
                put("ultraBatterySaver", p.ultraBatterySaver)
                put("dynamicPrivacyLowBattery", p.dynamicPrivacyLowBattery)
                put("twoFingerSwipeUpAction", p.twoFingerSwipeUpAction)
                put("twoFingerSwipeDownAction", p.twoFingerSwipeDownAction)
                put("pinchInAction", p.pinchInAction)
                put("pinchOutAction", p.pinchOutAction)
                put("dynamicIslandEnabled", p.dynamicIslandEnabled)
                put("edgeSidebarEnabled", p.edgeSidebarEnabled)
                put("zenModeEnabled", p.zenModeEnabled)
                put("touchParticleEffect", p.touchParticleEffect.name)
                put("blueLightFilterIntensity", p.blueLightFilterIntensity.toDouble())
                put("iconPackStyle", p.iconPackStyle.name)
                put("dockNeonBorderEnabled", p.dockNeonBorderEnabled)
                put("autoProfileByTime", p.autoProfileByTime)
                put("habitStates", p.habitStates)
                put("countdownTitle", p.countdownTitle)
                put("countdownDaysLeft", p.countdownDaysLeft)
                put("dailyExpenseTotal", p.dailyExpenseTotal)
                val achArr = JSONArray()
                p.unlockedAchievements.forEach { achArr.put(it) }
                put("unlockedAchievements", achArr)
            }
            prefs.edit().putString("launcher_prefs_json", obj.toString()).apply()
            _launcherPreferences.value = p
        } catch (e: Exception) {
            Log.e("SettingsRepository", "Error saving preferences", e)
        }
    }

    fun updatePreferences(update: (LauncherPreferences) -> LauncherPreferences) {
        val updated = update(_launcherPreferences.value)
        savePreferences(updated)
    }

    private fun loadHomeItems(): List<HomeItem> {
        val jsonStr = prefs.getString("home_items_json", null)
        if (jsonStr != null) {
            try {
                val arr = JSONArray(jsonStr)
                val list = mutableListOf<HomeItem>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    when (obj.optString("type")) {
                        "app" -> {
                            list.add(
                                HomeItem.AppItem(
                                    id = obj.getString("id"),
                                    packageName = obj.getString("packageName"),
                                    pageIndex = obj.getInt("pageIndex"),
                                    row = obj.getInt("row"),
                                    col = obj.getInt("col")
                                )
                            )
                        }
                        "folder" -> {
                            val subArr = obj.getJSONArray("apps")
                            val subApps = mutableListOf<String>()
                            for (j in 0 until subArr.length()) subApps.add(subArr.getString(j))
                            list.add(
                                HomeItem.FolderItem(
                                    id = obj.getString("id"),
                                    name = obj.getString("name"),
                                    colorHex = obj.optString("colorHex", "#3B82F6"),
                                    appPackages = subApps,
                                    pageIndex = obj.getInt("pageIndex"),
                                    row = obj.getInt("row"),
                                    col = obj.getInt("col")
                                )
                            )
                        }
                        "widget" -> {
                            val wType = try {
                                WidgetType.valueOf(obj.getString("widgetType"))
                            } catch (e: Exception) {
                                WidgetType.CLOCK_DATE
                            }
                            list.add(
                                HomeItem.WidgetItem(
                                    id = obj.getString("id"),
                                    type = wType,
                                    pageIndex = obj.getInt("pageIndex"),
                                    row = obj.getInt("row"),
                                    col = obj.getInt("col"),
                                    spanX = obj.optInt("spanX", 4),
                                    spanY = obj.optInt("spanY", 2),
                                    appPackage = if (obj.has("appPackage") && !obj.isNull("appPackage")) obj.getString("appPackage") else null,
                                    customTitle = if (obj.has("customTitle") && !obj.isNull("customTitle")) obj.getString("customTitle") else null
                                )
                            )
                        }
                    }
                }
                return list
            } catch (e: Exception) {
                Log.e("SettingsRepository", "Error parsing home items", e)
            }
        }

        // Default initial home items: Clock widget on page 0, top apps, page 1 with more apps and folder
        return listOf(
            HomeItem.WidgetItem(
                id = UUID.randomUUID().toString(),
                type = WidgetType.CLOCK_DATE,
                pageIndex = 0,
                row = 0,
                col = 0,
                spanX = 4,
                spanY = 2
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.youtube",
                pageIndex = 0,
                row = 2,
                col = 0
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.spotify.music",
                pageIndex = 0,
                row = 2,
                col = 1
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.gm",
                pageIndex = 0,
                row = 2,
                col = 2
            ),
            HomeItem.FolderItem(
                id = UUID.randomUUID().toString(),
                name = "Xã hội",
                colorHex = "#8B5CF6",
                appPackages = listOf("com.facebook.katana", "com.zing.zalo", "com.zhiliaoapp.musically"),
                pageIndex = 0,
                row = 2,
                col = 3
            ),
            HomeItem.WidgetItem(
                id = UUID.randomUUID().toString(),
                type = WidgetType.WEATHER,
                pageIndex = 1,
                row = 0,
                col = 0,
                spanX = 4,
                spanY = 2
            ),
            HomeItem.WidgetItem(
                id = UUID.randomUUID().toString(),
                type = WidgetType.SYSTEM_STATS,
                pageIndex = 1,
                row = 2,
                col = 0,
                spanX = 4,
                spanY = 1
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.calculator",
                pageIndex = 1,
                row = 3,
                col = 0
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.deskclock",
                pageIndex = 1,
                row = 3,
                col = 1
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.calendar",
                pageIndex = 1,
                row = 3,
                col = 2
            ),
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = "com.google.android.apps.maps",
                pageIndex = 1,
                row = 3,
                col = 3
            )
        )
    }

    private fun saveHomeItems(items: List<HomeItem>) {
        try {
            val arr = JSONArray()
            for (item in items) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("pageIndex", item.pageIndex)
                    put("row", item.row)
                    put("col", item.col)
                    when (item) {
                        is HomeItem.AppItem -> {
                            put("type", "app")
                            put("packageName", item.packageName)
                        }
                        is HomeItem.FolderItem -> {
                            put("type", "folder")
                            put("name", item.name)
                            put("colorHex", item.colorHex)
                            val subArr = JSONArray()
                            item.appPackages.forEach { subArr.put(it) }
                            put("apps", subArr)
                        }
                        is HomeItem.WidgetItem -> {
                            put("type", "widget")
                            put("widgetType", item.type.name)
                            put("spanX", item.spanX)
                            put("spanY", item.spanY)
                            if (item.appPackage != null) put("appPackage", item.appPackage)
                            if (item.customTitle != null) put("customTitle", item.customTitle)
                        }
                    }
                }
                arr.put(obj)
            }
            prefs.edit().putString("home_items_json", arr.toString()).apply()
            _homeItems.value = items
        } catch (e: Exception) {
            Log.e("SettingsRepository", "Error saving home items", e)
        }
    }

    fun addAppToHome(packageName: String, targetPage: Int = 0): Boolean {
        val current = _homeItems.value.toMutableList()
        // find next open cell on targetPage
        val occupied = current.filter { it.pageIndex == targetPage }
        val cols = _launcherPreferences.value.gridColumns
        val rows = _launcherPreferences.value.gridRows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val isOccupied = occupied.any { item ->
                    when (item) {
                        is HomeItem.AppItem -> item.row == r && item.col == c
                        is HomeItem.FolderItem -> item.row == r && item.col == c
                        is HomeItem.WidgetItem -> {
                            r >= item.row && r < item.row + item.spanY &&
                                    c >= item.col && c < item.col + item.spanX
                        }
                    }
                }
                if (!isOccupied) {
                    current.add(
                        HomeItem.AppItem(
                            id = UUID.randomUUID().toString(),
                            packageName = packageName,
                            pageIndex = targetPage,
                            row = r,
                            col = c
                        )
                    )
                    saveHomeItems(current)
                    return true
                }
            }
        }

        // If page is full, add to new page
        val maxPage = current.maxOfOrNull { it.pageIndex } ?: 0
        current.add(
            HomeItem.AppItem(
                id = UUID.randomUUID().toString(),
                packageName = packageName,
                pageIndex = maxPage + 1,
                row = 0,
                col = 0
            )
        )
        saveHomeItems(current)
        return true
    }

    fun removeHomeItem(id: String) {
        val filtered = _homeItems.value.filterNot { it.id == id }
        saveHomeItems(filtered)
    }

    fun moveHomeItem(id: String, newPage: Int, newRow: Int, newCol: Int) {
        val updated = _homeItems.value.map { item ->
            if (item.id == id) {
                when (item) {
                    is HomeItem.AppItem -> item.copy(pageIndex = newPage, row = newRow, col = newCol)
                    is HomeItem.FolderItem -> item.copy(pageIndex = newPage, row = newRow, col = newCol)
                    is HomeItem.WidgetItem -> item.copy(pageIndex = newPage, row = newRow, col = newCol)
                }
            } else item
        }
        saveHomeItems(updated)
    }

    fun createFolder(name: String, colorHex: String, appPackages: List<String>, pageIndex: Int, row: Int, col: Int) {
        val current = _homeItems.value.toMutableList()
        current.add(
            HomeItem.FolderItem(
                id = UUID.randomUUID().toString(),
                name = name,
                colorHex = colorHex,
                appPackages = appPackages,
                pageIndex = pageIndex,
                row = row,
                col = col
            )
        )
        saveHomeItems(current)
    }

    fun mergeAppsIntoFolder(
        pkg1: String,
        pkg2: String,
        pageIndex: Int,
        row: Int,
        col: Int,
        id1ToRemove: String? = null,
        id2ToRemove: String? = null,
        folderName: String = "Thư mục"
    ) {
        val current = _homeItems.value.toMutableList()
        if (id1ToRemove != null) current.removeAll { it.id == id1ToRemove }
        if (id2ToRemove != null) current.removeAll { it.id == id2ToRemove }
        current.removeAll { it is HomeItem.AppItem && (it.packageName == pkg1 || it.packageName == pkg2) && it.pageIndex == pageIndex && it.row == row && it.col == col }

        val newFolder = HomeItem.FolderItem(
            id = UUID.randomUUID().toString(),
            name = folderName,
            colorHex = "#3B82F6",
            appPackages = listOf(pkg1, pkg2).distinct(),
            pageIndex = pageIndex,
            row = row,
            col = col
        )
        current.add(newFolder)
        saveHomeItems(current)
    }

    fun addAppToFolder(pkg: String, folderId: String, removeAppItemId: String? = null) {
        val current = _homeItems.value.toMutableList()
        if (removeAppItemId != null) {
            current.removeAll { it.id == removeAppItemId }
        }
        val updated = current.map { item ->
            if (item.id == folderId && item is HomeItem.FolderItem) {
                if (!item.appPackages.contains(pkg)) {
                    item.copy(appPackages = item.appPackages + pkg)
                } else item
            } else item
        }
        saveHomeItems(updated)
    }

    fun removeAppFromHomeByPackage(pkg: String) {
        val filtered = _homeItems.value.filterNot { it is HomeItem.AppItem && it.packageName == pkg }
        saveHomeItems(filtered)
    }

    fun toggleDockApp(pkg: String) {
        val current = _launcherPreferences.value.dockPackages.toMutableList()
        if (current.contains(pkg)) {
            current.remove(pkg)
        } else {
            if (current.size < 6) {
                current.add(pkg)
            } else {
                current[current.size - 1] = pkg
            }
        }
        updatePreferences { it.copy(dockPackages = current) }
    }


    fun updateFolder(folderId: String, name: String, colorHex: String, appPackages: List<String>) {
        if (appPackages.isEmpty()) {
            removeHomeItem(folderId)
            return
        }
        val updated = _homeItems.value.map { item ->
            if (item.id == folderId && item is HomeItem.FolderItem) {
                item.copy(name = name, colorHex = colorHex, appPackages = appPackages)
            } else item
        }
        saveHomeItems(updated)
    }

    fun addWidget(
        type: WidgetType,
        pageIndex: Int = 0,
        requestedSpanX: Int = type.defaultSpanX,
        requestedSpanY: Int = type.defaultSpanY
    ): Boolean {
        val current = _homeItems.value.toMutableList()
        val cols = _launcherPreferences.value.gridColumns
        val rows = _launcherPreferences.value.gridRows
        val spanX = requestedSpanX.coerceIn(1, cols)
        val spanY = requestedSpanY.coerceIn(1, rows)

        // Try to place at row 0, col 0 if free, else find first fit
        for (r in 0..(rows - spanY)) {
            for (c in 0..(cols - spanX)) {
                val collides = current.filter { it.pageIndex == pageIndex }.any { item ->
                    when (item) {
                        is HomeItem.AppItem -> item.row in r until (r + spanY) && item.col in c until (c + spanX)
                        is HomeItem.FolderItem -> item.row in r until (r + spanY) && item.col in c until (c + spanX)
                        is HomeItem.WidgetItem -> {
                            val rOverlap = !(item.row + item.spanY <= r || item.row >= r + spanY)
                            val cOverlap = !(item.col + item.spanX <= c || item.col >= c + spanX)
                            rOverlap && cOverlap
                        }
                    }
                }
                if (!collides) {
                    current.add(
                        HomeItem.WidgetItem(
                            id = UUID.randomUUID().toString(),
                            type = type,
                            pageIndex = pageIndex,
                            row = r,
                            col = c,
                            spanX = spanX,
                            spanY = spanY
                        )
                    )
                    saveHomeItems(current)
                    return true
                }
            }
        }

        // Add to next page if current page has no room
        val maxPage = current.maxOfOrNull { it.pageIndex } ?: 0
        current.add(
            HomeItem.WidgetItem(
                id = UUID.randomUUID().toString(),
                type = type,
                pageIndex = maxPage + 1,
                row = 0,
                col = 0,
                spanX = spanX,
                spanY = spanY
            )
        )
        saveHomeItems(current)
        return true
    }

    fun addAppGlanceWidget(packageName: String, label: String, spanX: Int = 2, spanY: Int = 2, pageIndex: Int = 0): Boolean {
        val current = _homeItems.value.toMutableList()
        val cols = _launcherPreferences.value.gridColumns
        val rows = _launcherPreferences.value.gridRows
        val sX = spanX.coerceIn(1, cols)
        val sY = spanY.coerceIn(1, rows)

        for (r in 0..(rows - sY)) {
            for (c in 0..(cols - sX)) {
                val collides = current.filter { it.pageIndex == pageIndex }.any { item ->
                    when (item) {
                        is HomeItem.AppItem -> item.row in r until (r + sY) && item.col in c until (c + sX)
                        is HomeItem.FolderItem -> item.row in r until (r + sY) && item.col in c until (c + sX)
                        is HomeItem.WidgetItem -> {
                            val rOverlap = !(item.row + item.spanY <= r || item.row >= r + sY)
                            val cOverlap = !(item.col + item.spanX <= c || item.col >= c + sX)
                            rOverlap && cOverlap
                        }
                    }
                }
                if (!collides) {
                    current.add(
                        HomeItem.WidgetItem(
                            id = UUID.randomUUID().toString(),
                            type = WidgetType.APP_GLANCE,
                            pageIndex = pageIndex,
                            row = r,
                            col = c,
                            spanX = sX,
                            spanY = sY,
                            appPackage = packageName,
                            customTitle = label
                        )
                    )
                    saveHomeItems(current)
                    return true
                }
            }
        }

        val maxPage = current.maxOfOrNull { it.pageIndex } ?: 0
        current.add(
            HomeItem.WidgetItem(
                id = UUID.randomUUID().toString(),
                type = WidgetType.APP_GLANCE,
                pageIndex = maxPage + 1,
                row = 0,
                col = 0,
                spanX = sX,
                spanY = sY,
                appPackage = packageName,
                customTitle = label
            )
        )
        saveHomeItems(current)
        return true
    }

    fun resizeWidget(id: String, newSpanX: Int, newSpanY: Int) {
        val cols = _launcherPreferences.value.gridColumns
        val rows = _launcherPreferences.value.gridRows
        val updated = _homeItems.value.map { item ->
            if (item.id == id && item is HomeItem.WidgetItem) {
                val clampedSpanX = newSpanX.coerceIn(1, cols - item.col)
                val clampedSpanY = newSpanY.coerceIn(1, rows - item.row)
                item.copy(spanX = clampedSpanX, spanY = clampedSpanY)
            } else item
        }
        saveHomeItems(updated)
    }


    fun setDockApps(dockPackages: List<String>) {
        updatePreferences { it.copy(dockPackages = dockPackages.take(5)) }
    }

    fun toggleAppLock(packageName: String) {
        val current = _launcherPreferences.value.lockedPackages.toMutableSet()
        if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
        updatePreferences { it.copy(lockedPackages = current) }
    }

    fun toggleAppHidden(packageName: String) {
        val current = _launcherPreferences.value.hiddenPackages.toMutableSet()
        if (current.contains(packageName)) current.remove(packageName) else current.add(packageName)
        updatePreferences { it.copy(hiddenPackages = current) }
    }

    fun exportBackupJson(): String {
        val root = JSONObject()
        val prefsJson = prefs.getString("launcher_prefs_json", "{}")
        val itemsJson = prefs.getString("home_items_json", "[]")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("preferences", JSONObject(prefsJson ?: "{}"))
        root.put("homeItems", JSONArray(itemsJson ?: "[]"))
        return root.toString(2)
    }

    fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val prefsObj = root.getJSONObject("preferences")
            val itemsArr = root.getJSONArray("homeItems")
            prefs.edit()
                .putString("launcher_prefs_json", prefsObj.toString())
                .putString("home_items_json", itemsArr.toString())
                .apply()
            _launcherPreferences.value = loadPreferences()
            _homeItems.value = loadHomeItems()
            true
        } catch (e: Exception) {
            Log.e("SettingsRepository", "Error restoring backup", e)
            false
        }
    }
}
