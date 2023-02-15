package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.DailyWeather
import com.example.weatherjourney.weather.domain.model.HourlyWeather

data class WeatherInfoUiState(
    val cityAddress: String = "",
    val weatherState: WeatherState = WeatherState(),
    val isLoading: Boolean = false
)

data class WeatherState(
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val listHourly: List<HourlyWeather> = emptyList()
)
