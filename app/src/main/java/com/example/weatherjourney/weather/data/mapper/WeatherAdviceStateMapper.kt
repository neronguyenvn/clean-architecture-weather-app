package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.filterPastHours
import com.example.weatherjourney.util.toDate
import com.example.weatherjourney.weather.data.remote.dto.HourlyAirQuality
import com.example.weatherjourney.weather.domain.model.notification.AqiNotification
import com.example.weatherjourney.weather.domain.model.notification.UvNotification
import com.example.weatherjourney.weather.domain.model.notification.toAqiLevel
import com.example.weatherjourney.weather.domain.model.notification.toUvIndexLevel
import com.example.weatherjourney.weather.presentation.notification.WeatherNotifications
import com.example.weatherjourney.weather.util.DATE_24_PATTERN

fun HourlyAirQuality.toWeatherAdviceState(timeZone: String): WeatherNotifications {
    val newTimeList = time.filterPastHours()
    val newUvIndexList = uvIndexList.takeLast(newTimeList.size).filterNotNull()
    val newAqiList = europeanAqiList.takeLast(newTimeList.size).filterNotNull()

    var notificationState = WeatherNotifications()
    val lastIndex = newUvIndexList.size - 2

    for (i in 0..lastIndex) {
        val currentUvLevel = newUvIndexList[i].toUvIndexLevel()
        val currentAqiLevel = newAqiList[i].toAqiLevel()

        if (currentUvLevel != newUvIndexList[i + 1].toUvIndexLevel() ||
            (i == lastIndex && notificationState.uvNotification == null)
        ) {
            notificationState = notificationState.copy(
                uvNotification = UvNotification(
                    firstTimeLine = newTimeList[0].toDate(timeZone, DATE_24_PATTERN),
                    secondTimeLine = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                    infoRes = currentUvLevel.infoRes,
                    adviceRes = currentUvLevel.adviceRes
                )
            )
        }

        if (currentAqiLevel != newAqiList[i + 1].toAqiLevel() ||
            (i == lastIndex && notificationState.aqiNotification == null)
        ) {
            notificationState = notificationState.copy(
                aqiNotification = AqiNotification(
                    firstTimeLine = newTimeList[0].toDate(timeZone, DATE_24_PATTERN),
                    secondTimeLine = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                    infoRes = currentAqiLevel.infoRes,
                    adviceRes = currentAqiLevel.generalPopluationAdviceRes,
                    adviceRes2 = currentAqiLevel.sensitivePopluationAdvice
                )
            )
        }
    }

    return notificationState
}
