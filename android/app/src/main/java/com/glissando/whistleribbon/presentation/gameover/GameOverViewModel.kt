package com.glissando.whistleribbon.presentation.gameover

import androidx.lifecycle.ViewModel
import com.glissando.whistleribbon.domain.model.Award
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    songId: Int,
    onPitchPercent: Int,
    sparkles: Int,
    harmonyPercent: Int,
    awardOrdinal: Int,
    songs: SongRepository,
    progress: ProgressRepository
) : ViewModel() {

    private val state = MutableStateFlow(
        GameOverUiState(
            songId = songId,
            songName = songs.song(songId).name,
            onPitchPercent = onPitchPercent,
            sparkles = sparkles,
            harmonyPercent = harmonyPercent,
            bestPercent = progress.progress(songId).bestPercent,
            goldenWhistles = progress.goldenWhistles(),
            award = Award.entries.getOrElse(awardOrdinal) { Award.NONE }
        )
    )

    val uiState: StateFlow<GameOverUiState> = state.asStateFlow()
}
