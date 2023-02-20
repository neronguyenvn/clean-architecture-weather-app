package com.example.weatherjourney.weather.domain.model.unit

enum class WindSpeedUnit(val label: String, val apiParam: String) {
    KILOMETER_PER_HOUR("km/h", "kmh"),
    METER_PER_SECOND("m/s", "ms"),
    MILE_PER_HOUR("mph", "mph"),
    NULL("", "")
}
