package com.glissando.whistleribbon.presentation.menu

import com.glissando.whistleribbon.core.config.GameConfig

data class MenuSongItem(
    val id: Int,
    val name: String,
    val difficulty: Int,
    val earned: Boolean,
    val locked: Boolean,
    val bestPercent: Int
)

data class MenuUiState(
    val nextSongId: Int = 1,
    val nextSongName: String = "",
    val goldenWhistles: Int = 0,
    val totalSongs: Int = GameConfig.TOTAL_SONGS,
    val bestPercent: Int = 0,
    val sparkles: Int = 0,
    val reducedMotion: Boolean = false,
    val songs: List<MenuSongItem> = emptyList()
) {
    val whistleProgress: Float
        get() = if (totalSongs <= 0) 0f else goldenWhistles.toFloat() / totalSongs
}
