package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherjourney.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.weather.presentation.setting.AllUnitLabel

fun AllUnit.toAllUnitLabel() = AllUnitLabel(
    temperatureLabel = temperature.label,
    windSpeedLabel = windSpeed.label
)
