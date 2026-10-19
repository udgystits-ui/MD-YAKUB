package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AuraColorScheme = darkColorScheme(
    primary = AuraNeonPurple,
    onPrimary = Color.White,
    primaryContainer = AuraSurfaceDark,
    onPrimaryContainer = AuraGlowLight,
    secondary = AuraNeonViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF27134A),
    onSecondaryContainer = AuraGlowLight,
    tertiary = AuraNeonPink,
    onTertiary = Color.White,
    background = AuraDeepBackground,
    onBackground = AuraTextPrimary,
    surface = AuraSurfaceDark,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraCardSurface,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraNeonPurple.copy(alpha = 0.5f),
    outlineVariant = Color(0x33A855F7),
    error = AuraError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraColorScheme,
        typography = Typography,
        content = content
    )
}
