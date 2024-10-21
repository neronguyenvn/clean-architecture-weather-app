package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.datastore.model.UserData
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setTemperatureUnit(unit: TemperatureUnit)

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)

    suspend fun setPressureUnit(unit: PressureUnit)

    suspend fun setTimeFormatUnit(unit: TimeFormatUnit)
}
