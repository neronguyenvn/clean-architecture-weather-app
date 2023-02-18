package com.example.weatherjourney.weather.domain.model

enum class TemperatureUnit(val label: String, val apiParam: String) {
    CELSIUS("°C", "celsius"),
    FAHRENHEIT("°F", "fahrenheit"),
    NULL("", "")
}
