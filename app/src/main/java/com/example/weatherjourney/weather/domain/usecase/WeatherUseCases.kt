package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.usecase.weather.GetAllWeather
import com.example.weatherjourney.weather.domain.usecase.weather.GetAllWeatherAndCacheLastInfo
import com.example.weatherjourney.weather.domain.usecase.weather.GetWeatherAdvices

data class WeatherUseCases(
    val getAllWeatherAndCacheLastInfo: GetAllWeatherAndCacheLastInfo,
    val getAllWeather: GetAllWeather,
    val getWeatherAdvices: GetWeatherAdvices
)
