package com.example.weather.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weather.ui.screens.HomeScreen

/**
 * Main Ui component for the entire App.
 * Add NavHost here for later navigation.
 */
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        HomeScreen()
    }
}
