package com.glissando.whistleribbon.presentation.menu

import androidx.lifecycle.ViewModel
import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import com.glissando.whistleribbon.domain.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val songs: SongRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = state.asStateFlow()

    init {
        load()
    }

    fun load() {
        val unlocked = progress.unlockedUpTo()
        val catalogue = songs.songs().map { song ->
            val saved = progress.progress(song.id)
            MenuSongItem(
                id = song.id,
                name = song.name,
                difficulty = song.difficulty,
                earned = saved.award.isEarned,
                locked = song.id > unlocked,
                bestPercent = saved.bestPercent
            )
        }
        val next = catalogue.firstOrNull { !it.earned && !it.locked }
            ?: catalogue.lastOrNull { !it.locked }
            ?: catalogue.first()
        state.value = MenuUiState(
            nextSongId = next.id,
            nextSongName = next.name,
            goldenWhistles = progress.goldenWhistles(),
            totalSongs = GameConfig.TOTAL_SONGS,
            bestPercent = progress.bestPercent(),
            sparkles = progress.totalSparkles(),
            reducedMotion = settings.settings.value.reducedMotion,
            songs = catalogue
        )
    }
}
