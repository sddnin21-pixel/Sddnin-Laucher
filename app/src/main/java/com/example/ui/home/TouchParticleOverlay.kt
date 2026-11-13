package com.example.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.data.model.TouchParticleEffect
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class AmbientParticle(
    var xRatio: Float,
    var yRatio: Float,
    var speedY: Float,
    var driftX: Float,
    var radius: Float,
    var alpha: Float,
    var color: Color
)

/**
 * Renders interactive ambient falling/floating particles (Sakura, Snow, Neon Sparks, Starlight)
 * and the optional Blue Light Night Reading Filter overlay.
 */
@Composable
fun AmbientEffectsAndBlueFilterOverlay(
    effect: TouchParticleEffect,
    blueLightIntensity: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (effect != TouchParticleEffect.NONE) {
            val particles = remember(effect) {
                List(28) { idx ->
                    val palette = when (effect) {
                        TouchParticleEffect.NEON_SPARKS -> listOf(Color(0xFF38BDF8), Color(0xFFA855F7), Color(0xFF10B981))
                        TouchParticleEffect.SAKURA_PETALS -> listOf(Color(0xFFF9A8D4), Color(0xFFF472B6), Color(0xFFFCE7F3))
                        TouchParticleEffect.SNOW_FALL -> listOf(Color.White, Color(0xFFE0F2FE))
                        TouchParticleEffect.STARLIGHT -> listOf(Color(0xFFFDE047), Color(0xFF93C5FD), Color.White)
                        TouchParticleEffect.NONE -> listOf(Color.Transparent)
                    }
                    AmbientParticle(
                        xRatio = Random.nextFloat(),
                        yRatio = Random.nextFloat(),
                        speedY = if (effect == TouchParticleEffect.STARLIGHT) 0.0008f + Random.nextFloat() * 0.001f else 0.0015f + Random.nextFloat() * 0.0025f,
                        driftX = (Random.nextFloat() - 0.5f) * 0.0012f,
                        radius = when (effect) {
                            TouchParticleEffect.SNOW_FALL -> 3f + Random.nextFloat() * 4f
                            TouchParticleEffect.SAKURA_PETALS -> 5f + Random.nextFloat() * 4f
                            else -> 2.5f + Random.nextFloat() * 3.5f
                        },
                        alpha = 0.35f + Random.nextFloat() * 0.45f,
                        color = palette[idx % palette.size]
                    )
                }
            }

            var tick by remember { mutableLongStateOf(0L) }
            LaunchedEffect(effect) {
                while (true) {
                    delay(32L)
                    particles.forEach { p ->
                        p.yRatio += p.speedY
                        p.xRatio += p.driftX + sin(tick * 0.05f + p.yRatio * 8f).toFloat() * 0.0005f
                        if (p.yRatio > 1.05f) {
                            p.yRatio = -0.05f
                            p.xRatio = Random.nextFloat()
                        }
                        if (p.xRatio < -0.05f) p.xRatio = 1.05f
                        if (p.xRatio > 1.05f) p.xRatio = -0.05f
                    }
                    tick++
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val t = tick
                particles.forEachIndexed { idx, p ->
                    val cx = p.xRatio * size.width
                    val cy = p.yRatio * size.height
                    val twinkle = if (effect == TouchParticleEffect.STARLIGHT || effect == TouchParticleEffect.NEON_SPARKS) {
                        (0.4f + 0.6f * kotlin.math.abs(cos(t * 0.08 + idx).toFloat()))
                    } else 1f
                    drawCircle(
                        color = p.color.copy(alpha = (p.alpha * twinkle).coerceIn(0.1f, 0.9f)),
                        radius = p.radius,
                        center = Offset(cx, cy)
                    )
                }
            }
        }

        // Feature #21: Blue Light Night Reading Filter
        if (blueLightIntensity > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF9800).copy(alpha = blueLightIntensity.coerceIn(0f, 0.45f)))
            )
        }
    }
}
