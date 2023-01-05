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
        ),
        HourlyWeather(
            timestamp = 1672916400,
            temp = 26.5
        )
    ),
    daily = listOf(
        DailyWeatherApiModel(
            timestamp = 1672891200,
            temp = Temp(max = 25.94, min = 20.59)
        ),
        DailyWeatherApiModel(
            timestamp = 1672977600,
            temp = Temp(max = 28.93, min = 20.46)
        )
    ),
    timezoneOffset = 25200
)

val allWeather2 = AllWeather(
    current = CurrentWeatherApiModel(
        timestamp = 1672916022,
        temp = -13.07
    ),
    hourly = listOf(
        HourlyWeather(
            timestamp = 1672912800,
            temp = -12.77
        ),
        HourlyWeather(
            timestamp = 1672916400,
            temp = -12.76
        ),
        HourlyWeather(42141412, 16.3)
    ),
    daily = listOf(
        DailyWeatherApiModel(
            timestamp = 1672941600,
            temp = Temp(max = 0.83, min = -7.63)
        ),
        DailyWeatherApiModel(
            timestamp = 1673028000,
            temp = Temp(max = 1.66, min = -8.01)
        )
    ),
    timezoneOffset = -21600
)

val coordinate1 = Coordinate(10.873, 106.742)
val coordinate2 = Coordinate(39.784, -100.446)

const val city1 = "Thu Duc City"
const val city2 = "Usa"
