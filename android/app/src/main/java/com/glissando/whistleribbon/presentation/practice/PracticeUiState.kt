package com.glissando.whistleribbon.presentation.practice

data class PracticeSongItem(
    val id: Int,
    val name: String,
    val difficulty: Int,
    val bestPercent: Int
)

data class PracticeUiState(
    val songs: List<PracticeSongItem> = emptyList(),
    val selectedId: Int = 1,
    val halfSpeed: Boolean = true,
    val loopSection: Boolean = false,
    val sectionStart: Float = 0.2f,
    val sectionEnd: Float = 0.7f
)
