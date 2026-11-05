package com.glissando.whistleribbon.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite

@Composable
fun ClaySurface(
    modifier: Modifier = Modifier,
    radius: Dp = 26.dp,
    fill: Brush = Brush.verticalGradient(
        listOf(SurfaceWhite.copy(alpha = 0.92f), SurfaceWhite.copy(alpha = 0.78f))
    ),
    borderTint: Color = SurfaceSoft,
    shadowTint: Color = Primary,
    elevation: Dp = 10.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = shadowTint,
                spotColor = shadowTint
            )
            .clip(shape)
            .background(fill, shape)
            .border(
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            borderTint.copy(alpha = 0.95f),
                            borderTint.copy(alpha = 0.35f)
                        )
                    )
                ),
                shape = shape
            ),
        content = content
    )
}
