package com.example.weatherjourney.features.weather.domain.model.unit

enum class TemperatureUnit(override val label: String) : LabeledEnum {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    NULL(""),
}
