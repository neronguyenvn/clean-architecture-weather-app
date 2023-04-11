package com.example.weatherjourney.features.recommendation.domain.model

import androidx.annotation.StringRes

data class AqiRecommendation(
    val firstTimePeriod: String,
    val secondTimePeriod: String,
    @StringRes val infoRes: Int,
    @StringRes val generalPopulationRecommendationRes: Int,
    @StringRes val sensitivePopulationRecommendationRes: Int?
)
