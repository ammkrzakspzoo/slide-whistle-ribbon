package com.glissando.whistleribbon.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.AccentLight

@Composable
fun PitchStrip(
    onRight: Boolean,
    enabled: Boolean,
    onPitchChange: (Float) -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pitchHandler by rememberUpdatedState(onPitchChange)
    val releaseHandler by rememberUpdatedState(onRelease)
    val shape = if (onRight) {
        RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
    } else {
        RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Accent.copy(alpha = 0.22f),
                        AccentLight.copy(alpha = 0.12f),
                        Accent.copy(alpha = 0.26f)
                    )
                )
            )
            .border(1.5.dp, Accent.copy(alpha = 0.35f), shape)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    val height = size.height.toFloat().coerceAtLeast(1f)
                    pitchHandler(1f - (down.position.y / height).coerceIn(0f, 1f))
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change != null && change.pressed) {
                            pitchHandler(1f - (change.position.y / height).coerceIn(0f, 1f))
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })
                    releaseHandler()
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ticks = 13
            val inset = 14f
            for (index in 0 until ticks) {
                val y = size.height * (index / (ticks - 1f))
                val startX = if (onRight) size.width - inset - 54f else inset
                drawLine(
                    color = Accent.copy(alpha = if (index % 4 == 0) 0.55f else 0.34f),
                    start = Offset(startX, y.coerceIn(2f, size.height - 2f)),
                    end = Offset(startX + 54f, y.coerceIn(2f, size.height - 2f)),
                    strokeWidth = if (index % 4 == 0) 3.5f else 2f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
