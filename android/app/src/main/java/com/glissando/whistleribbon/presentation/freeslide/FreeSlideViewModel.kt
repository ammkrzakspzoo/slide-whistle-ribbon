package com.glissando.whistleribbon.presentation.freeslide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glissando.whistleribbon.data.audio.WhistleSynth
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class FreeSlideViewModel(
    private val settings: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(FreeSlideUiState())
    val uiState: StateFlow<FreeSlideUiState> = state.asStateFlow()

    private val synth = WhistleSynth()
    private var audioJob: Job? = null

    private val pentatonic = listOf(0f, 0.12f, 0.24f, 0.36f, 0.48f, 0.6f, 0.72f, 0.84f, 1f)

    init {
        val current = settings.settings.value
        state.value = FreeSlideUiState(
            stripOnRight = current.stripOnRight,
            reducedMotion = current.reducedMotion
        )
        audioJob = viewModelScope.launch(Dispatchers.Default) {
            synth.setVolume(current.volume)
            synth.setTone(current.whistleTone)
            if (!synth.open()) return@launch
            try {
                while (isActive && synth.renderBlock()) {
                    continue
                }
            } finally {
                synth.close()
            }
        }
    }

    fun onPitchChange(value: Float) {
        val snapped = if (state.value.keySnap) snap(value) else value.coerceIn(0f, 1f)
        synth.setPitch(snapped)
        synth.setBlowing(true)
        state.update { it.copy(pitch01 = snapped, fingerDown = true) }
    }

    fun onRelease() {
        synth.setBlowing(false)
        state.update { it.copy(fingerDown = false) }
    }

    fun cycleGroove() {
        val next = (state.value.groovePattern + 1) % GROOVE_COUNT
        val on = !(state.value.grooveOn && next == 0)
        synth.setGroove(if (on) next else -1)
        state.update { it.copy(groovePattern = next, grooveOn = on) }
    }

    fun toggleKey() {
        state.update { it.copy(keySnap = !it.keySnap) }
    }

    fun pause() {
        synth.setBlowing(false)
        synth.setGroove(-1)
        state.update { it.copy(fingerDown = false, grooveOn = false) }
    }

    private fun snap(value: Float): Float {
        val clamped = value.coerceIn(0f, 1f)
        return pentatonic.minByOrNull { abs(it - clamped) } ?: clamped
    }

    override fun onCleared() {
        audioJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val GROOVE_COUNT = 4
    }
}
