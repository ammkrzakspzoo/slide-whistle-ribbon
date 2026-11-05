package com.glissando.whistleribbon.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.data.audio.WhistleSynth
import com.glissando.whistleribbon.domain.model.Melody
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import com.glissando.whistleribbon.domain.repository.SongRepository
import com.glissando.whistleribbon.domain.usecase.EvaluatePitchUseCase
import com.glissando.whistleribbon.domain.usecase.GenerateMelodyUseCase
import com.glissando.whistleribbon.domain.usecase.ScoreRoundUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameViewModel(
    private val songId: Int,
    private val practice: Boolean,
    private val songs: SongRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val generateMelody: GenerateMelodyUseCase,
    private val evaluatePitch: EvaluatePitchUseCase,
    private val scoreRound: ScoreRoundUseCase
) : ViewModel() {

    private val state = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val synth = WhistleSynth()
    private var loopJob: Job? = null
    private var audioJob: Job? = null

    private var melody: Melody = Melody()
    private var positionMs = 0L
    private var totalFrames = 0
    private var onPitchFrames = 0
    private var idleMs = 0L
    private var targetPitch = 0.5f
    private var shownPitch = 0.5f
    private var fingerDown = false
    private var harmony = 0f
    private var collected = mutableSetOf<Int>()
    private var over = false

    init {
        val song = songs.song(songId)
        val current = settings.settings.value
        melody = generateMelody(song, GameConfig.SONG_LENGTH_MS)
        shownPitch = melody.points.firstOrNull()?.pitch01 ?: 0.5f
        targetPitch = shownPitch
        state.value = GameUiState(
            songName = song.name,
            phase = GamePhase.PLAYING,
            points = melody.points,
            sparkleNotes = melody.sparkles,
            durationMs = melody.durationMs,
            pitch01 = shownPitch,
            totalSparkles = melody.sparkles.size,
            practice = practice,
            halfSpeed = current.halfSpeed,
            stripOnRight = current.stripOnRight,
            reducedMotion = current.reducedMotion
        )
        startAudio()
        startLoop()
    }

    fun onPitchChange(value: Float) {
        fingerDown = true
        targetPitch = value.coerceIn(0f, 1f)
        synth.setBlowing(true)
        if (state.value.showHint) {
            state.update { it.copy(showHint = false) }
        }
    }

    fun onRelease() {
        fingerDown = false
        synth.setBlowing(false)
    }

    fun pause() {
        if (over) return
        loopJob?.cancel()
        loopJob = null
        fingerDown = false
        synth.setBlowing(false)
        if (state.value.phase == GamePhase.PLAYING) {
            state.update { it.copy(phase = GamePhase.PAUSED, fingerDown = false) }
        }
    }

    fun resume() {
        if (over || loopJob != null) return
        state.update { it.copy(phase = GamePhase.PLAYING) }
        startLoop()
    }

    private fun startAudio() {
        audioJob = viewModelScope.launch(Dispatchers.Default) {
            val current = settings.settings.value
            synth.setVolume(current.volume)
            synth.setTone(current.whistleTone)
            if (!synth.open()) return@launch
            try {
                while (isActive && synth.renderBlock()) {
                    continue
                }
            } finally {
                synth.close()
            }
        }
    }

    private fun startLoop() {
        loopJob = viewModelScope.launch {
            var previous = SystemClock.elapsedRealtime()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = SystemClock.elapsedRealtime()
                val delta = (now - previous).coerceIn(0L, 64L)
                previous = now
                tick(delta)
            }
        }
    }

    private fun tick(deltaMs: Long) {
        if (over) return
        val current = settings.settings.value
        val rate = if (practice && current.halfSpeed) 0.5f else 1f
        positionMs += (deltaMs * rate).toLong()

        val probe = (positionMs + current.audioOffsetMs).coerceAtLeast(0L)
        val target = evaluatePitch.pitchAt(melody, probe)
        shownPitch += (targetPitch - shownPitch) * GameConfig.PITCH_SMOOTHING
        synth.setPitch(shownPitch)

        val onPitch = fingerDown &&
            evaluatePitch.isOnPitch(target, shownPitch, GameConfig.PITCH_TOLERANCE)
        totalFrames++
        if (onPitch) {
            onPitchFrames++
            harmony = (harmony + GameConfig.HARMONY_STEP).coerceAtMost(1f)
        }

        var gained = false
        if (fingerDown) {
            melody.sparkles.forEach { note ->
                if (!collected.contains(note.index) &&
                    abs(note.timeMs - positionMs) < 130L &&
                    abs(note.pitch01 - shownPitch) <= GameConfig.SPARKLE_TOLERANCE
                ) {
                    collected.add(note.index)
                    gained = true
                }
            }
        }
        if (gained) synth.sparkle()

        idleMs = if (fingerDown) 0L else idleMs + deltaMs

        val percent = if (totalFrames <= 0) 0 else (onPitchFrames * 100) / totalFrames
        val milestone = (harmony * 4f).toInt().coerceIn(0, 4)
        state.update {
            it.copy(
                positionMs = positionMs,
                pitch01 = shownPitch,
                fingerDown = fingerDown,
                onPitch = onPitch,
                onPitchPercent = percent,
                sparkles = collected.size,
                collected = collected.toSet(),
                harmony = harmony,
                milestone = milestone
            )
        }

        if (positionMs >= melody.durationMs || idleMs >= GameConfig.IDLE_END_MS) {
            finishRound()
        }
    }

    private fun finishRound() {
        if (over) return
        over = true
        loopJob?.cancel()
        loopJob = null
        fingerDown = false
        synth.setBlowing(false)
        state.update { it.copy(phase = GamePhase.FINISHED, fingerDown = false) }
        val result = scoreRound(
            songId = songId,
            practice = practice,
            onPitchFrames = onPitchFrames,
            totalFrames = totalFrames,
            sparkles = collected.size,
            totalSparkles = melody.sparkles.size,
            harmony = harmony,
            goldThreshold = GameConfig.GOLD_THRESHOLD,
            silverThreshold = GameConfig.SILVER_THRESHOLD,
            bronzeThreshold = GameConfig.BRONZE_THRESHOLD
        )
        progress.save(result)
        viewModelScope.launch {
            eventChannel.send(GameEvent.Finished(result))
        }
    }

    override fun onCleared() {
        loopJob?.cancel()
        audioJob?.cancel()
        synth.setBlowing(false)
        super.onCleared()
    }
}
