package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.weather.domain.model.weather.HourlyWeather

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
