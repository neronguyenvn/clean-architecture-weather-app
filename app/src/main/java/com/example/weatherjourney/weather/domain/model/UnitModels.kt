package com.example.weatherjourney.weather.domain.model

enum class TemperatureUnit(val label: String, val apiParam: String) {
    CELSIUS("°C", "celsius"),
    FAHRENHEIT("°F", "fahrenheit"),
    NULL("", "")
}

enum class WindSpeedUnit(val label: String, val apiParam: String) {
    KILOMETER_PER_HOUR("km/h", "kmh"),
    METER_PER_SECOND("m/s", "ms"),
    MILE_PER_HOUR("mph", "mph"),
    NULL("", "")
}
