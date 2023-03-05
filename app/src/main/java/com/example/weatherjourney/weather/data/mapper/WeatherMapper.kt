package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.DATE_PATTERN
import com.example.weatherjourney.util.HOUR_PATTERN
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

fun AllWeatherDto.toAllWeather(cityAddress: String, timeZone: String): AllWeather = AllWeather(
    cityAddress = cityAddress,
    current = this.hourly.run {
        CurrentWeather(
            date = getCurrentDate(timeZone, DATE_PATTERN),
            temp = temperatures[0],
            windSpeed = windSpeeds[0],
            humidity = humidities[0],
            pressure = pressures[0],
            weatherType = WeatherType.fromWMO(weatherCodes[0])
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
                temp = temperatures[index],
                windSpeed = windSpeeds[index],
                weatherType = WeatherType.fromWMO(weatherCodes[index])
            )
        }
    }
)
