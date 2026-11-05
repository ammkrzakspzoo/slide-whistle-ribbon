package com.glissando.whistleribbon.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val songId: Int, val practice: Boolean) : Screen
    data class GameOver(
        val songId: Int,
        val onPitchPercent: Int,
        val sparkles: Int,
        val harmonyPercent: Int,
        val awardOrdinal: Int
    ) : Screen
    data object Practice : Screen
    data object WhistleCase : Screen
    data object FreeSlide : Screen
    data object Settings : Screen
}
