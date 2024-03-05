package com.example.weatherjourney.core.datastore.model

import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.core.model.unit.PressureUnit
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.core.model.unit.TimeFormatUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KILOMETER_PER_HOUR,
    val pressureUnit: PressureUnit = PressureUnit.HECTOPASCAL,
    val timeFormatUnit: TimeFormatUnit = TimeFormatUnit.AM_PM,
)

fun UserPreferences.toAllUnit() = AllUnit(
    temperature = temperatureUnit,
    windSpeed = windSpeedUnit,
    pressure = pressureUnit,
    timeFormat = timeFormatUnit
)