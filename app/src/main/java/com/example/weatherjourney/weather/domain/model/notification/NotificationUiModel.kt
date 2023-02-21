package com.example.weatherjourney.weather.domain.model.notification

import androidx.annotation.StringRes
import com.example.weatherjourney.util.UiText

open class NotificationUiModel(
    val firstTimeLine: UiText,
    val secondTimeLine: UiText,
    @StringRes val infoRes: Int,
    @StringRes val adviceRes: Int
)

class UvNotification(
    firstTimeLine: UiText,
    secondTimeLine: UiText,
    infoRes: Int,
    adviceRes: Int
) : NotificationUiModel(firstTimeLine, secondTimeLine, infoRes, adviceRes)

class AqiNotification(
    firstTimeLine: UiText,
    secondTimeLine: UiText,
    infoRes: Int,
    adviceRes: Int,
    val adviceRes2: UiText
) : NotificationUiModel(firstTimeLine, secondTimeLine, infoRes, adviceRes)
