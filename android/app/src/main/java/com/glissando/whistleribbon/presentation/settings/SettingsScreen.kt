package com.glissando.whistleribbon.presentation.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.glissando.whistleribbon.core.ui.ScreenScaffold
import com.glissando.whistleribbon.core.ui.SecondaryButton
import com.glissando.whistleribbon.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory())
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        state = state,
        onOffset = settingsViewModel::setAudioOffset,
        onVolume = settingsViewModel::setVolume,
        onStripSide = settingsViewModel::setStripOnRight,
        onReducedMotion = settingsViewModel::setReducedMotion,
        onAskReset = settingsViewModel::askReset,
        onDismissReset = settingsViewModel::dismissReset,
        onConfirmReset = settingsViewModel::confirmResetProgress,
        onMenu = onMenu
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onOffset: (Float) -> Unit,
    onVolume: (Float) -> Unit,
    onStripSide: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onAskReset: () -> Unit,
    onDismissReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        overlay = Brush.verticalGradient(
            listOf(
                BackgroundTop.copy(alpha = 0.92f),
                BackgroundBase.copy(alpha = 0.76f),
                BackgroundDeep.copy(alpha = 0.96f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 22.dp)
            )
            SettingRow(label = stringResource(R.string.audio_offset)) {
                Column {
                    Text(
                        text = stringResource(R.string.millis_format, state.audioOffsetMs),
                        style = MaterialTheme.typography.titleSmall,
                        color = Accent
                    )
                    Slider(
                        value = state.audioOffsetMs.toFloat(),
                        onValueChange = onOffset,
                        valueRange = -120f..120f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = Accent,
                            activeTrackColor = Accent,
                            inactiveTrackColor = SurfaceSoft
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow(label = stringResource(R.string.volume)) {
                Column {
                    Text(
                        text = stringResource(R.string.percent_format, state.volume),
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary
                    )
                    Slider(
                        value = state.volume.toFloat(),
                        onValueChange = onVolume,
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = SurfaceSoft
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingRow(label = stringResource(R.string.strip_side)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SideSegment(
                        label = stringResource(R.string.left),
                        selected = !state.stripOnRight,
                        onClick = { onStripSide(false) }
                    )
                    SideSegment(
                        label = stringResource(R.string.right),
                        selected = state.stripOnRight,
                        onClick = { onStripSide(true) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            val motionState = stringResource(
                if (state.reducedMotion) R.string.state_on else R.string.state_off
            )
            SettingRow(label = stringResource(R.string.reduced_motion)) {
                Switch(
                    checked = state.reducedMotion,
                    onCheckedChange = onReducedMotion,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SurfaceWhite,
                        checkedTrackColor = Accent,
                        uncheckedTrackColor = SurfaceSoft
                    ),
                    modifier = Modifier.semantics { stateDescription = motionState }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onAskReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_progress),
                    style = MaterialTheme.typography.titleSmall,
                    color = Primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.menu),
                modifier = Modifier.fillMaxWidth(),
                onClick = onMenu
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (state.confirmReset) {
        ConfirmResetDialog(onConfirm = onConfirmReset, onDismiss = onDismissReset)
    }
}

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    ClaySurface(
        modifier = Modifier.fillMaxWidth(),
        radius = 22.dp,
        borderTint = SurfaceSoft,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(104.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                control()
            }
        }
    }
}

@Composable
private fun SideSegment(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) Accent.copy(alpha = 0.2f) else SurfaceWhite.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp)
            .semantics { stateDescription = label },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Accent else TextMuted
        )
    }
}
