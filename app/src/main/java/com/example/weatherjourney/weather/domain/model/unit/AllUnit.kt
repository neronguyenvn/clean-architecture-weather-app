package com.example.weatherjourney.weather.domain.model.unit

data class AllUnit(
    val temperature: TemperatureUnit,
    val windSpeed: WindSpeedUnit,
    val pressure: PressureUnit,
    val timeFormat: TimeFormatUnit
)

interface LabeledEnum {
    val label: String
}
