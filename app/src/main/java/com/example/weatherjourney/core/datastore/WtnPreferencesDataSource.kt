package com.example.weatherjourney.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.weatherjourney.core.datastore.model.UserData
import java.io.IOException
import javax.inject.Inject
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WindSpeedUnit

class WtnPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserData>,
) {
    val userData = userPreferences.data

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        try {
            userPreferences.updateData {
                it.copy(temperatureUnit = unit)
            }
        } catch (ioException: IOException) {
            Log.e("WtnPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        try {
            userPreferences.updateData {
                it.copy(windSpeedUnit = unit)
            }
        } catch (ioException: IOException) {
            Log.e("WtnPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setPressureUnit(unit: PressureUnit) {
        try {
            userPreferences.updateData {
                it.copy(pressureUnit = unit)
            }
        } catch (ioException: IOException) {
            Log.e("WtnPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setTimeFormatUnit(unit: TimeFormatUnit) {
        try {
            userPreferences.updateData {
                it.copy(timeFormatUnit = unit)
            }
        } catch (ioException: IOException) {
            Log.e("WtnPreferences", "Failed to update user preferences", ioException)
        }
    }
}
