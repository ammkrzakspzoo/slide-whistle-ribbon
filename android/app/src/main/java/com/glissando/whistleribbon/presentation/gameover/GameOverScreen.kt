package com.glissando.whistleribbon.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.AccentLight
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.OnPrimary
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.PrimaryDeep
import com.glissando.whistleribbon.core.theme.PrimaryLight
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.ui.ConfettiLayer
import com.glissando.whistleribbon.core.ui.PrimaryButton
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SecondaryButton
import com.glissando.whistleribbon.core.ui.StatCard
import com.glissando.whistleribbon.domain.model.Award

@Composable
fun GameOverScreen(
    songId: Int,
    onPitchPercent: Int,
    sparkles: Int,
    harmonyPercent: Int,
    awardOrdinal: Int,
    onPlayAgain: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: GameOverViewModel = viewModel(
        factory = AppViewModelFactory(
            songId = songId,
            onPitchPercent = onPitchPercent,
            sparkles = sparkles,
            harmonyPercent = harmonyPercent,
            awardOrdinal = awardOrdinal
        )
    )
    val state by resultViewModel.uiState.collectAsStateWithLifecycle()
    GameOverContent(
        state = state,
        onPlayAgain = { onPlayAgain(state.songId) },
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val appear = remember { MutableTransitionState(false).apply { targetState = true } }
    val ringState = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberTransition(ringState, label = "result")
    val sweep by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 900, easing = FastOutSlowInEasing) },
        label = "resultSweep"
    ) { shown -> if (shown) state.onPitchPercent / 100f else 0f }

    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                PrimaryDeep.copy(alpha = 0.80f),
                Primary.copy(alpha = 0.68f),
                PrimaryLight.copy(alpha = 0.74f)
            )
        )
    ) {
        ConfettiLayer(pieceCount = 16, alpha = 0.34f, seed = 33)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.encore),
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.songName,
                style = MaterialTheme.typography.titleSmall,
                color = SurfaceSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visibleState = appear,
                enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = 260f))
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.award == Award.GOLD) {
                        Image(
                            painter = painterResource(R.drawable.sprite_3),
                            contentDescription = stringResource(R.string.cd_whistle),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(100.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.sprite_5),
                            contentDescription = stringResource(R.string.cd_rosette),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(92.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            PitchRing(
                sweep = sweep,
                percentText = stringResource(R.string.percent_format, state.onPitchPercent),
                caption = stringResource(R.string.on_pitch)
            )
            Spacer(modifier = Modifier.height(18.dp))
            ResultStats(state = state)
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.hint_slower),
                style = MaterialTheme.typography.labelSmall,
                color = SurfaceSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.play_again),
                icon = ImageVector.vectorResource(R.drawable.ic_refresh),
                height = 60.dp,
                containerBrush = Brush.horizontalGradient(listOf(SurfaceWhite, SurfaceSoft)),
                contentColor = Primary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onPlayAgain
            )
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
private fun PitchRing(sweep: Float, percentText: String, caption: String) {
    Box(
        modifier = Modifier.size(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = SurfaceWhite.copy(alpha = 0.25f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                brush = Brush.horizontalGradient(listOf(Accent, AccentLight)),
                startAngle = 135f,
                sweepAngle = 270f * sweep.coerceIn(0f, 1f),
                useCenter = false,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = percentText,
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                modifier = Modifier.semantics {
                    contentDescription = percentText
                    liveRegion = LiveRegionMode.Polite
                }
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = OnPrimary
            )
        }
    }
}

@Composable
private fun ResultStats(state: GameOverUiState) {
    val showSparkles = state.sparkles > 0
    val showHarmony = state.harmonyPercent > 0
    val showBest = state.bestPercent > 0
    val visible = (if (showSparkles) 1 else 0) +
        (if (showHarmony) 1 else 0) +
        (if (showBest) 1 else 0)
    if (visible < 2) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showSparkles) {
            StatCard(
                label = stringResource(R.string.sparkles),
                value = state.sparkles.toString(),
                accent = Gold,
                modifier = Modifier.weight(1f)
            )
        }
        if (showHarmony) {
            StatCard(
                label = stringResource(R.string.harmony),
                value = stringResource(R.string.percent_format, state.harmonyPercent),
                accent = Accent,
                modifier = Modifier.weight(1f)
            )
        }
        if (showBest) {
            StatCard(
                label = stringResource(R.string.best),
                value = stringResource(R.string.percent_format, state.bestPercent),
                accent = Primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
