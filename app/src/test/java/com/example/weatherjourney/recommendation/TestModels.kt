package com.example.weatherjourney.recommendation

import com.example.weatherjourney.features.recommendation.domain.model.AqiRecommendation
import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.features.recommendation.domain.model.UvRecommendation

val recommendations1 = Recommendations(
    uvRecommendation = UvRecommendation(
        firstTimePeriod = "April 01, 00:00",
        secondTimePeriod = "April 01, 12:00",
        infoRes = 1,
        recommendationRes = 1,
    ),
    aqiRecommendation = AqiRecommendation(
        firstTimePeriod = "April 01, 00:00",
        secondTimePeriod = "April 01, 12:00",
        infoRes = 1,
        generalPopulationRecommendationRes = 1,
        sensitivePopulationRecommendationRes = 1,
    ),
)

val recommendations2 = Recommendations(
    uvRecommendation = UvRecommendation(
        firstTimePeriod = "April 02, 00:00",
        secondTimePeriod = "April 02, 12:00",
        infoRes = 2,
        recommendationRes = 2,
    ),
    aqiRecommendation = AqiRecommendation(
        firstTimePeriod = "April 02, 00:00",
        secondTimePeriod = "April 02, 12:00",
        infoRes = 2,
        generalPopulationRecommendationRes = 2,
        sensitivePopulationRecommendationRes = 2,
    ),
)
