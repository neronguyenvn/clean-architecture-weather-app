package com.example.weatherjourney.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.weatherjourney.app.ui.WeatherApp
import com.example.weatherjourney.presentation.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // TODO: Use WorkManager to start a sync work
        viewModel.refreshAllWeather()
        setContent {
            WeatherTheme {
                WeatherApp()
            }
        }
    }
}
