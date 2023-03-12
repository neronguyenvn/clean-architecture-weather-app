package com.example.weatherjourney.weather.domain.model.unit

data class AllUnit(
    val temperature: TemperatureUnit,
    val windSpeed: WindSpeedUnit,
    val pressure: PressureUnit
)

interface LabeledEnum {
    val label: String
}
