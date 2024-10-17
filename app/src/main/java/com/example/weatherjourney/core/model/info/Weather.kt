package com.example.weatherjourney.core.model.info

data class Weather(
    val current: CurrentWeather,
    val listDaily: List<DailyWeather>,
    val listHourly: List<HourlyWeather>,
)

fun Weather.convertTemperature(convertMethod: (Double) -> Double) = this.copy(
    current = current.copy(
        temp = convertMethod(current.temp),
    ),
    listDaily = listDaily.map {
        it.copy(
            maxTemp = convertMethod(it.maxTemp),
            minTemp = convertMethod(it.minTemp),
        )
    },
    listHourly = listHourly.map {
        it.copy(
            temp = convertMethod(it.temp),
        )
    },
)

fun Weather.convertWindSpeed(convertMethod: (Double) -> Double) = this.copy(
    current = current.copy(
        windSpeed = convertMethod(current.windSpeed),
    ),
    listHourly = listHourly.map {
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
