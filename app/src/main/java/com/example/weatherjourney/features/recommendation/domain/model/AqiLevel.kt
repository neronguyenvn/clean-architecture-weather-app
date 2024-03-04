package com.example.weatherjourney.features.recommendation.domain.model

import androidx.annotation.StringRes
import com.example.weatherjourney.R

@Suppress("MagicNumber")
enum class AqiLevel(
    val indexRange: IntRange,
    @StringRes val infoRes: Int,
    @StringRes val generalPopulationRecommendationRes: Int,
    @StringRes val sensitivePopulationRecommendationRes: Int? = null,
) {
    GOOD(
        0 until 20,
        R.string.good_aqi_info,
        R.string.good_aqi_advice,
    ),
    FAIR(
        20 until 40,
        R.string.fair_aqi_info,
        R.string.fair_and_general_population_moderate_aqi_advice,
    ),
    MODERATE(
        40 until 60,
        R.string.moderate_aqi_info,
        R.string.fair_and_general_population_moderate_aqi_advice,
        R.string.sensitive_population_moderate_aqi_advice,
    ),
    POOR(
        60 until 80,
        R.string.poor_aqi_info,
        R.string.general_population_poor_and_very_poor_aqi_advice,
        R.string.sensitive_population_poor_aqi_advice,
    ),
    VERY_POOR(
        80 until 100,
        R.string.very_poor_aqi_info,
        R.string.general_population_poor_and_very_poor_aqi_advice,
        R.string.sensitive_population_very_poor_aqi_advice,
    ),
    EXTREMELY_POOR(
        100..Int.MAX_VALUE,
        R.string.extremely_poor_aqi_info,
        R.string.general_population_extremely_poor_aqi_advice,
        R.string.sensitive_population_extremely_poor_aqi_advice,
    ),
    NULL(-1..-1, 0, 0),
}

fun Int.toAqiLevel(): AqiLevel {
    for (level in AqiLevel.values()) {
        if (this in level.indexRange) return level
    }
    return AqiLevel.NULL
}
