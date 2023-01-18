package com.example.weatherjourney.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherjourney.ui.theme.WeatherTheme
import com.example.weatherjourney.util.setUpEdgeToEdge
import com.example.weatherjourney.weather.presentation.weatherinfo.WeatherInfoEvent
import com.example.weatherjourney.weather.presentation.weatherinfo.WeatherInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherInfoViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpEdgeToEdge()
        installSplashScreen().apply {
            checkAndInitWeatherState()
            setKeepOnScreenCondition {
                viewModel.isLastWeatherInfoLoading.value
            }
        }
        super.onCreate(savedInstanceState)

        checkAndInitWeatherState()


        setContent {
            WeatherTheme {
                WeatherApp()
            }
        }
    }

    private fun checkAndInitWeatherState() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.onEvent(WeatherInfoEvent.OnActivityCreate)
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }
}
