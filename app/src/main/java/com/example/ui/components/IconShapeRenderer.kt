package com.example.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorFilter
import com.example.data.model.AppCategory
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape

fun getIconShape(shape: IconShape): Shape {
    return when (shape) {
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(16.dp)
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(22.dp)
        IconShape.TEARDROP -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
        IconShape.HEXAGON -> GenericShape { size, _ ->
            val w = size.width
            val h = size.height
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.25f)
            lineTo(w, h * 0.75f)
            lineTo(w * 0.5f, h)
            lineTo(0f, h * 0.75f)
            lineTo(0f, h * 0.25f)
            close()
        }
    }
}

@Composable
fun AppIconView(
    app: AppInfo,
    iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    size: Dp = 54.dp,
    showBadge: Boolean = true,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    modifier: Modifier = Modifier
) {
    val shape = remember(iconShape) { getIconShape(iconShape) }
    val borderModifier = when (iconPackStyle) {
        IconPackStyle.DEFAULT -> Modifier
        IconPackStyle.NEON_GLOW -> Modifier.border(1.8.dp, Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFFA855F7))), shape)
        IconPackStyle.PASTEL_RING -> Modifier.border(2.dp, Color(0xFFF9A8D4).copy(alpha = 0.85f), shape)
        IconPackStyle.GLASS_FROST -> Modifier.border(1.2.dp, Color.White.copy(alpha = 0.55f), shape)
        IconPackStyle.CYBER_MONO -> Modifier.border(1.5.dp, Color(0xFF22D3EE), shape)
    }
    val monoFilter = remember(iconPackStyle) {
        if (iconPackStyle == IconPackStyle.CYBER_MONO) {
            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.15f) })
        } else null
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // App icon background & image
        Box(
            modifier = Modifier
                .size(size)
                .shadow(elevation = if (iconPackStyle == IconPackStyle.NEON_GLOW) 8.dp else 3.dp, shape = shape)
                .clip(shape)
                .background(
                    when {
                        iconPackStyle == IconPackStyle.GLASS_FROST ->
                            Brush.verticalGradient(listOf(Color(0x55FFFFFF), Color(0x220F172A)))
                        app.iconDrawable == null -> getFallbackGradient(app)
                        else -> Brush.verticalGradient(listOf(Color(0xFF26262B), Color(0xFF1E1E22)))
                    }
                )
                .then(borderModifier),
            contentAlignment = Alignment.Center
        ) {
            if (app.iconDrawable != null) {
                AsyncImage(
                    model = app.iconDrawable,
                    contentDescription = app.displayLabel,
                    colorFilter = monoFilter,
                    modifier = Modifier.size(size * 0.88f)
                )
            } else {
                FallbackAppIcon(app = app, size = size)
            }
        }

        // Lock icon badge if app is locked with PIN
        if (app.isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE11D48))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        // Notification badge
        if (showBadge && app.notificationCount > 0 && !app.isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-2).dp)
                    .size(if (app.notificationCount > 9) 20.dp else 17.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (app.notificationCount > 99) "99+" else app.notificationCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FallbackAppIcon(app: AppInfo, size: Dp) {
    val pkg = app.packageName.lowercase()
    val vector: ImageVector = when {
        pkg.contains("phone") || pkg.contains("dialer") -> Icons.Default.Phone
        pkg.contains("settings") -> Icons.Default.Settings
        pkg.contains("chat") || pkg.contains("zalo") || pkg.contains("message") -> Icons.Default.Chat
        pkg.contains("game") || pkg.contains("play") -> Icons.Default.SportsEsports
        pkg.contains("work") || pkg.contains("mail") || pkg.contains("office") -> Icons.Default.Work
        pkg.contains("movie") || pkg.contains("video") || pkg.contains("youtube") -> Icons.Default.Movie
        else -> Icons.Default.Build
    }

    if (app.displayLabel.isNotBlank()) {
        Text(
            text = app.displayLabel.take(1).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp
        )
    } else {
        Icon(
            imageVector = vector,
            contentDescription = app.displayLabel,
            tint = Color.White,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

private fun getFallbackGradient(app: AppInfo): Brush {
    val hash = kotlin.math.abs(app.packageName.hashCode())
    val palettes = listOf(
        listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
        listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
        listOf(Color(0xFFEC4899), Color(0xFFBE185D)),
        listOf(Color(0xFF10B981), Color(0xFF047857)),
        listOf(Color(0xFFF97316), Color(0xFFC2410C)),
        listOf(Color(0xFF06B6D4), Color(0xFF0E7490)),
        listOf(Color(0xFFEAB308), Color(0xFFA16207))
    )
    val chosen = palettes[hash % palettes.size]
    return Brush.linearGradient(chosen)
}
