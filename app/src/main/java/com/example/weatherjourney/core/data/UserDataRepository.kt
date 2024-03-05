package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.datastore.model.UserPreferences
import com.example.weatherjourney.core.model.unit.PressureUnit
import com.example.weatherjourney.core.model.unit.TemperatureUnit
import com.example.weatherjourney.core.model.unit.TimeFormatUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserPreferences>

    suspend fun setTemperatureUnit(unit: TemperatureUnit)

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)

    suspend fun setPressureUnit(unit: PressureUnit)

    suspend fun setTimeFormatUnit(unit: TimeFormatUnit)
}