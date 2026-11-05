package com.glissando.whistleribbon.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val progress: ProgressRepository
) : ViewModel() {

    private val confirmReset = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> =
        combine(settings.settings, confirmReset) { values, asking ->
            SettingsUiState(
                audioOffsetMs = values.audioOffsetMs,
                volume = values.volume,
                stripOnRight = values.stripOnRight,
                reducedMotion = values.reducedMotion,
                confirmReset = asking
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsUiState()
        )

    fun setAudioOffset(value: Float) {
        settings.setAudioOffset((value / 10f).toInt() * 10)
    }

    fun setVolume(value: Float) {
        settings.setVolume(value.toInt().coerceIn(0, 100))
    }

    fun setStripOnRight(value: Boolean) {
        settings.setStripOnRight(value)
    }

    fun setReducedMotion(value: Boolean) {
        settings.setReducedMotion(value)
    }

    fun askReset() {
        confirmReset.value = true
    }

    fun dismissReset() {
        confirmReset.value = false
    }

    fun confirmResetProgress() {
        progress.resetAll()
        confirmReset.value = false
    }
}
