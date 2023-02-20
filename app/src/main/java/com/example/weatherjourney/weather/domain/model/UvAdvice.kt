package com.example.weatherjourney.weather.domain.model

import androidx.annotation.StringRes
import com.example.weatherjourney.util.UiText

data class UvAdvice(
    val firstTimeLine: UiText,
    val secondTimeLine: UiText,
    @StringRes val infoRes: Int,
    @StringRes val adviceRes: Int
)
