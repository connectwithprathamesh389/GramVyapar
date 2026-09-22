package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AgriGreenLight,
    onPrimary = Color.Black,
    primaryContainer = AgriGreenDark,
    onPrimaryContainer = AgriGreenContainer,
    secondary = SaffronAccentLight,
    onSecondary = Color.Black,
    secondaryContainer = OnSaffronContainer,
    onSecondaryContainer = SaffronContainer,
    background = Color(0xFF131713),
    surface = Color(0xFF1B201B),
    onBackground = Color(0xFFE2E7E2),
    onSurface = Color(0xFFE2E7E2),
    outline = Color(0xFF3F4B3F)
)

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = OnAgriGreenContainer,
    secondary = SaffronAccent,
    onSecondary = Color.White,
    secondaryContainer = SaffronContainer,
    onSecondaryContainer = OnSaffronContainer,
    tertiary = Terracotta,
    background = RuralBackground,
    surface = RuralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = RuralCardBorder,
    surfaceVariant = Color(0xFFF0F4EF),
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive rural branding
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
