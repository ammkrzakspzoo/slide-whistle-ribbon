package com.glissando.whistleribbon.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glissando.whistleribbon.presentation.app.AppViewModel
import com.glissando.whistleribbon.presentation.freeslide.FreeSlideViewModel
import com.glissando.whistleribbon.presentation.game.GameViewModel
import com.glissando.whistleribbon.presentation.gameover.GameOverViewModel
import com.glissando.whistleribbon.presentation.menu.MenuViewModel
import com.glissando.whistleribbon.presentation.practice.PracticeViewModel
import com.glissando.whistleribbon.presentation.settings.SettingsViewModel
import com.glissando.whistleribbon.presentation.splash.SplashViewModel
import com.glissando.whistleribbon.presentation.whistlecase.WhistleCaseViewModel

class AppViewModelFactory(
    private val songId: Int = 1,
    private val practice: Boolean = false,
    private val onPitchPercent: Int = 0,
    private val sparkles: Int = 0,
    private val harmonyPercent: Int = 0,
    private val awardOrdinal: Int = 0
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                songs = ServiceLocator.songs,
                progress = ServiceLocator.progress,
                settings = ServiceLocator.appSettings
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                songId = songId,
                practice = practice,
                songs = ServiceLocator.songs,
                progress = ServiceLocator.progress,
                settings = ServiceLocator.appSettings,
                generateMelody = ServiceLocator.generateMelody,
                evaluatePitch = ServiceLocator.evaluatePitch,
                scoreRound = ServiceLocator.scoreRound
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                songId = songId,
                onPitchPercent = onPitchPercent,
                sparkles = sparkles,
                harmonyPercent = harmonyPercent,
                awardOrdinal = awardOrdinal,
                songs = ServiceLocator.songs,
                progress = ServiceLocator.progress
            )
            modelClass.isAssignableFrom(PracticeViewModel::class.java) -> PracticeViewModel(
                songs = ServiceLocator.songs,
                progress = ServiceLocator.progress,
                settings = ServiceLocator.appSettings
            )
            modelClass.isAssignableFrom(WhistleCaseViewModel::class.java) -> WhistleCaseViewModel(
                progress = ServiceLocator.progress,
                settings = ServiceLocator.appSettings,
                unlockWhistle = ServiceLocator.unlockWhistle
            )
            modelClass.isAssignableFrom(FreeSlideViewModel::class.java) -> FreeSlideViewModel(
                settings = ServiceLocator.appSettings
            )
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
                settings = ServiceLocator.appSettings,
                progress = ServiceLocator.progress
            )
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
