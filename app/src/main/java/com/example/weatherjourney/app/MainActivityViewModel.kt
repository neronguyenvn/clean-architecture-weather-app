package com.example.weatherjourney.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.core.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    fun refreshAllWeather() {
        viewModelScope.launch {
            weatherRepository.refreshWeatherOfLocations()
        }
    }
}
