package com.example.weatherjourney.weather.data.mapper

import androidx.annotation.StringRes
import com.example.weatherjourney.R
import com.example.weatherjourney.util.DATE_PATTERN
import com.example.weatherjourney.util.filterPastHours
import com.example.weatherjourney.util.toDate
import com.example.weatherjourney.weather.data.remote.dto.HourlyAirQuality
import com.example.weatherjourney.weather.domain.model.UvAdvice
import com.example.weatherjourney.weather.presentation.notification.WeatherAdviceState
import kotlin.math.roundToInt

fun HourlyAirQuality.toWeatherAdviceState(timeZone: String): WeatherAdviceState {
    val newTimeList = time.filterPastHours()
    val newUvIndexList = uvIndexList.takeLast(newTimeList.size)

    for (i in 0..(newTimeList.size - 2)) {
        val currentUvLevel = newUvIndexList[i].toUvLevel()

        if (currentUvLevel != newUvIndexList[i + 1].toUvLevel()) {
            return WeatherAdviceState(
                uvAdvice = UvAdvice(
                    firstTimeLine = newTimeList[0].toDate(timeZone, DATE_PATTERN),
                    secondTimeLine = newTimeList[i].toDate(timeZone, DATE_PATTERN),
                    infoRes = currentUvLevel.infoRes,
                    adviceRes = currentUvLevel.adviceRes
                )
            )
        }
    }

    return WeatherAdviceState()
}

enum class UvLevel(
    val range: IntRange,
    @StringRes val infoRes: Int,
    @StringRes val adviceRes: Int
) {
    LEVEL_1(0..2, R.string.level_one_uv_info, R.string.level_one_uv_advice),
    LEVEL_2(3..7, R.string.level_two_uv_info, R.string.level_two_uv_advice),
    LEVEL_3(8..12, R.string.level_three_uv_info, R.string.level_three_uv_advice),
    NULL(-1..-1, 0, 0)
}

fun Double.toUvLevel(): UvLevel {
    for (level in UvLevel.values()) {
        if (this.roundToInt() in level.range) return level
    }
    return UvLevel.NULL
}
