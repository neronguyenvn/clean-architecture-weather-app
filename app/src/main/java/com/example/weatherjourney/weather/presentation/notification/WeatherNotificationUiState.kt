package com.example.weatherjourney.weather.presentation.notification

import com.example.weatherjourney.weather.domain.model.notification.AqiNotification
import com.example.weatherjourney.weather.domain.model.notification.UvNotification

data class WeatherNotificationUiState(
    val isLoading: Boolean = false,
    val weatherNotificationState: WeatherNotificationState = WeatherNotificationState()
)

data class WeatherNotificationState(
    val uvNotification: UvNotification? = null,
    val aqiNotification: AqiNotification? = null
)
