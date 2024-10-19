package com.example.weatherjourney.core.model

data class Weather(
    val current: CurrentWeather,
    val dailyForecasts: List<DailyWeather>,
    val hourlyForecasts: List<HourlyWeather>,
)

fun Weather.convertTemperature(convertMethod: (Double) -> Double) = this.copy(
    current = current.copy(
        temp = convertMethod(current.temp),
    ),
    dailyForecasts = dailyForecasts.map {
        it.copy(
            maxTemp = convertMethod(it.maxTemp),
            minTemp = convertMethod(it.minTemp),
        )
    },
    hourlyForecasts = hourlyForecasts.map {
        it.copy(
            temp = convertMethod(it.temp),
        )
    },
)

fun Weather.convertWindSpeed(convertMethod: (Double) -> Double) = this.copy(
    current = current.copy(
        windSpeed = convertMethod(current.windSpeed),
    ),
    hourlyForecasts = hourlyForecasts.map {
        it.copy(windSpeed = convertMethod(it.windSpeed))
    },
)

fun Weather.convertPressure(convertMethod: (Double) -> Double) = this.copy(
    current = current.copy(
        pressure = convertMethod(current.pressure),
    ),
)

fun Weather.convertTimeFormat(convertMethod: (String) -> String) = this.copy(
    current = current.copy(
        date = convertMethod(current.date),
    ),
)
