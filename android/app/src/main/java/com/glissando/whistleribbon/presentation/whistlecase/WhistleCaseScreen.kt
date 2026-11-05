package com.glissando.whistleribbon.presentation.whistlecase

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.BackgroundBase
import com.glissando.whistleribbon.core.theme.BackgroundDeep
import com.glissando.whistleribbon.core.theme.BackgroundTop
import com.glissando.whistleribbon.core.theme.Gold
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.theme.TextMuted
import com.glissando.whistleribbon.core.theme.TextPrimary
import com.glissando.whistleribbon.core.theme.TextSecondary
import com.glissando.whistleribbon.core.ui.ClaySurface
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SecondaryButton
import com.glissando.whistleribbon.domain.model.WhistleTone

@Composable
fun WhistleCaseScreen(onMenu: () -> Unit) {
    val caseViewModel: WhistleCaseViewModel = viewModel(factory = AppViewModelFactory())
    val state by caseViewModel.uiState.collectAsStateWithLifecycle()
    WhistleCaseContent(
        state = state,
        onEquip = caseViewModel::equip,
        onMenu = onMenu
    )
}

@Composable
private fun WhistleCaseContent(
    state: WhistleCaseUiState,
    onEquip: (WhistleTone) -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundTop.copy(alpha = 0.90f),
                BackgroundBase.copy(alpha = 0.72f),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.whistle_case),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.golden_whistles),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = state.goldenWhistles.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = Gold
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = state.cards, key = { it.tone.name }) { card ->
                    WhistleCard(card = card, onEquip = onEquip)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
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
private fun WhistleCard(card: WhistleCardItem, onEquip: (WhistleTone) -> Unit) {
    val toneName = stringResource(toneNameRes(card.tone))
    val stateLabel = stringResource(
        when {
            card.equipped -> R.string.equipped
            card.unlocked -> R.string.use
            else -> R.string.locked
        }
    )
    ClaySurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(166.dp)
            .semantics { stateDescription = stateLabel }
            .clickable(enabled = card.unlocked) { onEquip(card.tone) },
        radius = 26.dp,
        borderTint = if (card.equipped) Accent else SurfaceSoft,
        shadowTint = if (card.equipped) Accent else Primary,
        elevation = if (card.unlocked) 10.dp else 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = stringResource(R.string.cd_whistle),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(54.dp)
            )
            Text(
                text = toneName,
                style = MaterialTheme.typography.titleSmall,
                color = if (card.unlocked) TextPrimary else TextMuted,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.requires_format, card.tone.requiredWhistles),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        when {
                            card.equipped -> Accent
                            card.unlocked -> SurfaceWhite.copy(alpha = 0.9f)
                            else -> SurfaceSoft.copy(alpha = 0.5f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        card.equipped -> SurfaceWhite
                        card.unlocked -> Primary
                        else -> TextMuted
                    }
                )
            }
        }
    }
}

private fun toneNameRes(tone: WhistleTone): Int = when (tone) {
    WhistleTone.SLIDE -> R.string.tone_slide
    WhistleTone.FLUTE -> R.string.tone_flute
    WhistleTone.BIRD -> R.string.tone_bird
    WhistleTone.OCARINA -> R.string.tone_ocarina
    WhistleTone.KAZOO -> R.string.tone_kazoo
}
