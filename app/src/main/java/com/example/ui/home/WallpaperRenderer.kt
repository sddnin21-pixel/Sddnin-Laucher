package com.example.ui.home

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LauncherPreferences
import com.example.ui.theme.WallpaperPresets
import kotlin.math.sin

@Composable
fun WallpaperRenderer(
    preferences: LauncherPreferences,
    modifier: Modifier = Modifier
) {
    if (preferences.ultraBatterySaver) {
        Box(modifier = modifier.fillMaxSize().background(Color.Black))
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (preferences.wallpaperType) {
            "CUSTOM_IMAGE" -> {
                if (!preferences.customWallpaperUri.isNullOrBlank()) {
                    AsyncImage(
                        model = preferences.customWallpaperUri,
                        contentDescription = "Custom Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback to sloth image
                    Image(
                        painter = painterResource(id = R.drawable.wallpaper_sloth),
                        contentDescription = "Sloth Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            "VIDEO" -> {
                if (!preferences.customWallpaperUri.isNullOrBlank()) {
                    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                tag = preferences.customWallpaperUri
                                setVideoURI(Uri.parse(preferences.customWallpaperUri))
                                setOnErrorListener { _, _, _ -> true }
                                setOnPreparedListener { mp ->
                                    mediaPlayerRef = mp
                                    mp.isLooping = true
                                    val vol = if (preferences.videoSoundEnabled) 1f else 0f
                                    mp.setVolume(vol, vol)
                                    start()
                                }
                            }
                        },
                        update = { view ->
                            val uriStr = preferences.customWallpaperUri
                            if (view.tag != uriStr && !uriStr.isNullOrBlank()) {
                                view.tag = uriStr
                                view.setVideoURI(Uri.parse(uriStr))
                                view.start()
                            }
                            try {
                                val vol = if (preferences.videoSoundEnabled) 1f else 0f
                                mediaPlayerRef?.setVolume(vol, vol)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Procedural Live Video/Animated Wallpaper (Aurora & Starfield)
                    LiveAnimatedWallpaper(modifier = Modifier.fillMaxSize())
                }
            }

            "GRADIENT" -> {
                val idx = preferences.wallpaperIndex.coerceIn(0, WallpaperPresets.size - 1)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WallpaperPresets[idx].second)
                )
            }

            else -> { // "PRESET_IMAGE"
                when (preferences.presetImageId) {
                    "sloth" -> {
                        Image(
                            painter = painterResource(id = R.drawable.wallpaper_sloth),
                            contentDescription = "Chú lười trên cành cây",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "nature" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF0F172A))
                                    )
                                )
                        )
                    }
                    "cyber" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF311042), Color(0xFF1E1B4B), Color(0xFF090D16))
                                    )
                                )
                        )
                    }
                    "sunset" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF7C2D12), Color(0xFF581C87), Color(0xFF0F172A))
                                    )
                                )
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.wallpaper_sloth),
                            contentDescription = "Sloth Wallpaper",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Ambient Animated Particles / Fireflies Overlay (if enabled)
        if (preferences.liveAnimationEnabled && !preferences.ultraBatterySaver) {
            AmbientParticleOverlay(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LiveAnimatedWallpaper(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aurora_phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background dark gradient
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF050B14), Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF050811))
            )
        )

        // Flowing Aurora light bands
        val rad = Math.toRadians(animOffset.toDouble())
        val centerY1 = h * 0.35f + sin(rad).toFloat() * 60f
        val centerY2 = h * 0.65f + sin(rad + 1.5).toFloat() * 80f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x5506B6D4), Color(0x223B82F6), Color.Transparent),
                center = Offset(w * 0.3f, centerY1),
                radius = w * 0.8f
            ),
            center = Offset(w * 0.3f, centerY1),
            radius = w * 0.8f
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x448B5CF6), Color(0x18EC4899), Color.Transparent),
                center = Offset(w * 0.7f, centerY2),
                radius = w * 0.9f
            ),
            center = Offset(w * 0.7f, centerY2),
            radius = w * 0.9f
        )
    }
}

@Composable
fun AmbientParticleOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles_progress"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 16 gentle floating light orbs / fireflies
        for (i in 0 until 16) {
            val seed = i * 47.3f
            val startX = (sin(seed) * 0.5f + 0.5f) * w
            val speed = 0.6f + (i % 5) * 0.15f
            val yProgress = (progress * speed + (i * 0.08f)) % 1f
            val currentY = h - (yProgress * h)
            val currentX = startX + sin((progress * 4f + seed).toDouble()).toFloat() * 25f

            val alpha = (sin(yProgress * Math.PI).toFloat() * 0.45f).coerceIn(0f, 0.6f)
            val radius = 2.5.dp.toPx() + (i % 3) * 1.5.dp.toPx()

            drawCircle(
                color = Color(0xFFFDE047).copy(alpha = alpha),
                center = Offset(currentX, currentY),
                radius = radius
            )
        }
    }
}
