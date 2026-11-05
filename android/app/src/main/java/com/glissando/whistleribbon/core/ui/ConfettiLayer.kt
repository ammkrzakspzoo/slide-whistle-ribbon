package com.glissando.whistleribbon.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.Violet
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val startX: Float,
    val width: Float,
    val phase: Float,
    val speed: Float,
    val sway: Float,
    val color: Color
)

@Composable
fun ConfettiLayer(
    modifier: Modifier = Modifier,
    pieceCount: Int = 12,
    alpha: Float = 0.3f,
    reducedMotion: Boolean = false,
    seed: Int = 7
) {
    val pieces = remember(pieceCount, seed) {
        val palette = listOf(Primary, Accent, Gold, SurfaceSoft, Violet, SurfaceWhite)
        val random = Random(seed)
        List(pieceCount) {
            ConfettiPiece(
                startX = random.nextFloat(),
                width = 10f + random.nextFloat() * 10f,
                phase = random.nextFloat(),
                speed = 0.6f + random.nextFloat() * 0.8f,
                sway = 14f + random.nextFloat() * 26f,
                color = palette[random.nextInt(palette.size)]
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "confetti")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiDrift"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        pieces.forEach { piece ->
            val progress = (piece.phase + drift * piece.speed) % 1f
            val y = progress * (size.height + 120f) - 60f
            val x = piece.startX * size.width + sin((progress + piece.phase) * 6.2831f) * piece.sway
            drawRoundRect(
                color = piece.color.copy(alpha = alpha),
                topLeft = Offset(x, y),
                size = Size(piece.width, piece.width * 0.62f),
                cornerRadius = CornerRadius(piece.width * 0.3f, piece.width * 0.3f)
            )
        }
    }
}
