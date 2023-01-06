package com.example.weather

import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.weather.AllWeather
import com.example.weather.model.weather.CurrentWeatherApiModel
import com.example.weather.model.weather.DailyWeatherApiModel
import com.example.weather.model.weather.HourlyWeather
import com.example.weather.model.weather.Temp

val allWeather1 = AllWeather(
    current = CurrentWeatherApiModel(
        timestamp = 1672915075,
        temp = 26.5
    ),
    hourly = listOf(
        HourlyWeather(
            timestamp = 1672912800,
            temp = 25.63
        )
    ),
    daily = listOf(
        DailyWeatherApiModel(
            timestamp = 1672891200,
            temp = Temp(max = 25.94, min = 20.59)
        )
    ),
    timezoneOffset = 25200
)

val coordinate1 = Coordinate(10.873, 106.742)
const val city1 = "Thu Duc City"
