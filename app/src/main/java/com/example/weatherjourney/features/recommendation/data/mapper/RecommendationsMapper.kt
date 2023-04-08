package com.example.weatherjourney.features.recommendation.data.mapper

import com.example.weatherjourney.constants.DATE_24_PATTERN
import com.example.weatherjourney.features.recommendation.domain.model.AqiRecommendation
import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.features.recommendation.domain.model.UvRecommendation
import com.example.weatherjourney.features.recommendation.domain.model.toAqiLevel
import com.example.weatherjourney.features.recommendation.domain.model.toUvIndexLevel
import com.example.weatherjourney.features.weather.data.remote.dto.HourlyAirQuality
import com.example.weatherjourney.util.filterPastHours
import com.example.weatherjourney.util.isNull
import com.example.weatherjourney.util.toDate

fun HourlyAirQuality.toRecommendations(timeZone: String): Recommendations {
    // Filter time period already passed today
    val newTimeList = time.filterPastHours()
    // Create new lists filter values belong to passed time period
    val newUvIndexList = uvIndexList.takeLast(newTimeList.size).filterNotNull()
    val newAqiList = europeanAqiList.takeLast(newTimeList.size).filterNotNull()

    var recommendations = Recommendations()
    val lastIndex = newUvIndexList.size - 2
    val firstTimeLine = newTimeList[0].toDate(timeZone, DATE_24_PATTERN)

    for (i in 0..lastIndex) {
        val currentUvLevel = newUvIndexList[i].toUvIndexLevel()
        val nextUvLevel = newUvIndexList[i + 1].toUvIndexLevel()
        val currentAqiLevel = newAqiList[i].toAqiLevel()
        val nextAqiLevel = newAqiList[i + 1].toAqiLevel()

        when {
            recommendations.uvRecommendation.isNull() -> {
                // Uv level haven't changed yet and i isn't the last index so continue
                if (currentUvLevel == nextUvLevel || i == lastIndex) {
                    continue
                }

                recommendations = recommendations.copy(
                    uvRecommendation = UvRecommendation(
                        firstTimeLine = firstTimeLine,
                        secondTimeLine = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                        infoRes = currentUvLevel.infoRes,
                        adviceRes = currentUvLevel.adviceRes
                    )
                )
            }

            recommendations.aqiRecommendation.isNull() -> {
                // Aqi level haven't changed yet and i isn't the last index so continue
                if (currentAqiLevel == nextAqiLevel || i == lastIndex) {
                    continue
                }

                recommendations = recommendations.copy(
                    aqiRecommendation = AqiRecommendation(
                        firstTimeLine = firstTimeLine,
                        secondTimeLine = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                        infoRes = currentAqiLevel.infoRes,
                        adviceRes = currentAqiLevel.generalPopulationAdviceRes,
                        adviceRes2 = currentAqiLevel.sensitivePopulationAdvice
                    )
                )
            }
        }
    }

    return recommendations
}
