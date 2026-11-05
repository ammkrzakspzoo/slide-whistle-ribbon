package com.glissando.whistleribbon.data.repository

import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.data.local.PreferencesStorage
import com.glissando.whistleribbon.domain.model.Award
import com.glissando.whistleribbon.domain.model.RoundResult
import com.glissando.whistleribbon.domain.model.SongProgress
import com.glissando.whistleribbon.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val storage: PreferencesStorage
) : ProgressRepository {

    override fun progress(songId: Int): SongProgress = SongProgress(
        songId = songId,
        bestPercent = storage.readInt(KEY_BEST + songId, 0),
        sparkles = storage.readInt(KEY_SPARKLES + songId, 0),
        award = Award.entries.getOrElse(storage.readInt(KEY_AWARD + songId, 0)) { Award.NONE }
    )

    override fun unlockedUpTo(): Int =
        storage.readInt(KEY_UNLOCKED, 1).coerceIn(1, GameConfig.TOTAL_SONGS)

    override fun goldenWhistles(): Int = (1..GameConfig.TOTAL_SONGS)
        .count { storage.readInt(KEY_AWARD + it, 0) == Award.GOLD.ordinal }

    override fun totalSparkles(): Int = (1..GameConfig.TOTAL_SONGS)
        .sumOf { storage.readInt(KEY_SPARKLES + it, 0) }

    override fun bestPercent(): Int = (1..GameConfig.TOTAL_SONGS)
        .maxOf { storage.readInt(KEY_BEST + it, 0) }

    override fun save(result: RoundResult) {
        val id = result.songId
        if (result.onPitchPercent > storage.readInt(KEY_BEST + id, 0)) {
            storage.writeInt(KEY_BEST + id, result.onPitchPercent)
        }
        if (result.sparkles > storage.readInt(KEY_SPARKLES + id, 0)) {
            storage.writeInt(KEY_SPARKLES + id, result.sparkles)
        }
        if (result.award.ordinal > storage.readInt(KEY_AWARD + id, 0)) {
            storage.writeInt(KEY_AWARD + id, result.award.ordinal)
        }
        if (result.award.isEarned) {
            val next = (id + 1).coerceAtMost(GameConfig.TOTAL_SONGS)
            if (next > unlockedUpTo()) storage.writeInt(KEY_UNLOCKED, next)
        }
    }

    override fun resetAll() {
        storage.clearProgress(listOf(KEY_BEST, KEY_SPARKLES, KEY_AWARD, KEY_UNLOCKED))
    }

    private companion object {
        const val KEY_BEST = "bestPercent_"
        const val KEY_SPARKLES = "sparkles_"
        const val KEY_AWARD = "award_"
        const val KEY_UNLOCKED = "unlockedUpTo"
    }
}
