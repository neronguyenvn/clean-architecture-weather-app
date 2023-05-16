package com.example.weatherjourney.features.recommendation.domain.model

import androidx.annotation.StringRes
import com.example.weatherjourney.R

@Suppress("MagicNumber")
enum class UvIndexLevel(
    val indexRange: IntRange,
    @StringRes val infoRes: Int,
    @StringRes val recommendationRes: Int,
) {
    LEVEL_1(0..2, R.string.level_one_uv_info, R.string.level_one_uv_advice),
    LEVEL_2(3..7, R.string.level_two_uv_info, R.string.level_two_uv_advice),
    LEVEL_3(8..Int.MAX_VALUE, R.string.level_three_uv_info, R.string.level_three_uv_advice),
    NULL(-1..-1, 0, 0),
}

fun Double.toUvIndexLevel(): UvIndexLevel {
    for (level in UvIndexLevel.values()) {
        if (this.toInt() in level.indexRange) return level
    }
    return UvIndexLevel.NULL
}
