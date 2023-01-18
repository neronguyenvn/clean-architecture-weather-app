package com.example.weatherjourney.util

import com.example.weatherjourney.model.data.Coordinate
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather
import com.example.weatherjourney.weather.data.source.remote.dto.CurrentWeatherDto
import com.example.weatherjourney.weather.data.source.remote.dto.DailyWeatherDto
import com.example.weatherjourney.weather.data.source.remote.dto.HourlyWeatherDto
import com.example.weatherjourney.weather.data.source.remote.dto.Temp
import com.example.weatherjourney.weather.domain.model.Coordinate

val allWeather1 = AllWeather(
    current = CurrentWeatherDto(
        timestamp = 1672915075,
        temp = 26.5
    ),
    hourly = listOf(
        HourlyWeatherDto(
            timestamp = 1672912800,
            temp = 25.63
        )
    ),
    daily = listOf(
        DailyWeatherDto(
            timestamp = 1672891200,
            temp = Temp(max = 25.94, min = 20.59)
        )
    ),
    timezoneOffset = 25200
)

val coordinate1 = Coordinate(10.873, 106.742)
const val city1 = "Thu Duc City"
