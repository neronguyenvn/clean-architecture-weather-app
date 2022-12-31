package com.example.weather.utils

import com.example.weather.model.geocoding.Location
import com.example.weather.model.weather.DailyWeather
import com.example.weather.model.weather.DailyWeatherApiModel
import kotlin.math.roundToInt

/**
 * Convert Daily Weather Api Model into Ui Model one
 */
fun DailyWeatherApiModel.toUiModel(currentDt: Long): DailyWeather {
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
fun android.location.Location.toUiModel(): Location {
    return Location(latitude = latitude, longitude = longitude)
}
