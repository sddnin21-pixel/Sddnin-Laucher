package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Launcher Accent Colors
val AccentBlue = Color(0xFF3B82F6)
val AccentPurple = Color(0xFF8B5CF6)
val AccentEmerald = Color(0xFF10B981)
val AccentOrange = Color(0xFFF97316)
val AccentPink = Color(0xFFEC4899)
val AccentCyan = Color(0xFF06B6D4)
val AccentAmber = Color(0xFFF59E0B)

val AccentColorsList = listOf(
    Pair("Xanh lam", "#3B82F6"),
    Pair("Tím Neon", "#8B5CF6"),
    Pair("Ngọc lục bảo", "#10B981"),
    Pair("Cam hoàng hôn", "#F97316"),
    Pair("Hồng thạch anh", "#EC4899"),
    Pair("Xanh Cyan", "#06B6D4"),
    Pair("Vàng hổ phách", "#F59E0B")
)

// Dynamic Wallpaper Gradient Presets
val WallpaperPresets = listOf(
    // 0: Deep Obsidian Space
    Pair("Vũ trụ huyền bí", Brush.verticalGradient(
        listOf(
            Color(0xFF0B0F19),
            Color(0xFF111827),
            Color(0xFF1E1B4B),
            Color(0xFF0F172A)
        )
    )),
    // 1: Cyberpunk Neon Sunset
    Pair("Cyberpunk Hoàng hôn", Brush.linearGradient(
        listOf(
            Color(0xFF180B2B),
            Color(0xFF2E1065),
            Color(0xFF4C1D95),
            Color(0xFF831843),
            Color(0xFF0F172A)
        )
    )),
    // 2: Emerald Dark Forest
    Pair("Rừng ngọc lục bảo", Brush.verticalGradient(
        listOf(
            Color(0xFF022C22),
            Color(0xFF064E3B),
            Color(0xFF065F46),
            Color(0xFF021612)
        )
    )),
    // 3: Minimalist Slate Night
    Pair("Đá đen tối giản", Brush.verticalGradient(
        listOf(
            Color(0xFF1E293B),
            Color(0xFF0F172A),
            Color(0xFF020617)
        )
    )),
    // 4: Sunset Dusk Glow
    Pair("Ánh rực hoàng hôn", Brush.verticalGradient(
        listOf(
            Color(0xFF450A0A),
            Color(0xFF7C2D12),
            Color(0xFF581C87),
            Color(0xFF0F172A)
        )
    )),
    // 5: Pure AMOLED Midnight
    Pair("AMOLED Đen tuyền", Brush.verticalGradient(
        listOf(
            Color(0xFF000000),
            Color(0xFF050505),
            Color(0xFF000000)
        )
    ))
)
