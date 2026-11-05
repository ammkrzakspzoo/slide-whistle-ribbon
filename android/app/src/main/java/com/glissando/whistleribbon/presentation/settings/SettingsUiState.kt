package com.glissando.whistleribbon.presentation.settings

data class SettingsUiState(
    val audioOffsetMs: Int = 0,
    val volume: Int = 80,
    val stripOnRight: Boolean = false,
    val reducedMotion: Boolean = false,
    val confirmReset: Boolean = false
)
