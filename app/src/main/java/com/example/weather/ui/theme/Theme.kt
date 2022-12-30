package com.example.weather.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DayRainColorPalette = lightColors(
    primary = DarkBlue,
    background = White,
    onBackground = Black
)

/**
 * Material Theme component for the entire App
 */
@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    val colors = DayRainColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
