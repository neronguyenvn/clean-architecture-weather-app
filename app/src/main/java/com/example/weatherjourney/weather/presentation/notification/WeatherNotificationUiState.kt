package com.example.weatherjourney.weather.presentation.notification

import com.example.weatherjourney.weather.domain.model.UvAdvice

data class WeatherNotificationUiState(
    val isLoading: Boolean = false,
    val weatherAdviceState: WeatherAdviceState = WeatherAdviceState()
)

data class WeatherAdviceState(
    val uvAdvice: UvAdvice? = null
)
