package com.glissando.whistleribbon.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.navigation.Screen
import com.glissando.whistleribbon.presentation.freeslide.FreeSlideScreen
import com.glissando.whistleribbon.presentation.game.GameScreen
import com.glissando.whistleribbon.presentation.gameover.GameOverScreen
import com.glissando.whistleribbon.presentation.menu.MenuScreen
import com.glissando.whistleribbon.presentation.practice.PracticeScreen
import com.glissando.whistleribbon.presentation.settings.SettingsScreen
import com.glissando.whistleribbon.presentation.splash.SplashScreen
import com.glissando.whistleribbon.presentation.whistlecase.WhistleCaseScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler(enabled = true) {
        if (!appViewModel.back()) {
            (context as? Activity)?.finish()
        }
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            (fadeIn(tween(durationMillis = 320)) + scaleIn(initialScale = 0.96f)) togetherWith
                fadeOut(tween(durationMillis = 240))
        },
        label = "appScreens"
    ) { target ->
        ScreenHost(target) {
            when (target) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                Screen.Menu -> MenuScreen(
                    onPlay = { songId -> appViewModel.navigate(Screen.Game(songId, false)) },
                    onPractice = { appViewModel.navigate(Screen.Practice) },
                    onWhistles = { appViewModel.navigate(Screen.WhistleCase) },
                    onFreeSlide = { appViewModel.navigate(Screen.FreeSlide) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    songId = target.songId,
                    practice = target.practice,
                    onFinished = { result ->
                        appViewModel.replace(
                            Screen.GameOver(
                                songId = result.songId,
                                onPitchPercent = result.onPitchPercent,
                                sparkles = result.sparkles,
                                harmonyPercent = result.harmonyPercent,
                                awardOrdinal = result.award.ordinal
                            )
                        )
                    },
                    onExit = { appViewModel.toMenu() }
                )

                is Screen.GameOver -> GameOverScreen(
                    songId = target.songId,
                    onPitchPercent = target.onPitchPercent,
                    sparkles = target.sparkles,
                    harmonyPercent = target.harmonyPercent,
                    awardOrdinal = target.awardOrdinal,
                    onPlayAgain = { songId ->
                        appViewModel.replace(Screen.Game(songId, false))
                    },
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.Practice -> PracticeScreen(
                    onStart = { songId -> appViewModel.navigate(Screen.Game(songId, true)) },
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.WhistleCase -> WhistleCaseScreen(
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.FreeSlide -> FreeSlideScreen(
                    onMenu = { appViewModel.toMenu() }
                )

                Screen.Settings -> SettingsScreen(
                    onMenu = { appViewModel.toMenu() }
                )
            }
        }
    }
}

@Composable
private fun ScreenHost(key: Screen, content: @Composable () -> Unit) {
    val owner = remember(key) { ScreenViewModelStoreOwner() }
    DisposableEffect(owner) {
        onDispose { owner.viewModelStore.clear() }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }
}

private class ScreenViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}
