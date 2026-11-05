package com.glissando.whistleribbon.domain.repository

import com.glissando.whistleribbon.domain.model.RoundResult
import com.glissando.whistleribbon.domain.model.SongProgress

interface ProgressRepository {
    fun progress(songId: Int): SongProgress
    fun unlockedUpTo(): Int
    fun goldenWhistles(): Int
    fun totalSparkles(): Int
    fun bestPercent(): Int
    fun save(result: RoundResult)
    fun resetAll()
}
