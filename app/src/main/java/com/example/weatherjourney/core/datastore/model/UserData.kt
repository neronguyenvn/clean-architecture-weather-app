package com.example.weatherjourney.core.datastore.model

import kotlinx.serialization.Serializable
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit

@Serializable
data class UserData(
    val temperatureUnit: TemperatureUnit,
    val windSpeedUnit: WindSpeedUnit,
    val pressureUnit: PressureUnit,
    val timeFormatUnit: TimeFormatUnit
) {
    companion object {
        val default = UserData(
            temperatureUnit = TemperatureUnit.CELSIUS,
            windSpeedUnit = WindSpeedUnit.KILOMETER_PER_HOUR,
            pressureUnit = PressureUnit.HECTOPASCAL,
            timeFormatUnit = TimeFormatUnit.AM_PM
        )
    }
}
