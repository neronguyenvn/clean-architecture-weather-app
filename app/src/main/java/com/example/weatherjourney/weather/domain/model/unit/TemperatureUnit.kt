package com.example.weatherjourney.weather.domain.model.unit

enum class TemperatureUnit(override val label: String) : LabeledEnum {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    NULL("")
}
