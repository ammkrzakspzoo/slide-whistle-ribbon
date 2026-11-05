package com.glissando.whistleribbon.domain.model

data class RoundResult(
    val songId: Int,
    val practice: Boolean,
    val onPitchPercent: Int,
    val sparkles: Int,
    val totalSparkles: Int,
    val harmonyPercent: Int,
    val award: Award
)
