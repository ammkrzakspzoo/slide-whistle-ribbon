package com.glissando.whistleribbon.presentation.practice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.BackgroundBase
import com.glissando.whistleribbon.core.theme.BackgroundDeep
import com.glissando.whistleribbon.core.theme.BackgroundTop
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextMuted
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.TextSecondary
import com.glissando.whistleribbon.core.ui.ClaySurface
import com.glissando.whistleribbon.core.ui.PrimaryButton
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SecondaryButton

@Composable
fun PracticeScreen(
    onStart: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val practiceViewModel: PracticeViewModel = viewModel(factory = AppViewModelFactory())
    val state by practiceViewModel.uiState.collectAsStateWithLifecycle()
    PracticeContent(
        state = state,
        onSelect = practiceViewModel::select,
        onToggleSpeed = practiceViewModel::toggleHalfSpeed,
        onToggleLoop = practiceViewModel::toggleLoopSection,
        onSection = practiceViewModel::setSection,
        onStart = { onStart(state.selectedId) },
        onMenu = onMenu
    )
}

@Composable
private fun PracticeContent(
    state: PracticeUiState,
    onSelect: (Int) -> Unit,
    onToggleSpeed: () -> Unit,
    onToggleLoop: () -> Unit,
    onSection: (Float, Float) -> Unit,
    onStart: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundTop.copy(alpha = 0.88f),
                BackgroundBase.copy(alpha = 0.74f),
                BackgroundDeep.copy(alpha = 0.95f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(46.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.practice_room),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.practice_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ToggleChip(
                    label = stringResource(R.string.half_speed),
                    active = state.halfSpeed,
                    accent = Accent,
                    onClick = onToggleSpeed
                )
                ToggleChip(
                    label = stringResource(R.string.loop_section),
                    active = state.loopSection,
                    accent = Primary,
                    onClick = onToggleLoop
                )
            }
            if (state.loopSection) {
                Spacer(modifier = Modifier.height(6.dp))
                RangeSlider(
                    value = state.sectionStart..state.sectionEnd,
                    onValueChange = { range -> onSection(range.start, range.endInclusive) },
                    colors = SliderDefaults.colors(
                        thumbColor = Accent,
                        activeTrackColor = Accent,
                        inactiveTrackColor = SurfaceSoft
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = state.songs, key = { it.id }) { song ->
                    PracticeRow(
                        song = song,
                        selected = song.id == state.selectedId,
                        onClick = { onSelect(song.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.start_practice),
                height = 56.dp,
                modifier = Modifier.fillMaxWidth(),
                onClick = onStart
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                modifier = Modifier.fillMaxWidth(),
                onClick = onMenu
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ToggleChip(
    label: String,
    active: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val activeLabel = stringResource(if (active) R.string.state_on else R.string.state_off)
    Box(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (active) accent.copy(alpha = 0.18f) else SurfaceWhite.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp)
            .semantics { stateDescription = activeLabel },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) accent else TextMuted
        )
    }
}

@Composable
private fun PracticeRow(
    song: PracticeSongItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    ClaySurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        radius = 22.dp,
        borderTint = if (selected) Accent else SurfaceSoft,
        elevation = if (selected) 10.dp else 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = song.id.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = if (selected) Accent else Primary
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.difficulty_format, song.difficulty),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            if (song.bestPercent > 0) {
                Text(
                    text = stringResource(R.string.percent_format, song.bestPercent),
                    style = MaterialTheme.typography.titleSmall,
                    color = Accent
                )
            }
        }
    }
}
