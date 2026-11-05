package com.glissando.whistleribbon.domain.repository

import com.glissando.whistleribbon.domain.model.AppSettings
import com.glissando.whistleribbon.domain.model.WhistleTone
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setAudioOffset(valueMs: Int)
    fun setVolume(value: Int)
    fun setStripOnRight(value: Boolean)
    fun setReducedMotion(value: Boolean)
    fun setHalfSpeed(value: Boolean)
    fun setWhistleTone(tone: WhistleTone)
}
