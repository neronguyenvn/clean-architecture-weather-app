package com.example.weatherjourney.weather.domain.model.unit

enum class PressureUnit(override val label: String) : LabeledEnum {
    HECTOPASCAL("hPa"),
    INCH_OF_MERCURY("inHg"),
    NULL("")
}
