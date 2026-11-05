package com.glissando.whistleribbon.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glissando.whistleribbon.R
import com.glissando.whistleribbon.core.di.AppViewModelFactory
import com.glissando.whistleribbon.core.theme.Accent
import com.glissando.whistleribbon.core.theme.OnPrimary
import com.glissando.whistleribbon.core.theme.Primary
import com.glissando.whistleribbon.core.theme.PrimaryDeep
import com.glissando.whistleribbon.core.theme.PrimaryLight
import com.glissando.whistleribbon.core.theme.SurfaceSoft
import com.glissando.whistleribbon.core.theme.SurfaceWhite
import com.glissando.whistleribbon.core.ui.ConfettiLayer
import kotlin.math.sin

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }
    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition(label = "splash")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashWave"
    )
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashPulse"
    )
    val discState = remember { MutableTransitionState(false).apply { targetState = true } }
    val titleState = remember { MutableTransitionState(false).apply { targetState = true } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryDeep, Primary, PrimaryLight)))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val amplitude = size.height * 0.07f
            val baseY = size.height * 0.30f + wave * size.height * 0.10f
            val ribbon = Path()
            ribbon.moveTo(-60f, baseY)
            var x = -60f
            while (x < size.width + 60f) {
                val progress = x / size.width
                ribbon.lineTo(x, baseY + sin(progress * 3.2f + wave * 3.1f) * amplitude)
                x += 22f
            }
            drawPath(
                path = ribbon,
                color = SurfaceSoft.copy(alpha = 0.55f),
                style = Stroke(
                    width = 14.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            val under = Path()
            under.moveTo(-60f, baseY + 140f)
            var backX = -60f
            while (backX < size.width + 60f) {
                val progress = backX / size.width
                under.lineTo(
                    backX,
                    baseY + 140f + sin(progress * 2.4f - wave * 2.6f) * amplitude * 0.7f
                )
                backX += 22f
            }
            drawPath(
                path = under,
                color = Accent.copy(alpha = 0.42f),
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            drawLine(
                color = SurfaceWhite.copy(alpha = 0.10f),
                start = Offset(0f, size.height * 0.80f),
                end = Offset(size.width, size.height * 0.80f),
                strokeWidth = 1.dp.toPx()
            )
        }
        ConfettiLayer(pieceCount = 12, alpha = 0.32f, seed = 21)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visibleState = discState,
                enter = scaleIn(spring(dampingRatio = 0.55f, stiffness = 320f)) +
                    fadeIn(tween(durationMillis = 420))
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(SurfaceWhite.copy(alpha = 0.22f))
                        .border(2.dp, SurfaceSoft.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(148.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            AnimatedVisibility(
                visibleState = titleState,
                enter = slideInVertically(
                    animationSpec = tween(durationMillis = 520, delayMillis = 160)
                ) { full -> full / 4 } + fadeIn(tween(durationMillis = 520, delayMillis = 160))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.splash_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = SurfaceWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.splash_tagline),
                        style = MaterialTheme.typography.labelLarge,
                        color = SurfaceSoft,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Accent,
                trackColor = SurfaceWhite.copy(alpha = 0.25f)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = OnPrimary.copy(alpha = 0.88f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
