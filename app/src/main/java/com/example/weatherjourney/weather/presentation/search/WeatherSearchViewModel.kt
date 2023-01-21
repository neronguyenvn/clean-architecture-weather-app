package com.example.weatherjourney.weather.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherSearchViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(WeatherSearchUiState())
        private set
}
