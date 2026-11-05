package com.glissando.whistleribbon.presentation.gameover

import com.glissando.whistleribbon.domain.model.Award

data class GameOverUiState(
    val songId: Int = 1,
    val songName: String = "",
    val onPitchPercent: Int = 0,
    val sparkles: Int = 0,
    val harmonyPercent: Int = 0,
    val bestPercent: Int = 0,
    val goldenWhistles: Int = 0,
    val award: Award = Award.NONE
)
