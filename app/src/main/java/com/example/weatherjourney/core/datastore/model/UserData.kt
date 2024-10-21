package com.example.weatherjourney.core.datastore.model

import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit
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
            temperatureUnit = TemperatureUnit.Celsius,
            windSpeedUnit = WindSpeedUnit.KilometerPerHour,
            pressureUnit = PressureUnit.Hectopascal,
            timeFormatUnit = TimeFormatUnit.AmPm
        )
    }
}
