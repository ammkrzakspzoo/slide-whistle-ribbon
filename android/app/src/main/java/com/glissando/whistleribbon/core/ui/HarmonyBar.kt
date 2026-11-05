package com.glissando.whistleribbon.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.AccentLight
import com.glissando.whistleribbon.core.theme.SurfaceWhite

@Composable
fun HarmonyBar(
    progress: Float,
    description: String,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0.002f, 1f),
        animationSpec = tween(durationMillis = 90, easing = LinearEasing),
        label = "harmonyFill"
    )
    Box(
        modifier = modifier
            .width(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(SurfaceWhite.copy(alpha = 0.55f))
            .semantics {
                contentDescription = description
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animated)
                .background(
                    Brush.verticalGradient(listOf(AccentLight, Accent)),
                    RoundedCornerShape(7.dp)
                )
        )
    }
}
