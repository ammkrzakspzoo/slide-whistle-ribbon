package com.glissando.whistleribbon.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.config.GameConfig
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.BackgroundBase
import com.glissando.whistleribbon.core.theme.BackgroundDeep
import com.glissando.whistleribbon.core.theme.BackgroundTop
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.TextSecondary
import com.glissando.whistleribbon.core.ui.ClaySurface
import com.glissando.whistleribbon.core.ui.ConfettiLayer
import com.glissando.whistleribbon.core.ui.HarmonyBar
import com.glissando.whistleribbon.core.ui.PitchStrip
import com.glissando.whistleribbon.core.ui.RibbonCanvas
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.domain.model.RoundResult
import com.glissando.whistleribbon.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    songId: Int,
    practice: Boolean,
    onFinished: (RoundResult) -> Unit,
    onExit: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        factory = AppViewModelFactory(songId = songId, practice = practice)
    )
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.result)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }
    GameContent(
        state = state,
        onPitchChange = gameViewModel::onPitchChange,
        onRelease = gameViewModel::onRelease,
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onExit = onExit
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onPitchChange: (Float) -> Unit,
    onRelease: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onExit: () -> Unit
) {
    val stripWidth = GameConfig.STRIP_WIDTH_DP.dp
    val ribbonSpeedPx = with(LocalDensity.current) {
        GameConfig.RIBBON_SPEED_DP_PER_SEC.dp.toPx()
    }
    ScreenOverlay(reducedMotion = state.reducedMotion) {
        RibbonCanvas(
            points = state.points,
            sparkles = state.sparkleNotes,
            collected = state.collected,
            positionMs = state.positionMs,
            pitch01 = state.pitch01,
            onPitch = state.onPitch,
            speedPxPerSec = ribbonSpeedPx,
            nowLine = GameConfig.NOW_LINE_DP.dp,
            stripOnRight = state.stripOnRight,
            showMarker = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (state.stripOnRight) 0.dp else stripWidth,
                    end = if (state.stripOnRight) stripWidth else 0.dp,
                    top = 72.dp,
                    bottom = 104.dp
                )
        )
        PitchStrip(
            onRight = state.stripOnRight,
            enabled = state.phase == GamePhase.PLAYING,
            onPitchChange = onPitchChange,
            onRelease = onRelease,
            modifier = Modifier
                .align(if (state.stripOnRight) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxHeight()
                .width(stripWidth)
        )
        HarmonyBar(
            progress = state.harmony,
            description = stringResource(R.string.harmony),
            modifier = Modifier
                .align(if (state.stripOnRight) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 10.dp)
                .fillMaxHeight(0.52f)
        )
        if (state.showHint) {
            Text(
                text = stringResource(R.string.hint_touch_strip),
                style = MaterialTheme.typography.labelSmall,
                color = Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceWhite.copy(alpha = 0.86f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
        GameTopBar(
            songName = state.songName,
            progress = state.songProgress,
            onHome = onPause,
            onPause = onPause,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
        GameControls(
            state = state,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = if (state.stripOnRight) 20.dp else stripWidth + 12.dp,
                    end = if (state.stripOnRight) stripWidth + 12.dp else 20.dp,
                    bottom = 24.dp
                )
        )
    }
    if (state.phase == GamePhase.PAUSED) {
        PauseDialog(
            onResume = onResume,
            onMenu = onExit,
            onDismiss = onResume
        )
    }
}

@Composable
private fun ScreenOverlay(
    reducedMotion: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundBase.copy(alpha = 0.90f),
                BackgroundTop.copy(alpha = 0.70f),
                BackgroundDeep.copy(alpha = 0.94f)
            )
        )
    ) {
        if (!reducedMotion) {
            ConfettiLayer(pieceCount = 7, alpha = 0.12f, seed = 12)
        }
        content()
    }
}

@Composable
private fun GameTopBar(
    songName: String,
    progress: Float,
    onHome: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlyphButton(
            painterId = R.drawable.icon_home,
            description = stringResource(R.string.cd_home),
            onClick = onHome
        )
        Spacer(modifier = Modifier.width(10.dp))
        ClaySurface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            radius = 18.dp,
            borderTint = SurfaceSoft,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = songName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SurfaceSoft)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0.01f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(Primary)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        GlyphButton(
            painterId = R.drawable.icon_pause,
            description = stringResource(R.string.cd_pause),
            onClick = onPause
        )
    }
}

@Composable
private fun GlyphButton(
    painterId: Int,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(SurfaceWhite.copy(alpha = 0.82f))
    ) {
        Image(
            painter = painterResource(painterId),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun GameControls(state: GameUiState, modifier: Modifier = Modifier) {
    ClaySurface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        radius = 32.dp,
        borderTint = SurfaceSoft,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.percent_format, state.onPitchPercent),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primary,
                    modifier = Modifier.semantics {
                        contentDescription = state.onPitchPercent.toString()
                        liveRegion = LiveRegionMode.Polite
                    }
                )
                Text(
                    text = stringResource(R.string.on_pitch),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(SurfaceSoft)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_sparkle),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(
                            R.string.counter_format,
                            state.sparkles,
                            state.totalSparkles
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gold
                    )
                    Text(
                        text = stringResource(R.string.sparkles),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            if (state.practice && state.halfSpeed) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Accent.copy(alpha = 0.16f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.half_speed),
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent
                    )
                }
            }
        }
    }
}
