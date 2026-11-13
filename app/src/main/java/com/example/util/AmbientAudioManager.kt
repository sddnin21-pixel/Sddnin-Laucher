package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Ambient Audio Synthesizer.
 * Generates beautiful, calming background sounds (Lofi, Rain/Nature, Cosmic Drone, Piano)
 * completely offline with zero external assets using Android's native AudioTrack.
 */
class AmbientAudioManager(private val context: Context) {

    @Volatile
    private var audioTrack: AudioTrack? = null
    private var synthesisJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var isPlaying = false
    private var currentPreset = "LOFI"
    private var currentVolume = 0.5f

    private val sampleRate = 22050

    @Synchronized
    fun start(preset: String = currentPreset, volume: Float = currentVolume) {
        currentPreset = preset
        currentVolume = volume.coerceIn(0f, 1f)
        stop()

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(sampleRate / 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.setVolume(currentVolume)
            track.play()
            audioTrack = track
            isPlaying = true

            synthesisJob = scope.launch {
                generateAudioLoop(track, preset, bufferSize / 2)
            }
        } catch (e: Exception) {
            Log.e("AmbientAudio", "Error starting audio synthesis", e)
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(currentVolume)
        } catch (_: Exception) {
        }
    }

    @Synchronized
    fun stop() {
        isPlaying = false
        synthesisJob?.cancel()
        synthesisJob = null
        val track = audioTrack
        audioTrack = null
        try {
            track?.pause()
            track?.flush()
            track?.release()
        } catch (_: Exception) {
        }
    }

    fun pause() {
        try {
            audioTrack?.pause()
        } catch (e: Exception) {
            // Ignored
        }
    }

    fun resume() {
        if (isPlaying) {
            try {
                audioTrack?.play()
            } catch (e: Exception) {
                // If failed, re-start
                start(currentPreset, currentVolume)
            }
        }
    }

    private suspend fun generateAudioLoop(track: AudioTrack, preset: String, chunkSize: Int) {
        val buffer = ShortArray(chunkSize)
        var phase1 = 0.0
        var phase2 = 0.0
        var phase3 = 0.0
        var tick = 0L

        // Frequencies for soothing chords (Lofi: C4, E4, G4, B4, A3)
        val lofiChords = listOf(
            doubleArrayOf(261.63, 329.63, 392.00, 493.88), // Cmaj7
            doubleArrayOf(220.00, 261.63, 329.63, 392.00), // Am7
            doubleArrayOf(174.61, 220.00, 261.63, 329.63), // Fmaj7
            doubleArrayOf(196.00, 246.94, 293.66, 392.00)  // G7
        )

        var chordIndex = 0
        var chordTimer = 0

        while (scope.isActive && isPlaying && audioTrack === track) {
            for (i in 0 until chunkSize) {
                val sample: Double = when (preset) {
                    "RAIN" -> {
                        // Gentle pink noise + random rain droplet plops
                        val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.12
                        val drop = if (Random.nextInt(450) == 0) {
                            sin(tick * 0.15) * 0.4
                        } else 0.0
                        (noise + drop)
                    }
                    "COSMIC" -> {
                        // Deep harmonic drone (55Hz, 110Hz, 164.8Hz)
                        phase1 += (2 * PI * 55.0) / sampleRate
                        phase2 += (2 * PI * 110.0) / sampleRate
                        phase3 += (2 * PI * 164.81) / sampleRate
                        val wave = (sin(phase1) * 0.35 + sin(phase2) * 0.2 + sin(phase3) * 0.15)
                        wave * 0.6
                    }
                    "PIANO" -> {
                        // Soft slow ambient piano tones
                        if (tick % (sampleRate * 2) == 0L) {
                            chordIndex = (chordIndex + 1) % lofiChords.size
                        }
                        val currentNotes = lofiChords[chordIndex]
                        var mix = 0.0
                        for (note in currentNotes) {
                            val p = (tick * 2 * PI * note) / sampleRate
                            val env = exp(-((tick % (sampleRate * 2)).toDouble() / (sampleRate * 1.2)))
                            mix += sin(p) * env * 0.15
                        }
                        mix
                    }
                    else -> { // "LOFI"
                        chordTimer++
                        if (chordTimer >= sampleRate * 3) {
                            chordTimer = 0
                            chordIndex = (chordIndex + 1) % lofiChords.size
                        }
                        val notes = lofiChords[chordIndex]
                        var chordMix = 0.0
                        for (note in notes) {
                            val p = (tick * 2 * PI * note) / sampleRate
                            chordMix += sin(p) * 0.08
                        }
                        // Gentle warm vibrato
                        val vibrato = sin(tick * 2 * PI * 4.0 / sampleRate) * 0.02
                        val vinylCrack = if (Random.nextInt(1200) == 0) (Random.nextDouble() - 0.5) * 0.15 else 0.0
                        (chordMix + vibrato + vinylCrack) * 0.7
                    }
                }

                tick++
                buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
            }

            try {
                if (!isPlaying || audioTrack !== track) break
                track.write(buffer, 0, chunkSize)
            } catch (_: Throwable) {
                break
            }
        }
    }
}
