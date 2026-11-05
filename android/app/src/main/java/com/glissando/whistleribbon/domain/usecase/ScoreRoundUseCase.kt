package com.glissando.whistleribbon.domain.usecase

import com.glissando.whistleribbon.domain.model.Award
import com.glissando.whistleribbon.domain.model.RoundResult

class ScoreRoundUseCase {

    operator fun invoke(
        songId: Int,
        practice: Boolean,
        onPitchFrames: Int,
        totalFrames: Int,
        sparkles: Int,
        totalSparkles: Int,
        harmony: Float,
        goldThreshold: Int,
        silverThreshold: Int,
        bronzeThreshold: Int
    ): RoundResult {
        val percent = if (totalFrames <= 0) 0 else (onPitchFrames * 100) / totalFrames
        val award = when {
            practice -> Award.NONE
            percent >= goldThreshold -> Award.GOLD
            percent >= silverThreshold -> Award.SILVER
            percent >= bronzeThreshold -> Award.BRONZE
            else -> Award.NONE
        }
        return RoundResult(
            songId = songId,
            practice = practice,
            onPitchPercent = percent.coerceIn(0, 100),
            sparkles = sparkles,
            totalSparkles = totalSparkles,
            harmonyPercent = (harmony * 100f).toInt().coerceIn(0, 100),
            award = award
        )
    }
}
