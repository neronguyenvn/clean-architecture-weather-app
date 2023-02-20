package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.weather.domain.model.weather.HourlyWeather

data class WeatherInfoUiState(
    val cityAddress: String = "",
    val temperatureLabel: String = "",
    val windSpeedLabel: String = "",
    val weatherState: WeatherState = WeatherState(),
    val isLoading: Boolean = false
)

data class WeatherState(
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val listHourly: List<HourlyWeather> = emptyList()
)
