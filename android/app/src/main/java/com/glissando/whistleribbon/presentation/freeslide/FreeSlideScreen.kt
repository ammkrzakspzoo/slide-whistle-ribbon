package com.glissando.whistleribbon.presentation.freeslide

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.AccentLight
import com.glissando.whistleribbon.core.theme.BackgroundBase
import com.glissando.whistleribbon.core.theme.BackgroundDeep
import com.glissando.whistleribbon.core.theme.BackgroundTop
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextMuted
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.Violet
import com.glissando.whistleribbon.core.ui.PitchStrip
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SecondaryButton

@Composable
fun FreeSlideScreen(onMenu: () -> Unit) {
    val freeViewModel: FreeSlideViewModel = viewModel(factory = AppViewModelFactory())
    val state by freeViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        onDispose { freeViewModel.pause() }
    }
    FreeSlideContent(
        state = state,
        onPitchChange = freeViewModel::onPitchChange,
        onRelease = freeViewModel::onRelease,
        onGroove = freeViewModel::cycleGroove,
        onKey = freeViewModel::toggleKey,
        onMenu = onMenu
    )
}

@Composable
private fun FreeSlideContent(
    state: FreeSlideUiState,
    onPitchChange: (Float) -> Unit,
    onRelease: () -> Unit,
    onGroove: () -> Unit,
    onKey: () -> Unit,
    onMenu: () -> Unit
) {
    val stripWidth = GameConfig.STRIP_WIDTH_DP.dp
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundTop.copy(alpha = 0.88f),
                BackgroundBase.copy(alpha = 0.70f),
                BackgroundDeep.copy(alpha = 0.95f)
            )
        )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (state.stripOnRight) 0.dp else stripWidth,
                    end = if (state.stripOnRight) stripWidth else 0.dp,
                    top = 86.dp,
                    bottom = 150.dp
                )
        ) {
            val top = size.height * 0.08f
            val usable = size.height * 0.84f
            for (index in 0..8) {
                val y = top + usable * (index / 8f)
                drawLine(
                    color = Violet.copy(alpha = if (index % 2 == 0) 0.26f else 0.12f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
            }
            val markerY = top + (1f - state.pitch01.coerceIn(0f, 1f)) * usable
            if (state.fingerDown) {
                drawCircle(
                    color = Accent.copy(alpha = 0.22f),
                    radius = 46.dp.toPx(),
                    center = Offset(size.width / 2f, markerY)
                )
            }
            drawCircle(
                brush = Brush.verticalGradient(listOf(AccentLight, Accent)),
                radius = 22.dp.toPx(),
                center = Offset(size.width / 2f, markerY)
            )
            drawCircle(
                color = SurfaceWhite,
                radius = 22.dp.toPx(),
                center = Offset(size.width / 2f, markerY),
                style = Stroke(width = 3.dp.toPx())
            )
            drawLine(
                color = Accent.copy(alpha = 0.35f),
                start = Offset(0f, markerY),
                end = Offset(size.width, markerY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        PitchStrip(
            onRight = state.stripOnRight,
            enabled = true,
            onPitchChange = onPitchChange,
            onRelease = onRelease,
            modifier = Modifier
                .align(if (state.stripOnRight) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxHeight()
                .width(stripWidth)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.free_slide),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.free_slide_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = if (state.stripOnRight) 20.dp else stripWidth + 12.dp,
                    end = if (state.stripOnRight) stripWidth + 12.dp else 20.dp,
                    bottom = 20.dp
                )
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Chip(
                    label = stringResource(R.string.groove),
                    active = state.grooveOn,
                    accent = Violet,
                    onClick = onGroove
                )
                Chip(
                    label = stringResource(R.string.key),
                    active = state.keySnap,
                    accent = Accent,
                    onClick = onKey
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                modifier = Modifier.fillMaxWidth(),
                onClick = onMenu
            )
        }
    }
}

@Composable
private fun Chip(
    label: String,
    active: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val stateLabel = stringResource(if (active) R.string.state_on else R.string.state_off)
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (active) accent.copy(alpha = 0.2f) else SurfaceWhite.copy(alpha = 0.78f))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp)
            .semantics { stateDescription = stateLabel },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) accent else TextMuted
        )
    }
}
