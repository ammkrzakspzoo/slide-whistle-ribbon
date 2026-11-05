package com.glissando.whistleribbon.presentation.game

import com.glissando.whistleribbon.domain.model.MelodyPoint
import com.glissando.whistleribbon.domain.model.RoundResult
import com.glissando.whistleribbon.domain.model.SparkleNote

enum class GamePhase {
    PLAYING,
    PAUSED,
    FINISHED
}

sealed interface GameEvent {
    data class Finished(val result: RoundResult) : GameEvent
}

data class GameUiState(
    val songName: String = "",
    val phase: GamePhase = GamePhase.PLAYING,
    val points: List<MelodyPoint> = emptyList(),
    val sparkleNotes: List<SparkleNote> = emptyList(),
    val collected: Set<Int> = emptySet(),
    val positionMs: Long = 0L,
    val durationMs: Long = 1L,
    val pitch01: Float = 0.5f,
    val fingerDown: Boolean = false,
    val onPitch: Boolean = false,
    val onPitchPercent: Int = 0,
    val sparkles: Int = 0,
    val totalSparkles: Int = 0,
    val harmony: Float = 0f,
    val milestone: Int = 0,
    val practice: Boolean = false,
    val halfSpeed: Boolean = true,
    val stripOnRight: Boolean = false,
    val reducedMotion: Boolean = false,
    val showHint: Boolean = true
) {
    val songProgress: Float
        get() = if (durationMs <= 0L) 0f else (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
}
