package com.glissando.whistleribbon.presentation.whistlecase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glissando.whistleribbon.data.audio.WhistleSynth
import com.glissando.whistleribbon.domain.model.WhistleTone
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import com.glissando.whistleribbon.domain.usecase.UnlockWhistleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WhistleCaseViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val unlockWhistle: UnlockWhistleUseCase
) : ViewModel() {

    private val state = MutableStateFlow(WhistleCaseUiState())
    val uiState: StateFlow<WhistleCaseUiState> = state.asStateFlow()

    private var demoJob: Job? = null

    init {
        refresh()
    }

    fun equip(tone: WhistleTone) {
        if (!unlockWhistle.isUnlocked(tone, progress.goldenWhistles())) return
        settings.setWhistleTone(tone)
        refresh()
        playDemo(tone)
    }

    private fun refresh() {
        val golden = progress.goldenWhistles()
        val current = settings.settings.value.whistleTone
        state.value = WhistleCaseUiState(
            goldenWhistles = golden,
            cards = WhistleTone.entries.map { tone ->
                WhistleCardItem(
                    tone = tone,
                    unlocked = unlockWhistle.isUnlocked(tone, golden),
                    equipped = tone == current
                )
            }
        )
    }

    private fun playDemo(tone: WhistleTone) {
        demoJob?.cancel()
        demoJob = viewModelScope.launch {
            val synth = WhistleSynth()
            synth.setTone(tone)
            synth.setVolume(settings.settings.value.volume)
            val render = launch(Dispatchers.Default) {
                if (!synth.open()) return@launch
                try {
                    while (isActive && synth.renderBlock()) {
                        continue
                    }
                } finally {
                    synth.close()
                }
            }
            delay(60)
            synth.setBlowing(true)
            listOf(0.18f, 0.38f, 0.52f, 0.74f, 0.58f).forEach { step ->
                synth.setPitch(step)
                delay(210)
            }
            synth.setBlowing(false)
            delay(160)
            render.cancel()
        }
    }

    override fun onCleared() {
        demoJob?.cancel()
        super.onCleared()
    }
}
