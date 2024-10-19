package com.example.weatherjourney.core.datastore.model

import com.example.weatherjourney.core.model.unit.PressureUnit
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.core.model.unit.TimeFormatUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import kotlinx.serialization.Serializable

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
