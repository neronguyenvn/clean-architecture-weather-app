package com.example.weatherjourney.features.weather.domain.usecase

import com.example.weatherjourney.features.weather.domain.usecase.weather.ConvertUnit
import com.example.weatherjourney.features.weather.domain.usecase.weather.GetAllWeather

data class WeatherUseCases(
    val getAllWeather: GetAllWeather,
    val convertUnit: ConvertUnit
)
