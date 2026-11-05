package com.glissando.whistleribbon.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.glissando.whistleribbon.core.theme.BackgroundDeep
import com.glissando.whistleribbon.core.theme.BackgroundBase
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.OnPrimary
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.PrimaryDeep
import com.glissando.whistleribbon.core.theme.PrimaryLight
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextMuted
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.TextSecondary
import com.glissando.whistleribbon.core.theme.Violet
import com.glissando.whistleribbon.core.ui.ClaySurface
import com.glissando.whistleribbon.core.ui.ConfettiLayer
import com.glissando.whistleribbon.core.ui.PrimaryButton
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SongFloat
import com.glissando.whistleribbon.core.ui.StatCard

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onPractice: () -> Unit,
    onWhistles: () -> Unit,
    onFreeSlide: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()
    MenuContent(
        state = state,
        onPlay = onPlay,
        onPractice = onPractice,
        onWhistles = onWhistles,
        onFreeSlide = onFreeSlide,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: (Int) -> Unit,
    onPractice: () -> Unit,
    onWhistles: () -> Unit,
    onFreeSlide: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menu")
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (state.reducedMotion) 1f else 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "menuBreathe"
    )
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundBase.copy(alpha = 0.82f),
                BackgroundBase.copy(alpha = 0.62f),
                BackgroundDeep.copy(alpha = 0.94f)
            )
        )
    ) {
        ConfettiLayer(
            pieceCount = 9,
            alpha = 0.18f,
            reducedMotion = state.reducedMotion,
            seed = 4
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            MenuHeader(onSettings = onSettings)
            Spacer(modifier = Modifier.height(10.dp))
            WhistleStrip(state = state)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.height(172.dp)) {
                PlayTile(
                    songName = state.nextSongName,
                    scale = breathe,
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxHeight(),
                    onPlay = { onPlay(state.nextSongId) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                MenuStats(
                    state = state,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.height(96.dp)) {
                ModeTile(
                    label = stringResource(R.string.practice),
                    accent = Accent,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = onPractice
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_refresh),
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                ModeTile(
                    label = stringResource(R.string.whistles),
                    accent = Gold,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = onWhistles
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_2),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                ModeTile(
                    label = stringResource(R.string.free_slide),
                    accent = Violet,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = onFreeSlide
                ) {
                    EqualizerMark()
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.songs, key = { it.id }) { song ->
                    SongFloat(
                        number = song.id,
                        name = song.name,
                        difficulty = song.difficulty,
                        earned = song.earned,
                        locked = song.locked,
                        description = song.name,
                        state = stringResource(
                            if (song.locked) R.string.locked else R.string.unlocked
                        ),
                        onClick = { onPlay(song.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.hint_trace),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MenuHeader(onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.app_name_upper),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onSettings,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.72f))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
                contentDescription = stringResource(R.string.cd_settings),
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun WhistleStrip(state: MenuUiState) {
    val label = stringResource(R.string.golden_whistles)
    val value = stringResource(
        R.string.counter_format,
        state.goldenWhistles,
        state.totalSongs
    )
    ClaySurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .semantics {
                contentDescription = label
                liveRegion = LiveRegionMode.Polite
            },
        radius = 20.dp,
        borderTint = SurfaceSoft,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SurfaceSoft.copy(alpha = 0.7f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.whistleProgress.coerceIn(0.02f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(Accent)
                )
            }
        }
    }
}

@Composable
private fun PlayTile(
    songName: String,
    scale: Float,
    modifier: Modifier = Modifier,
    onPlay: () -> Unit
) {
    ClaySurface(
        modifier = modifier.scale(scale),
        radius = 30.dp,
        fill = Brush.linearGradient(listOf(PrimaryLight, Primary, PrimaryDeep)),
        borderTint = SurfaceSoft.copy(alpha = 0.7f),
        elevation = 14.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = songName,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PrimaryButton(
                text = stringResource(R.string.play),
                icon = ImageVector.vectorResource(R.drawable.ic_play_arrow),
                height = 56.dp,
                containerBrush = Brush.horizontalGradient(
                    listOf(SurfaceWhite, SurfaceWhite)
                ),
                contentColor = Primary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onPlay
            )
        }
    }
}

@Composable
private fun MenuStats(state: MenuUiState, modifier: Modifier = Modifier) {
    val visibleCards = (if (state.bestPercent > 0) 1 else 0) +
        (if (state.sparkles > 0) 1 else 0)
    Column(modifier = modifier) {
        if (visibleCards >= 2) {
            StatCard(
                label = stringResource(R.string.best_on_pitch),
                value = stringResource(R.string.percent_format, state.bestPercent),
                accent = Accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                label = stringResource(R.string.sparkles),
                value = state.sparkles.toString(),
                accent = Gold,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            StatCard(
                label = stringResource(R.string.tunes),
                value = state.totalSongs.toString(),
                accent = Primary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ModeTile(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    mark: @Composable () -> Unit
) {
    ClaySurface(
        modifier = modifier.clickable(onClick = onClick),
        radius = 24.dp,
        borderTint = accent.copy(alpha = 0.4f),
        shadowTint = accent,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            mark()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EqualizerMark() {
    Canvas(modifier = Modifier.size(26.dp)) {
        val bars = 4
        val gap = size.width * 0.1f
        val barWidth = (size.width - gap * (bars - 1)) / bars
        val heights = listOf(0.45f, 0.95f, 0.65f, 0.85f)
        heights.forEachIndexed { index, factor ->
            val barHeight = size.height * factor
            drawRoundRect(
                color = Violet,
                topLeft = Offset(index * (barWidth + gap), size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
