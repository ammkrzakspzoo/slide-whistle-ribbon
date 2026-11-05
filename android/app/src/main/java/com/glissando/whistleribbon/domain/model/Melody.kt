package com.glissando.whistleribbon.domain.model

data class Melody(
    val points: List<MelodyPoint> = emptyList(),
    val sparkles: List<SparkleNote> = emptyList(),
    val durationMs: Long = 0L
)
