package com.example.weatherjourney.features.weather.domain.model.unit

enum class TimeFormatUnit(override val label: String) : LabeledEnum {
    TWENTY_FOUR("24-hour"),
    AM_PM("12-hour"),
    NULL(""),
}
