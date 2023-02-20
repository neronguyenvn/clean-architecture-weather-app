package com.example.weatherjourney.weather.presentation.notification

sealed class WeatherNotificationEvent {
    object OnRefresh : WeatherNotificationEvent()
}
