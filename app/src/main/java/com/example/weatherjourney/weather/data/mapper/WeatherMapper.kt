package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.DATE_PATTERN
import com.example.weatherjourney.util.HOUR_PATTERN
import com.example.weatherjourney.util.filterPastHours
import com.example.weatherjourney.util.getCurrentDate
import com.example.weatherjourney.util.toDate
import com.example.weatherjourney.util.toDayNameInWeek
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.data.remote.dto.DailyWeatherDto
import com.example.weatherjourney.weather.data.remote.dto.HourlyWeatherDto
import com.example.weatherjourney.weather.domain.model.WeatherType
import com.example.weatherjourney.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.weather.domain.model.weather.HourlyWeather

fun AllWeather.toCurrentWeather(timeZone: String): CurrentWeather {
    this.hourly.apply {
        return CurrentWeather(
            date = getCurrentDate(timeZone, DATE_PATTERN),
            temp = temperatures[0],
            windSpeed = windSpeeds[0],
            humidity = humidities[0],
            pressure = pressures[0],
            weatherType = WeatherType.fromWMO(weatherCodes[0])
        )
    }
}

fun DailyWeatherDto.toDailyWeather(timeZone: String): List<DailyWeather> {
    return time.mapIndexed { index, time ->
        DailyWeather(
            date = time.toDayNameInWeek(timeZone),
            maxTemp = maxTemperatures[index],
            minTemp = minTemperatures[index],
            weatherType = WeatherType.fromWMO(weatherCodes[index])
        )
    }
}

fun HourlyWeatherDto.toHourlyWeather(timeZone: String): List<HourlyWeather> {
    return time.filterPastHours().mapIndexed { index, time ->
        HourlyWeather(
            date = time.toDate(timeZone, HOUR_PATTERN),
            temp = temperatures[index],
            windSpeed = windSpeeds[index],
            weatherType = WeatherType.fromWMO(weatherCodes[index])
        )
    }
}
