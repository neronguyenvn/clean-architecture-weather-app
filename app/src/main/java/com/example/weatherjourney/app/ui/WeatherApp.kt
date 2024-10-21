package com.example.weatherjourney.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherjourney.app.navigation.WeatherNavGraph

@Composable
fun WeatherApp() {
    Scaffold { padding ->
        WeatherNavGraph(modifier = Modifier.padding(padding))
    }
}
