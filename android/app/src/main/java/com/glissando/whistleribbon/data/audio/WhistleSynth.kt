package com.glissando.whistleribbon.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.domain.model.WhistleTone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

class WhistleSynth {

    private var track: AudioTrack? = null
    private val block = ShortArray(BLOCK_FRAMES)

    private var phase = 0.0
    private var groovePhase = 0.0
    private var frequency = 440.0
    private var amplitude = 0.0
    private var grooveStep = 0
    private var grooveFrames = 0
    private var bellFrames = 0

    @Volatile private var targetFrequency = 440.0
    @Volatile private var targetAmplitude = 0.0
    @Volatile private var tone = WhistleTone.SLIDE
    @Volatile private var master = 0.8
    @Volatile private var groovePattern = -1

    fun open(): Boolean {
        if (track != null) return true
        val minBytes = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBytes <= 0) return false
        val created = runCatching {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(maxOf(minBytes, BLOCK_FRAMES * 4))
                .build()
        }.getOrNull() ?: return false
        if (created.state != AudioTrack.STATE_INITIALIZED) {
            runCatching { created.release() }
            return false
        }
        runCatching { created.play() }
        track = created
        return true
    }

    fun renderBlock(): Boolean {
        val active = track ?: return false
        fill()
        val written = runCatching {
            active.write(block, 0, block.size, AudioTrack.WRITE_BLOCKING)
        }.getOrDefault(-1)
        return written >= 0
    }

    fun close() {
        val active = track ?: return
        track = null
        runCatching { active.pause() }
        runCatching { active.flush() }
        runCatching { active.release() }
        amplitude = 0.0
        phase = 0.0
    }

    fun setPitch(pitch01: Float) {
        val clamped = pitch01.coerceIn(0f, 1f)
        targetFrequency = GameConfig.BASE_FREQUENCY.toDouble() *
            2.0.pow(clamped.toDouble() * GameConfig.PITCH_RANGE_OCTAVES)
    }

    fun setBlowing(blowing: Boolean) {
        targetAmplitude = if (blowing) 0.82 else 0.0
    }

    fun setTone(value: WhistleTone) {
        tone = value
    }

    fun setVolume(percent: Int) {
        master = (percent.coerceIn(0, 100) / 100.0) * 0.9
    }

    fun setGroove(pattern: Int) {
        groovePattern = pattern
    }

    fun sparkle() {
        bellFrames = SAMPLE_RATE / 6
    }

    private fun fill() {
        val shape = tone
        val slew = GameConfig.PITCH_SMOOTHING.toDouble() / 8.0
        for (i in block.indices) {
            frequency += (targetFrequency - frequency) * slew
            amplitude += (targetAmplitude - amplitude) * 0.0025
            phase += TWO_PI * frequency / SAMPLE_RATE
            if (phase > TWO_PI) phase -= TWO_PI
            val fundamental = sin(phase)
            val second = sin(phase * 2.0) * shape.secondHarmonic
            val third = sin(phase * 3.0) * shape.thirdHarmonic
            val triangle = (2.0 * abs(2.0 * (phase / TWO_PI) - 1.0) - 1.0) * shape.breath
            var sample = (fundamental + second + third + triangle) * amplitude * 0.5
            sample += bell()
            sample += groove()
            val scaled = (sample * master * Short.MAX_VALUE).coerceIn(
                -Short.MAX_VALUE.toDouble(),
                Short.MAX_VALUE.toDouble()
            )
            block[i] = scaled.toInt().toShort()
        }
    }

    private fun bell(): Double {
        if (bellFrames <= 0) return 0.0
        val life = bellFrames.toDouble() / (SAMPLE_RATE / 6.0)
        bellFrames--
        val bellPhase = TWO_PI * frequency * 3.0 * (bellFrames.toDouble() / SAMPLE_RATE)
        return sin(bellPhase) * life * life * 0.18
    }

    private fun groove(): Double {
        val pattern = groovePattern
        if (pattern < 0) return 0.0
        val steps = GROOVE_PATTERNS[pattern % GROOVE_PATTERNS.size]
        grooveFrames++
        if (grooveFrames >= STEP_FRAMES) {
            grooveFrames = 0
            grooveStep = (grooveStep + 1) % steps.size
            groovePhase = 0.0
        }
        val semitone = steps[grooveStep]
        if (semitone < 0) return 0.0
        val freq = 110.0 * 2.0.pow(semitone / 12.0)
        groovePhase += TWO_PI * freq / SAMPLE_RATE
        if (groovePhase > TWO_PI) groovePhase -= TWO_PI
        val envelope = 1.0 - (grooveFrames.toDouble() / STEP_FRAMES)
        return sin(groovePhase) * envelope * envelope * 0.16
    }

    private companion object {
        const val SAMPLE_RATE = 44100
        const val BLOCK_FRAMES = 1024
        const val TWO_PI = 2.0 * PI
        const val STEP_FRAMES = 44100 / 4
        val GROOVE_PATTERNS = listOf(
            intArrayOf(0, -1, 7, -1, 5, -1, 7, -1),
            intArrayOf(0, 12, 7, 12, 5, 12, 3, 12),
            intArrayOf(0, -1, -1, 5, -1, 7, -1, 10),
            intArrayOf(0, 3, 7, 10, 12, 10, 7, 3)
        )
    }
}
