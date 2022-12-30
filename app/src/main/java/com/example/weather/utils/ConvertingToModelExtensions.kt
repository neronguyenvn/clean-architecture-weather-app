package com.example.weather.utils

import com.example.weather.model.geocoding.Location
import com.example.weather.model.weather.DailyWeather
import com.example.weather.model.weather.DailyWeatherApiModel
import kotlin.math.roundToInt

/**
 * Convert Daily Weather Api Model into Business Model one
 */
fun DailyWeatherApiModel.asModel(currentDt: Long): DailyWeather {
    return DailyWeather(
        iconUrl = "$OPENWEATHER_ICON_BASE_URL${weatherItem.first().iconUrl}@2x.png",
        date = dt.toDayNameInWeek(currentDt),
        weather = weatherItem.first().weatherDescription,
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt()
    )
}

/**
 * Convert Android Location to Business Model Location
 */
fun android.location.Location.asModel(): Location {
    return Location(latitude = latitude, longitude = longitude)
}
