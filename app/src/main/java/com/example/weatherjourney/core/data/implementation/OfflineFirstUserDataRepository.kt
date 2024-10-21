package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.datastore.WtnPreferencesDataSource
import com.example.weatherjourney.core.datastore.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit

class OfflineFirstUserDataRepository @Inject constructor(
    private val wtnPreferencesDataSource: WtnPreferencesDataSource
) : UserDataRepository {

    override val userData: Flow<UserData> = wtnPreferencesDataSource.userData

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        wtnPreferencesDataSource.setTemperatureUnit(unit)
    }

    override suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        wtnPreferencesDataSource.setWindSpeedUnit(unit)
    }

    override suspend fun setPressureUnit(unit: PressureUnit) {
        wtnPreferencesDataSource.setPressureUnit(unit)
    }

    override suspend fun setTimeFormatUnit(unit: TimeFormatUnit) {
        wtnPreferencesDataSource.setTimeFormatUnit(unit)
    }
}
