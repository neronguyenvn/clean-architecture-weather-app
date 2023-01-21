package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.DailyWeather
import com.example.weatherjourney.weather.domain.model.HourlyWeather

data class WeatherInfoUiState(
    val city: String = "",
    val weatherState: WeatherInfo = WeatherInfo(),
    val isLoading: Boolean = false,
    val isLocationPermissionGranted: Boolean = false
)

data class WeatherInfo(
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val listHourly: List<HourlyWeather> = emptyList()
)
