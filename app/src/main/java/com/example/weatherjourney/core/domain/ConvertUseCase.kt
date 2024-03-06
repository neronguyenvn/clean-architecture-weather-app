package com.example.weatherjourney.core.domain

import com.example.weatherjourney.core.common.constant.DATE_24_PATTERN
import com.example.weatherjourney.core.common.constant.DATE_AM_PM_PATTERN
import com.example.weatherjourney.core.common.constant.GENERAL_TIME_FORMATTER
import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.database.model.toSavedLocation
import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.info.Weather
import com.example.weatherjourney.core.model.info.convertPressureUnit
import com.example.weatherjourney.core.model.info.convertTemperatureUnit
import com.example.weatherjourney.core.model.info.convertTimeFormatUnit
import com.example.weatherjourney.core.model.info.convertWindSpeedUnit
import com.example.weatherjourney.core.model.search.SavedLocation
import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.core.model.unit.PressureUnit
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.core.model.unit.TimeFormatUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ConvertUseCase @Inject constructor() {

    operator fun invoke(
        weather: Weather?,
        units: AllUnit?
    ): Weather? {
        if (weather == null) return null

        var tempWeather = when (units?.temperature) {
            TemperatureUnit.FAHRENHEIT -> weather.convertTemperatureUnit {
                convertCelsiusToFahrenheit(it)
            }

            else -> weather
        }

        tempWeather = when (units?.windSpeed) {
            WindSpeedUnit.METER_PER_SECOND -> tempWeather.convertWindSpeedUnit { convertKmhToMs(it) }
            WindSpeedUnit.MILE_PER_HOUR -> tempWeather.convertWindSpeedUnit { convertKmhToMph(it) }
            else -> tempWeather
        }

        tempWeather = when (units?.pressure) {
            PressureUnit.INCH_OF_MERCURY -> tempWeather.convertPressureUnit { convertHPaToInHg(it) }
            else -> tempWeather
        }

        tempWeather = when (units?.timeFormat) {
            TimeFormatUnit.AM_PM -> tempWeather.convertTimeFormatUnit { convertTimeFormatToAmPm(it) }
            else -> tempWeather.convertTimeFormatUnit { convertTimeFormatTo24(it) }
        }

        return tempWeather
    }

    operator fun invoke(
        locations: List<LocationEntityWithWeather>,
        tUnit: TemperatureUnit?,
        currentCoordinate: Coordinate?
    ): List<SavedLocation?> = locations.map { locationEntity ->
        val location = locationEntity.toSavedLocation(currentCoordinate)
        location?.copy(
            temp = when (tUnit) {
                TemperatureUnit.FAHRENHEIT -> convertCelsiusToFahrenheit(location.temp)
                else -> location.temp
            },
        )
    }


    private fun convertCelsiusToFahrenheit(celsius: Float) = celsius * 9 / 5 + 32

    private fun convertKmhToMs(kmh: Float) = kmh / 3.6f

    private fun convertKmhToMph(kmh: Float) = kmh / 1.609344f

    private fun convertHPaToInHg(hPa: Float) = hPa * 0.02953f

    private fun convertTimeFormatToAmPm(timeStr: String): String {
        val todayDateTime = LocalDateTime.parse(timeStr, GENERAL_TIME_FORMATTER)
        return todayDateTime.format(DateTimeFormatter.ofPattern(DATE_AM_PM_PATTERN))
    }

    private fun convertTimeFormatTo24(timeStr: String): String {
        val todayDateTime = LocalDateTime.parse(timeStr, GENERAL_TIME_FORMATTER)
        return todayDateTime.format(DateTimeFormatter.ofPattern(DATE_24_PATTERN))
    }
}
