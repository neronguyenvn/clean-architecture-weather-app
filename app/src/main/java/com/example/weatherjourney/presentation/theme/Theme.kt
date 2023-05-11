package com.example.weatherjourney.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppLightColorScheme = lightColorScheme().copy(
    surface = DeepBlue,
    primaryContainer = DeepBlue,
    surfaceVariant = DeepBlue,
    background = DarkBlue,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun WeatherTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = AppLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
