package com.glissando.whistleribbon.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

const val PRESET_NAME = "CANDY_SWEET"

private val RibbonColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnPrimary,
    secondary = Accent,
    onSecondary = SurfaceWhite,
    secondaryContainer = AccentLight,
    onSecondaryContainer = TextPrimary,
    tertiary = Gold,
    onTertiary = TextPrimary,
    background = BackgroundBase,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceSoft,
    outlineVariant = BackgroundDeep,
    error = PrimaryDeep,
    onError = OnPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RibbonColorScheme,
        typography = AppTypography,
        content = content
    )
}
