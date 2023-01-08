package com.example.weather.util

import com.example.weather.R
import com.example.weather.model.data.Coordinate
import com.example.weather.model.data.CurrentWeatherApiModel
import com.example.weather.model.data.DailyWeatherApiModel
import com.example.weather.model.data.Location
import com.example.weather.model.ui.CurrentWeather
import com.example.weather.model.ui.DailyWeather
import kotlin.math.roundToInt

/**
 * Convert Daily Weather Api Model into Ui Model one.
 */
fun DailyWeatherApiModel.toUiModel(timezoneOffset: Int): DailyWeather {
    return DailyWeather(
        iconUrl = "$OPENWEATHER_ICON_BASE_URL${weatherItem.first().iconUrl}@2x.png",
        date = timestamp.toDayNameInWeek(timezoneOffset),
        weather = weatherItem.first().weatherDescription,
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt()
    )
}

/**
 * Convert Android Location to Ui Model Location.
 */
fun android.location.Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

/**
 * Convert Database Model Location to Ui Model Location.
 */
fun Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

/**
 * Convert Ui Model Location to Database Model Location.
 */
fun Coordinate.toLocation(city: String): Location {
    return Location(city = city, latitude = latitude, longitude = longitude)
}

/**
 * Round Coordinate to make it has united number of digits after decimal point.
 * Object that can be unambiguously recognized at this scale (3): neighborhood, street.
 */
fun Coordinate.toUnifiedCoordinate(): Coordinate {
    return Coordinate(
        latitude = latitude.roundTo(DECIMAL_DEGREE_PRECISION),
        longitude = longitude.roundTo(DECIMAL_DEGREE_PRECISION)
    )
}

fun CurrentWeatherApiModel.toUiModel(timezoneOffset: Int): CurrentWeather {
    return CurrentWeather(
        date = timestamp.toDateString(timezoneOffset, DATE_PATTERN),
        temp = temp.roundToInt(),
        weather = weatherItem.first().weatherDescription,
        bgImg = selectBackgroundImage(this)
    )
}

private fun selectBackgroundImage(current: CurrentWeatherApiModel): Int {
    current.apply {
        val weatherDescription = weatherItem.first().weatherDescription

        return if (sunriseTimestamp != null && sunsetTimestamp != null) {
            if (timestamp in sunriseTimestamp..sunsetTimestamp) {
                when (weatherDescription) {
                    "Thunderstorm", "Drizzle", "Rain" -> R.drawable.day_rain
                    "Snow" -> R.drawable.day_snow
                    "Clear" -> R.drawable.day_clearsky
                    "Cloud" -> R.drawable.day_cloudy
                    else -> R.drawable.day_other_atmosphere
                }
            } else {
                when (weatherDescription) {
                    "Thunderstorm", "Drizzle", "Rain" -> R.drawable.night_rain
                    "Snow" -> R.drawable.night_snow
                    "Clear" -> R.drawable.night_clearsky
                    "Clouds" -> R.drawable.night_cloudy
                    else -> R.drawable.night_other_atmosphere
                }
            }
        } else {
            when (weatherDescription) {
                "Clouds" -> R.drawable.night_cloudy
                "Snow" -> R.drawable.night_snow
                else -> R.drawable.night_snow
            }
        }
    }
}
