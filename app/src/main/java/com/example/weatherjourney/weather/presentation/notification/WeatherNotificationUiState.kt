package com.example.weatherjourney.weather.presentation.notification

import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.weather.domain.model.notification.AqiNotification
import com.example.weatherjourney.weather.domain.model.notification.UvNotification

data class WeatherNotificationUiState(
    val notifications: WeatherNotifications? = null,
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null
)

data class WeatherNotifications(
    val uvNotification: UvNotification? = null,
    val aqiNotification: AqiNotification? = null
)
