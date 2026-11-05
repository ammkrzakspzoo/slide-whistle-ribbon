package com.glissando.whistleribbon.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.AccentLight
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.PrimaryLight
import com.glissando.whistleribbon.core.theme.RibbonDull
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.domain.model.MelodyPoint
import com.glissando.whistleribbon.domain.model.SparkleNote

@Composable
fun RibbonCanvas(
    points: List<MelodyPoint>,
    sparkles: List<SparkleNote>,
    collected: Set<Int>,
    positionMs: Long,
    pitch01: Float,
    onPitch: Boolean,
    speedPxPerSec: Float,
    nowLine: Dp,
    stripOnRight: Boolean,
    showMarker: Boolean,
    modifier: Modifier = Modifier
) {
    val strokeWidth by animateDpAsState(
        targetValue = if (onPitch) 26.dp else 18.dp,
        animationSpec = tween(durationMillis = 120),
        label = "ribbonStroke"
    )
    val halo by animateFloatAsState(
        targetValue = if (onPitch) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "ribbonHalo"
    )
    Canvas(modifier = modifier) {
        val nowX = if (stripOnRight) size.width - nowLine.toPx() else nowLine.toPx()
        val top = size.height * 0.12f
        val usable = size.height * 0.76f
        val direction = if (stripOnRight) -1f else 1f

        fun xFor(timeMs: Long): Float =
            nowX + direction * ((timeMs - positionMs) / 1000f) * speedPxPerSec

        fun yFor(pitch: Float): Float = top + (1f - pitch.coerceIn(0f, 1f)) * usable

        val visible = points.map { Offset(xFor(it.timeMs), yFor(it.pitch01)) }

        if (visible.size >= 2) {
            val path = splinePath(visible)
            if (halo > 0f) {
                drawPath(
                    path = path,
                    color = Accent.copy(alpha = 0.3f * halo),
                    style = Stroke(
                        width = 40.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            drawPath(
                path = path,
                brush = if (onPitch) {
                    Brush.horizontalGradient(listOf(PrimaryLight, Primary))
                } else {
                    Brush.horizontalGradient(listOf(RibbonDull, RibbonDull))
                },
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        drawLine(
            color = Accent.copy(alpha = 0.45f),
            start = Offset(nowX, top - 24f),
            end = Offset(nowX, top + usable + 24f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        sparkles.forEach { note ->
            val x = xFor(note.timeMs)
            if (x < -60f || x > size.width + 60f) return@forEach
            val y = yFor(note.pitch01)
            val taken = collected.contains(note.index)
            drawSparkle(
                centerX = x,
                centerY = y,
                radius = if (taken) 20.dp.toPx() else 9.dp.toPx(),
                alpha = if (taken) 0.35f else 1f
            )
        }

        if (showMarker) {
            val markerY = yFor(pitch01)
            drawCircle(
                color = Accent.copy(alpha = 0.25f),
                radius = (18f + 12f * halo).dp.toPx(),
                center = Offset(nowX, markerY)
            )
            drawCircle(
                brush = Brush.verticalGradient(listOf(AccentLight, Accent)),
                radius = 18.dp.toPx(),
                center = Offset(nowX, markerY)
            )
            drawCircle(
                color = SurfaceWhite,
                radius = 18.dp.toPx(),
                center = Offset(nowX, markerY),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawSparkle(
    centerX: Float,
    centerY: Float,
    radius: Float,
    alpha: Float
) {
    rotate(degrees = 45f, pivot = Offset(centerX, centerY)) {
        drawRoundRect(
            color = Gold.copy(alpha = alpha),
            topLeft = Offset(centerX - radius / 2f, centerY - radius / 2f),
            size = Size(radius, radius),
            cornerRadius = CornerRadius(radius / 3f, radius / 3f)
        )
    }
    drawCircle(
        color = Gold.copy(alpha = alpha * 0.8f),
        radius = radius * 0.38f,
        center = Offset(centerX, centerY)
    )
}

private fun splinePath(points: List<Offset>): Path {
    val path = Path()
    path.moveTo(points.first().x, points.first().y)
    for (index in 0 until points.size - 1) {
        val previous = points[(index - 1).coerceAtLeast(0)]
        val current = points[index]
        val next = points[index + 1]
        val after = points[(index + 2).coerceAtMost(points.lastIndex)]
        val control1 = Offset(
            current.x + (next.x - previous.x) / 6f,
            current.y + (next.y - previous.y) / 6f
        )
        val control2 = Offset(
            next.x - (after.x - current.x) / 6f,
            next.y - (after.y - current.y) / 6f
        )
        path.cubicTo(control1.x, control1.y, control2.x, control2.y, next.x, next.y)
    }
    return path
}
