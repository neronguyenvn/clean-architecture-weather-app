package com.example.weather.utils

import com.example.weather.model.database.Location
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.weather.DailyWeather
import com.example.weather.model.weather.DailyWeatherApiModel
import kotlin.math.roundToInt

/**
 * Convert Daily Weather Api Model into Ui Model one
 */
fun DailyWeatherApiModel.toCoordinate(currentDt: Long): DailyWeather {
    return DailyWeather(
        iconUrl = "$OPENWEATHER_ICON_BASE_URL${weatherItem.first().iconUrl}@2x.png",
        date = dt.toDayNameInWeek(currentDt),
        weather = weatherItem.first().weatherDescription,
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt()
    )
}

/**
 * Convert Android Location to Ui Model Location
 */
fun android.location.Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

/**
 * Convert Database Model Location to Ui Model Location
 */
fun Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

/**
 * Convert Ui Model Location to Database Model Location
 */
fun Coordinate.toLocation(city: String): Location {
    return Location(city = city, latitude = latitude, longitude = longitude)
}

/**
 * Round Coordinate to make it has united number of digits after decimal point
 */
fun Coordinate.toUnifiedCoordinate(): Coordinate {
    return Coordinate(latitude = latitude.roundTo(4), longitude = longitude.roundTo(4))
}
