package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit

fun getTemperatureUnit(label: String): TemperatureUnit {
    for (unit in TemperatureUnit.values()) {
        if (label == unit.label) return unit
    }
    return TemperatureUnit.NULL
}

fun getWindSpeedUnit(label: String): WindSpeedUnit {
    for (unit in WindSpeedUnit.values()) {
        if (label == unit.label) return unit
    }
    return WindSpeedUnit.NULL
}
