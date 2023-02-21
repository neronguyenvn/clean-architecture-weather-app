package com.example.weatherjourney.weather.domain.model.notification

import androidx.annotation.StringRes
import com.example.weatherjourney.R
import com.example.weatherjourney.util.UiText

@OptIn(ExperimentalStdlibApi::class)
enum class AqiLevel(
    val indexRange: IntRange,
    @StringRes val infoRes: Int,
    @StringRes val generalPopluationAdviceRes: Int,
    val sensitivePopluationAdvice: UiText
) {
    GOOD(0..<20, R.string.good_aqi_info, R.string.good_aqi_advice, UiText.DynamicString("")),
    FAIR(
        20..<40,
        R.string.fair_aqi_info,
        R.string.fair_and_general_population_moderate_aqi_advice,
        UiText.DynamicString("")
    ),
    MODERATE(
        40..<60,
        R.string.moderate_aqi_info,
        R.string.fair_and_general_population_moderate_aqi_advice,
        UiText.StringResource(R.string.sensitive_population_moderate_aqi_advice)
    ),
    POOR(
        60..<80,
        R.string.poor_aqi_info,
        R.string.general_population_poor_and_very_poor_aqi_advice,
        UiText.StringResource(R.string.sensitive_population_poor_aqi_advice)
    ),
    VERY_POOR(
        80..<100,
        R.string.very_poor_aqi_info,
        R.string.general_population_poor_and_very_poor_aqi_advice,
        UiText.StringResource(R.string.sensitive_population_very_poor_aqi_advice)
    ),
    EXTREMELY_POOR(
        100..Int.MAX_VALUE,
        R.string.extremely_poor_aqi_info,
        R.string.general_population_extremely_poor_aqi_advice,
        UiText.StringResource(R.string.sensitive_population_extremely_poor_aqi_advice)
    ),
    NULL(-1..-1, 0, 0, UiText.DynamicString(""))
}

fun Int.toAqiLevel(): AqiLevel {
    for (level in AqiLevel.values()) {
        if (this in level.indexRange) return level
    }
    return AqiLevel.NULL
}
