package com.example.weatherjourney.features.weather.presentation.info

import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.features.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.features.weather.domain.model.weather.HourlyWeather
import com.example.weatherjourney.util.UserMessage

data class WeatherInfoUiState(
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val isCurrentLocation: Boolean = false,
    val allUnit: AllUnit? = null,
    val allWeather: AllWeather = AllWeather()
)

data class AllWeather(
    val cityAddress: String = "",
    val current: CurrentWeather? = null,
    val listDaily: List<DailyWeather> = emptyList(),
    val listHourly: List<HourlyWeather> = emptyList()
)
