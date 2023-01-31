package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.DATE_PATTERN
import com.example.weatherjourney.util.DAY_NAME_IN_WEEK_PATTERN
import com.example.weatherjourney.util.HOUR_PATTERN
import com.example.weatherjourney.util.capitalizeByWord
import com.example.weatherjourney.util.toDateString
import com.example.weatherjourney.weather.data.source.remote.ApiService
import com.example.weatherjourney.weather.data.source.remote.dto.CurrentWeatherDto
import com.example.weatherjourney.weather.data.source.remote.dto.DailyWeatherDto
import com.example.weatherjourney.weather.data.source.remote.dto.HourlyWeatherDto
import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.DailyWeather
import com.example.weatherjourney.weather.domain.model.HourlyWeather
import kotlin.math.roundToInt

fun DailyWeatherDto.toDailyWeather(timezoneOffset: Int): DailyWeather {
    return DailyWeather(
        date = timestamp.toDateString(timezoneOffset, DAY_NAME_IN_WEEK_PATTERN),
        weather = weatherItem.first().description.capitalizeByWord(),
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt(),
        imageUrl = getImageUrl(weatherItem.first().imageUri)
    )
}

fun CurrentWeatherDto.toCurrentWeather(
    timezoneOffset: Int,
    precipitationChance: Double
): CurrentWeather {
    return CurrentWeather(
        date = timestamp.toDateString(timezoneOffset, DATE_PATTERN),
        temp = temp.roundToInt(),
        weather = weatherItem.first().description.capitalizeByWord(),
        imageUrl = getImageUrl(weatherItem.first().imageUri),
        realFeelTemp = realFeelTemp.roundToInt(),
        humidity = humidity,
        rainChance = (precipitationChance * 100).toInt(),
        pressure = pressure,
        visibility = visibility,
        uvIndex = uvi.roundToInt()
    )
}

fun HourlyWeatherDto.toHourlyWeather(timezoneOffset: Int): HourlyWeather {
    return HourlyWeather(
        date = timestamp.toDateString(timezoneOffset, HOUR_PATTERN),
        temp = temp.roundToInt(),
        windSpeed = windSpeed.roundToInt(),
        imageUrl = getImageUrl(weatherItem.first().imageUri)
    )
}

private fun getImageUrl(uri: String) = "${ApiService.OPENWEATHER_IMAGE_BASE_URL}$uri@2x.png"
