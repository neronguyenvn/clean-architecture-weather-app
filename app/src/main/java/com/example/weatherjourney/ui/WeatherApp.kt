package com.example.weatherjourney.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherjourney.weather.presentation.weatherinfo.WeatherInfoScreen

@Composable
fun WeatherApp(
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        WeatherInfoScreen()
    }
}
