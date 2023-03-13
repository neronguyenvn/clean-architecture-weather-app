package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.usecase.weather.ConvertUnit
import com.example.weatherjourney.weather.domain.usecase.weather.GetAllWeather
import com.example.weatherjourney.weather.domain.usecase.weather.GetWeatherNotifications

data class WeatherUseCases(
    val getAllWeather: GetAllWeather,
    val getWeatherNotifications: GetWeatherNotifications,
    val convertUnit: ConvertUnit
)
