package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundFxHelper(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playTapSound(enabled: Boolean, pitchMultiplier: Float = 1.0f) {
        if (!enabled) return
        scope.launch {
            try {
                val sampleRate = 22050
                val durationSamples = (sampleRate * 0.035).toInt()
                val buffer = ShortArray(durationSamples)
                val freq = 880.0 * pitchMultiplier
                for (i in 0 until durationSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 95.0)
                    val sample = sin(2.0 * PI * freq * t) * env * 0.35
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, buffer.size)
                track.play()
                kotlinx.coroutines.delay(60)
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    @Suppress("DEPRECATION")
    fun triggerHaptic(enabled: Boolean, strong: Boolean = false) {
        if (!enabled) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            if (strong) 28L else 12L,
                            if (strong) 180 else 80
                        )
                    )
                } else {
                    vibrator.vibrate(if (strong) 28L else 12L)
                }
            }
        } catch (_: Exception) {
        }
    }
}
