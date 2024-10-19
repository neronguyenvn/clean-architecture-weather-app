package com.example.weatherjourney.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.weatherjourney.app.ui.WtnApp
import com.example.weatherjourney.presentation.theme.WtnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use WorkManager to start a sync work
        viewModel.refreshAllWeather()
        setContent {
            WtnTheme {
                WtnApp()
            }
        }
    }
}
