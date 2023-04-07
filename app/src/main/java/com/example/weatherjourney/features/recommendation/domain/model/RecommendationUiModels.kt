package com.example.weatherjourney.features.recommendation.domain.model

import androidx.annotation.StringRes

open class RecommendationUiModel(
    val firstTimeLine: String,
    val secondTimeLine: String,
    @StringRes val infoRes: Int,
    @StringRes val adviceRes: Int
)

class UvRecommendation(
    firstTimeLine: String,
    secondTimeLine: String,
    infoRes: Int,
    adviceRes: Int
) : RecommendationUiModel(firstTimeLine, secondTimeLine, infoRes, adviceRes)

class AqiRecommendation(
    firstTimeLine: String,
    secondTimeLine: String,
    infoRes: Int,
    adviceRes: Int,
    @StringRes val adviceRes2: Int?
) : RecommendationUiModel(firstTimeLine, secondTimeLine, infoRes, adviceRes)
