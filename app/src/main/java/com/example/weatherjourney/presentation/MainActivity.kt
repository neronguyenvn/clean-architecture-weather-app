package com.example.weatherjourney.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherjourney.presentation.theme.WeatherTheme
import com.example.weatherjourney.weather.presentation.info.WeatherInfoEvent
import com.example.weatherjourney.weather.presentation.info.WeatherInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        checkAndInitWeatherState()

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLastWeatherInfoLoading.value
        }

        setContent {
            WeatherTheme {
                WeatherNavGraph()
            }
        }
    }

    private fun checkAndInitWeatherState() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            viewModel.onEvent(WeatherInfoEvent.OnAppInit(isGranted))
        }
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
