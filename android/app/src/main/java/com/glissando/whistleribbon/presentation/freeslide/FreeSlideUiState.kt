package com.glissando.whistleribbon.presentation.freeslide

data class FreeSlideUiState(
    val pitch01: Float = 0.5f,
    val fingerDown: Boolean = false,
    val groovePattern: Int = 0,
    val grooveOn: Boolean = false,
    val keySnap: Boolean = true,
    val stripOnRight: Boolean = false,
    val reducedMotion: Boolean = false
)
