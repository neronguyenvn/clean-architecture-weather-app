package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherjourney.weather.domain.model.TemperatureUnit

fun getTemperatureUnit(label: String): TemperatureUnit {
    for (unit in TemperatureUnit.values()) {
        if (label == unit.label) return unit
    }
    return TemperatureUnit.NULL
}
