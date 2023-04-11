package com.example.weatherjourney.features.recommendation.domain.model

import androidx.annotation.StringRes

data class UvRecommendation(
    val firstTimePeriod: String,
    val secondTimePeriod: String,
    @StringRes val infoRes: Int,
    @StringRes val recommendationRes: Int
)
