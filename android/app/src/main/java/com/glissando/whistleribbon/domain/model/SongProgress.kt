package com.glissando.whistleribbon.domain.model

data class SongProgress(
    val songId: Int,
    val bestPercent: Int = 0,
    val sparkles: Int = 0,
    val award: Award = Award.NONE
)
