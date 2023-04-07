package com.example.weatherjourney.features.weather.domain.model.unit

enum class WindSpeedUnit(override val label: String) : LabeledEnum {
    KILOMETER_PER_HOUR("km/h"),
    METER_PER_SECOND("m/s"),
    MILE_PER_HOUR("mph"),
    NULL("")
}
