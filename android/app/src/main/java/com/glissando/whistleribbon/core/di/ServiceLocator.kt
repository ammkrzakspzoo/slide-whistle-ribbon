package com.glissando.whistleribbon.core.di

import android.content.Context
import com.glissando.whistleribbon.data.local.PreferencesStorage
import com.glissando.whistleribbon.data.repository.ProgressRepositoryImpl
import com.glissando.whistleribbon.data.repository.SettingsRepositoryImpl
import com.glissando.whistleribbon.data.repository.SongRepositoryImpl
import com.glissando.whistleribbon.domain.repository.ProgressRepository
import com.glissando.whistleribbon.domain.repository.SettingsRepository
import com.glissando.whistleribbon.domain.repository.SongRepository
import com.glissando.whistleribbon.domain.usecase.EvaluatePitchUseCase
import com.glissando.whistleribbon.domain.usecase.GenerateMelodyUseCase
import com.glissando.whistleribbon.domain.usecase.ScoreRoundUseCase
import com.glissando.whistleribbon.domain.usecase.UnlockWhistleUseCase

object ServiceLocator {

    private var progressStore: ProgressRepository? = null
    private var settingsStore: SettingsRepository? = null
    private var songStore: SongRepository? = null

    val generateMelody = GenerateMelodyUseCase()
    val evaluatePitch = EvaluatePitchUseCase()
    val scoreRound = ScoreRoundUseCase()
    val unlockWhistle = UnlockWhistleUseCase()

    val progress: ProgressRepository get() = requireNotNull(progressStore)
    val appSettings: SettingsRepository get() = requireNotNull(settingsStore)
    val songs: SongRepository get() = requireNotNull(songStore)

    fun init(context: Context) {
        if (progressStore != null) return
        val storage = PreferencesStorage(context.applicationContext)
        songStore = SongRepositoryImpl()
        settingsStore = SettingsRepositoryImpl(storage)
        progressStore = ProgressRepositoryImpl(storage)
    }
}
