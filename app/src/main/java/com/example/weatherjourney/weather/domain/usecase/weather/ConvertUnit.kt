package com.example.weatherjourney.weather.domain.usecase.weather

import com.example.weatherjourney.weather.domain.mapper.convertPressureUnit
import com.example.weatherjourney.weather.domain.mapper.convertTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.convertTimeFormatUnit
import com.example.weatherjourney.weather.domain.mapper.convertWindSpeedUnit
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.weather.domain.model.unit.PressureUnit.INCH_OF_MERCURY
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit.FAHRENHEIT
import com.example.weatherjourney.weather.domain.model.unit.TimeFormatUnit.AM_PM
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit.METER_PER_SECOND
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit.MILE_PER_HOUR
import com.example.weatherjourney.weather.presentation.info.AllWeather
import com.example.weatherjourney.weather.util.DATE_24_PATTERN
import com.example.weatherjourney.weather.util.DATE_AM_PM_PATTERN
import com.example.weatherjourney.weather.util.TODAY_TIME_FORMATTER
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConvertUnit {

    operator fun invoke(allWeather: AllWeather, allUnit: AllUnit?): AllWeather {
        var tempWeather = when (allUnit?.temperature) {
            FAHRENHEIT -> allWeather.convertTemperatureUnit { convertCelsiusToFahrenheit(it) }
            else -> allWeather
        }

        tempWeather = when (allUnit?.windSpeed) {
            METER_PER_SECOND -> tempWeather.convertWindSpeedUnit { convertKmhToMs(it) }
            MILE_PER_HOUR -> tempWeather.convertWindSpeedUnit { convertKmhToMph(it) }
            else -> tempWeather
        }

        tempWeather = when (allUnit?.pressure) {
            INCH_OF_MERCURY -> tempWeather.convertPressureUnit { convertHPaToInHg(it) }
            else -> tempWeather
        }

        tempWeather = when (allUnit?.timeFormat) {
            AM_PM -> tempWeather.convertTimeFormatUnit { convertTimeFormatToAmPm(it) }
            else -> tempWeather.convertTimeFormatUnit { convertTimeFormatTo24(it) }
        }

        return tempWeather
    }

    operator fun invoke(cities: List<SavedCity>, tUnit: TemperatureUnit?) = cities.map {
        it.copy(
            temp = when (tUnit) {
                FAHRENHEIT -> convertCelsiusToFahrenheit(it.temp)
                else -> it.temp
            }
        )
    }

    private fun convertCelsiusToFahrenheit(celsius: Double) = celsius * 9 / 5 + 32

    private fun convertKmhToMs(kmh: Double) = kmh / 3.6

    private fun convertKmhToMph(kmh: Double) = kmh / 1.609344

    private fun convertHPaToInHg(hPa: Double) = hPa * 0.0295299830714

    private fun convertTimeFormatToAmPm(timeStr: String): String {
        val todayDateTime = LocalDateTime.parse(timeStr, TODAY_TIME_FORMATTER)
        return todayDateTime.format(DateTimeFormatter.ofPattern(DATE_AM_PM_PATTERN))
    }

    private fun convertTimeFormatTo24(timeStr: String): String {
        val todayDateTime = LocalDateTime.parse(timeStr, TODAY_TIME_FORMATTER)
        return todayDateTime.format(DateTimeFormatter.ofPattern(DATE_24_PATTERN))
    }
}
