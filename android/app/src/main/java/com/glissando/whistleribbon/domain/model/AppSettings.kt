package com.glissando.whistleribbon.domain.model

data class AppSettings(
    val audioOffsetMs: Int = 0,
    val volume: Int = 80,
    val stripOnRight: Boolean = false,
    val reducedMotion: Boolean = false,
    val halfSpeed: Boolean = true,
    val whistleTone: WhistleTone = WhistleTone.SLIDE
)
