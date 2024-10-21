package com.example.weatherjourney.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DefaultColorScheme = darkColorScheme()

@Composable
fun WeatherTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = DefaultColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
