package com.glissando.whistleribbon.domain.model

data class Song(
    val id: Int,
    val name: String,
    val bpm: Int,
    val seed: Int,
    val difficulty: Int,
    val sparkleCount: Int
)
