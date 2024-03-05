package com.example.weatherjourney.core.model.unit

enum class TimeFormatUnit(override val label: String) : LabeledEnum {
    TWENTY_FOUR("24-hour"),
    AM_PM("12-hour"),
    NULL(""),
}
