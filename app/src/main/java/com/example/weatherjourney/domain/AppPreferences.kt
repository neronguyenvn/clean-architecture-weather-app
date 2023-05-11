package com.example.weatherjourney.domain

import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.locationpreferences.LocationPreferences
import kotlinx.coroutines.flow.Flow

interface AppPreferences {

    val locationPreferencesFlow: Flow<LocationPreferences>

    val temperatureUnitFlow: Flow<TemperatureUnit>

    val windSpeedUnitFlow: Flow<WindSpeedUnit>

    val pressureUnitFlow: Flow<PressureUnit>

    val timeFormatUnitFlow: Flow<TimeFormatUnit>
    suspend fun isFirstTimeRunApp(): Boolean

    suspend fun updateLocation(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String,
        isCurrentLocation: Boolean?,
    )

    suspend fun updateIsCurrentLocation(isCurrentLocation: Boolean)

    suspend fun updateTemperatureUnit(unit: TemperatureUnit)

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit)

    suspend fun updatePressureUnit(unit: PressureUnit)

    suspend fun updateTimeFormatUnit(unit: TimeFormatUnit)

    suspend fun setFirstTimeRunAppToFalse()
}
