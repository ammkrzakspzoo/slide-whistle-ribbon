package com.glissando.whistleribbon.domain.usecase

import com.glissando.whistleribbon.domain.model.Melody
import com.glissando.whistleribbon.domain.model.MelodyPoint
import com.glissando.whistleribbon.domain.model.Song
import com.glissando.whistleribbon.domain.model.SparkleNote
import kotlin.math.abs
import kotlin.random.Random

class GenerateMelodyUseCase {

    private val degrees = listOf(0, 2, 4, 5, 7, 9, 11, 12, 14, 16)

    operator fun invoke(song: Song, maxDurationMs: Long): Melody {
        val random = Random(song.seed)
        val stepMs = (60000L / song.bpm.coerceAtLeast(40)).coerceIn(260L, 720L)
        val requested = 24 + song.difficulty * 5
        val affordable = (maxDurationMs / stepMs).toInt().coerceAtLeast(12)
        val count = minOf(requested, affordable)
        val leap = 1 + song.difficulty
        var index = 3 + random.nextInt(3)
        val points = ArrayList<MelodyPoint>(count)
        var time = 0L
        repeat(count) { step ->
            val move = random.nextInt(-leap, leap + 1)
            index = (index + move).coerceIn(0, degrees.lastIndex)
            val semitones = degrees[index].toFloat()
            val pitch = (semitones / 16f).coerceIn(0.06f, 0.94f)
            val hold = if (step % 4 == 3) (stepMs * 3) / 2 else stepMs
            points.add(MelodyPoint(time, pitch))
            time += hold
        }
        val duration = points.last().timeMs + stepMs
        val sparkles = pickSparkles(song, points, random)
        return Melody(points = points, sparkles = sparkles, durationMs = duration)
    }

    private fun pickSparkles(
        song: Song,
        points: List<MelodyPoint>,
        random: Random
    ): List<SparkleNote> {
        if (points.size < 6) return emptyList()
        val wanted = song.sparkleCount.coerceAtMost(points.size - 4)
        val chosen = LinkedHashSet<Int>()
        var guard = 0
        while (chosen.size < wanted && guard < wanted * 12) {
            guard++
            val candidate = 2 + random.nextInt(points.size - 3)
            if (chosen.none { abs(it - candidate) < 2 }) {
                chosen.add(candidate)
            }
        }
        return chosen.sorted().mapIndexed { order, pointIndex ->
            val point = points[pointIndex]
            SparkleNote(index = order, timeMs = point.timeMs, pitch01 = point.pitch01)
        }
    }
}
