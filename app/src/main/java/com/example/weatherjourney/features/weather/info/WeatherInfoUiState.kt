package com.example.weatherjourney.features.weather.info

import com.example.weatherjourney.core.common.util.UserMessage
import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.core.model.weather.AllWeather

data class WeatherInfoUiState(
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val isCurrentLocation: Boolean = false,
    val cityAddress: String = "",
    val allUnit: AllUnit? = null,
    val allWeather: AllWeather = AllWeather(),
)

