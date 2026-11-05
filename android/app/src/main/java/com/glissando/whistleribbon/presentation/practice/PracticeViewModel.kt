package com.glissando.whistleribbon.presentation.practice

import androidx.lifecycle.ViewModel
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import com.glissando.whistleribbon.domain.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PracticeViewModel(
    private val songs: SongRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = state.asStateFlow()

    init {
        val unlocked = progress.unlockedUpTo()
        val available = songs.songs()
            .filter { it.id <= unlocked }
            .map { song ->
                PracticeSongItem(
                    id = song.id,
                    name = song.name,
                    difficulty = song.difficulty,
                    bestPercent = progress.progress(song.id).bestPercent
                )
            }
        state.value = PracticeUiState(
            songs = available,
            selectedId = available.firstOrNull()?.id ?: 1,
            halfSpeed = settings.settings.value.halfSpeed
        )
    }

    fun select(songId: Int) {
        state.update { it.copy(selectedId = songId) }
    }

    fun toggleHalfSpeed() {
        val next = !state.value.halfSpeed
        settings.setHalfSpeed(next)
        state.update { it.copy(halfSpeed = next) }
    }

    fun toggleLoopSection() {
        state.update { it.copy(loopSection = !it.loopSection) }
    }

    fun setSection(start: Float, end: Float) {
        val from = start.coerceIn(0f, 0.9f)
        val to = end.coerceIn(from + 0.08f, 1f)
        state.update { it.copy(sectionStart = from, sectionEnd = to) }
    }
}
