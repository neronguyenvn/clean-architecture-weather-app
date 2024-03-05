package com.example.weatherjourney.core.model.unit

enum class PressureUnit(override val label: String) : LabeledEnum {
    HECTOPASCAL("hPa"),
    INCH_OF_MERCURY("inHg"),
    NULL(""),
}
