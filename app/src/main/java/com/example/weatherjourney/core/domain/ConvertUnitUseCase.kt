package com.example.weatherjourney.core.domain

import com.example.weatherjourney.core.common.util.TimeUtils
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TemperatureUnit.*
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.Weather
import com.example.weatherjourney.core.model.WindSpeedUnit
import com.example.weatherjourney.core.model.convertPressure
import com.example.weatherjourney.core.model.convertTemperature
import com.example.weatherjourney.core.model.convertTimeFormat
import com.example.weatherjourney.core.model.convertWindSpeed
import javax.inject.Inject

class ConvertUnitUseCase @Inject constructor() {

    operator fun invoke(
        weather: Weather,
        temperatureUnit: TemperatureUnit? = null,
        windSpeedUnit: WindSpeedUnit? = null,
        pressureUnit: PressureUnit? = null,
        timeFormatUnit: TimeFormatUnit? = null
    ): Weather {

        return weather.apply {
            temperatureUnit?.let { convertTemperatureIfNeeded(it) }
            windSpeedUnit?.let { convertWindSpeedIfNeeded(it) }
            pressureUnit?.let { convertPressureIfNeeded(it) }
            timeFormatUnit?.let { convertTimeFormatIfNeeded(it) }
        }
    }

    private fun Weather.convertTemperatureIfNeeded(unit: TemperatureUnit): Weather {
        return when (unit) {
            Fahrenheit -> this.convertTemperature(::convertCelsiusToFahrenheit)
            Celsius -> this
        }
    }

    private fun Weather.convertWindSpeedIfNeeded(unit: WindSpeedUnit): Weather {
        return when (unit) {
            WindSpeedUnit.MeterPerSecond -> this.convertWindSpeed(::convertKmhToMs)
            WindSpeedUnit.MilePerHour -> this.convertWindSpeed(::convertKmhToMph)
            WindSpeedUnit.KilometerPerHour -> this
        }
    }

    private fun Weather.convertPressureIfNeeded(unit: PressureUnit): Weather {
        return when (unit) {
            PressureUnit.InchOfMercury -> this.convertPressure(::convertHPaToInHg)
            else -> this
        }
    }

    private fun Weather.convertTimeFormatIfNeeded(unit: TimeFormatUnit): Weather {
        return when (unit) {
            TimeFormatUnit.AmPm -> this.convertTimeFormat(TimeUtils::formatTimeToAmPm)
            TimeFormatUnit.TwentyFour -> this.convertTimeFormat(TimeUtils::formatTimeTo24Hour)
        }
    }

    private fun convertCelsiusToFahrenheit(celsius: Double) = celsius * 9 / 5 + 32
    private fun convertKmhToMs(kmh: Double) = kmh / 3.6
    private fun convertKmhToMph(kmh: Double) = kmh / 1.609344
    private fun convertHPaToInHg(hPa: Double) = hPa * 0.02953
}
