package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.DATE_PATTERN
import com.example.weatherjourney.util.HOUR_PATTERN
import com.example.weatherjourney.util.countPastHoursToday
import com.example.weatherjourney.util.filterPastHours
import com.example.weatherjourney.util.getCurrentDate
import com.example.weatherjourney.util.toDate
import com.example.weatherjourney.util.toDayNameInWeek
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.domain.model.WeatherType
import com.example.weatherjourney.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.weather.domain.model.weather.HourlyWeather
import com.example.weatherjourney.weather.presentation.info.AllWeather

fun AllWeatherDto.toAllWeather(cityAddress: String, timeZone: String): AllWeather {
    val count = this.hourly.time.countPastHoursToday()

    return AllWeather(
        cityAddress = cityAddress,
        current = this.hourly.run {
            CurrentWeather(
                date = getCurrentDate(timeZone, DATE_PATTERN),
                temp = temperatures[count],
                windSpeed = windSpeeds[count],
                humidity = humidities[count],
                pressure = pressures[count],
                weatherType = WeatherType.fromWMO(weatherCodes[count])
            )
        },
        listDaily = this.daily.run {
            time.mapIndexed { index, time ->
                DailyWeather(
                    date = time.toDayNameInWeek(timeZone),
                    maxTemp = maxTemperatures[index],
                    minTemp = minTemperatures[index],
                    weatherType = WeatherType.fromWMO(weatherCodes[index])
                )
            }
        },
        listHourly = this.hourly.run {
            time.filterPastHours().mapIndexed { index, time ->
                HourlyWeather(
                    date = time.toDate(timeZone, HOUR_PATTERN),
                    temp = temperatures[index + count],
                    windSpeed = windSpeeds[index + count],
                    weatherType = WeatherType.fromWMO(weatherCodes[index + count])
                )
            }
        }
    )
}
