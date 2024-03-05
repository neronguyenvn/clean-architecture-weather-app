package com.example.weatherjourney.core.model.unit

enum class TemperatureUnit(override val label: String) : LabeledEnum {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    NULL(""),
}
