package com.glissando.whistleribbon.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextMuted
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.TextSecondary

@Composable
fun SongFloat(
    number: Int,
    name: String,
    difficulty: Int,
    earned: Boolean,
    locked: Boolean,
    description: String,
    state: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ClaySurface(
        modifier = modifier
            .width(112.dp)
            .height(124.dp)
            .semantics {
                contentDescription = description
                stateDescription = state
            }
            .clickable(enabled = !locked, onClick = onClick),
        radius = 24.dp,
        fill = if (locked) {
            Brush.verticalGradient(
                listOf(SurfaceWhite.copy(alpha = 0.55f), SurfaceSoft.copy(alpha = 0.42f))
            )
        } else {
            Brush.verticalGradient(
                listOf(SurfaceWhite.copy(alpha = 0.95f), SurfaceWhite.copy(alpha = 0.76f))
            )
        },
        borderTint = if (earned) Gold else SurfaceSoft,
        shadowTint = Primary,
        elevation = if (locked) 4.dp else 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (locked) TextMuted else Primary
                )
                if (earned) {
                    Image(
                        painter = painterResource(R.drawable.sprite_3),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(2.dp, Accent.copy(alpha = 0.55f), CircleShape)
                    )
                }
            }
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = if (locked) TextMuted else TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                if (index < difficulty) {
                                    Accent.copy(alpha = 0.85f)
                                } else {
                                    TextSecondary.copy(alpha = 0.22f)
                                },
                                CircleShape
                            )
                    )
                    if (index < 4) Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}
