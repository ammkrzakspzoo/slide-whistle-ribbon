package com.glissando.whistleribbon.data.repository

import com.glissando.whistleribbon.data.local.PreferencesStorage
import com.glissando.whistleribbon.domain.model.AppSettings
import com.glissando.whistleribbon.domain.model.WhistleTone
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(
    private val storage: PreferencesStorage
) : SettingsRepository {

    private val state = MutableStateFlow(load())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    private fun load(): AppSettings {
        val toneName = storage.readString(KEY_TONE, WhistleTone.SLIDE.name)
        val tone = WhistleTone.entries.firstOrNull { it.name == toneName } ?: WhistleTone.SLIDE
        return AppSettings(
            audioOffsetMs = storage.readInt(KEY_OFFSET, 0),
            volume = storage.readInt(KEY_VOLUME, 80),
            stripOnRight = storage.readBoolean(KEY_SIDE, false),
            reducedMotion = storage.readBoolean(KEY_MOTION, false),
            halfSpeed = storage.readBoolean(KEY_HALF, true),
            whistleTone = tone
        )
    }

    override fun setAudioOffset(valueMs: Int) {
        storage.writeInt(KEY_OFFSET, valueMs)
        state.update { it.copy(audioOffsetMs = valueMs) }
    }

    override fun setVolume(value: Int) {
        storage.writeInt(KEY_VOLUME, value)
        state.update { it.copy(volume = value) }
    }

    override fun setStripOnRight(value: Boolean) {
        storage.writeBoolean(KEY_SIDE, value)
        state.update { it.copy(stripOnRight = value) }
    }

    override fun setReducedMotion(value: Boolean) {
        storage.writeBoolean(KEY_MOTION, value)
        state.update { it.copy(reducedMotion = value) }
    }

    override fun setHalfSpeed(value: Boolean) {
        storage.writeBoolean(KEY_HALF, value)
        state.update { it.copy(halfSpeed = value) }
    }

    override fun setWhistleTone(tone: WhistleTone) {
        storage.writeString(KEY_TONE, tone.name)
        state.update { it.copy(whistleTone = tone) }
    }

    private companion object {
        const val KEY_OFFSET = "audioOffsetMs"
        const val KEY_VOLUME = "volume"
        const val KEY_SIDE = "stripOnRight"
        const val KEY_MOTION = "reducedMotion"
        const val KEY_HALF = "halfSpeed"
        const val KEY_TONE = "whistleTone"
    }
}
