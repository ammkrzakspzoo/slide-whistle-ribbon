package com.glissando.whistleribbon.domain.usecase

import com.glissando.whistleribbon.domain.model.Melody
import kotlin.math.abs

class EvaluatePitchUseCase {

    fun pitchAt(melody: Melody, timeMs: Long): Float {
        val points = melody.points
        if (points.isEmpty()) return 0.5f
        if (timeMs <= points.first().timeMs) return points.first().pitch01
        if (timeMs >= points.last().timeMs) return points.last().pitch01
        var low = 0
        var high = points.lastIndex
        while (low < high - 1) {
            val middle = (low + high) / 2
            if (points[middle].timeMs <= timeMs) low = middle else high = middle
        }
        val start = points[low]
        val end = points[high]
        val span = (end.timeMs - start.timeMs).toFloat().coerceAtLeast(1f)
        val factor = ((timeMs - start.timeMs).toFloat() / span).coerceIn(0f, 1f)
        val eased = factor * factor * (3f - 2f * factor)
        return start.pitch01 + (end.pitch01 - start.pitch01) * eased
    }

    fun isOnPitch(target: Float, current: Float, tolerance: Float): Boolean =
        abs(target - current) <= tolerance
}
