package com.example.weatherjourney.weather.domain.usecase.weather

import com.example.weatherjourney.weather.domain.mapper.convertTemperatureUnit
import com.example.weatherjourney.weather.domain.mapper.convertWindSpeedUnit
import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit.FAHRENHEIT
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit.KELVIN
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit.METER_PER_SECOND
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit.MILE_PER_HOUR
import com.example.weatherjourney.weather.presentation.info.AllWeather

class ConvertUnit {

    operator fun invoke(allWeather: AllWeather, allUnit: AllUnit): AllWeather {
        var tempWeather = when (allUnit.temperature) {
            FAHRENHEIT -> allWeather.convertTemperatureUnit { convertCelsiusToFahrenheit(it) }
            KELVIN -> allWeather.convertTemperatureUnit { convertCelsiusToKelvin(it) }
            else -> allWeather
        }

        tempWeather = when (allUnit.windSpeed) {
            METER_PER_SECOND -> tempWeather.convertWindSpeedUnit { convertKmhToMs(it) }
            MILE_PER_HOUR -> tempWeather.convertWindSpeedUnit { convertKmhToMph(it) }
            else -> tempWeather
        }

        return tempWeather
    }

    private fun convertCelsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    private fun convertCelsiusToKelvin(celsius: Double): Double {
        return celsius + 273.15
    }

    private fun convertKmhToMs(kmh: Double): Double {
        return kmh / 3.6
    }

    private fun convertKmhToMph(kmh: Double): Double {
        return kmh / 1.609344
    }
}
