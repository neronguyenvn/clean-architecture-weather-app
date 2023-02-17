package com.example.weatherjourney.weather.domain.usecase

data class WeatherUseCases(
    val getAllWeatherAndCacheLastInfo: GetAllWeatherAndCacheLastInfo,
    val getAllWeather: GetAllWeather
)
