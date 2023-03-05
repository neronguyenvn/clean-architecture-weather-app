package com.example.weatherjourney.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherejourney.LocationPreferences
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.mapper.toCoordinatePreferences
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "DefaultPreferenceRepository"

class DefaultPreferenceRepository(
    private val userPreferencesStore: DataStore<Preferences>,
    private val locationPreferencesStore: DataStore<LocationPreferences>
) : PreferenceRepository {

    override val locationPreferencesFlow: Flow<LocationPreferences> = locationPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(LocationPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override val temperatureUnitFlow: Flow<TemperatureUnit> =
        userPreferencesStore.data.map { preferences ->
            TemperatureUnit.valueOf(preferences[TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name)
        }

    override val windSpeedUnitFlow: Flow<WindSpeedUnit> =
        userPreferencesStore.data.map { preferences ->
            WindSpeedUnit.valueOf(
                preferences[WIND_SPEED_UNIT] ?: WindSpeedUnit.KILOMETER_PER_HOUR.name
            )
        }

    override suspend fun updateLocation(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String
    ) {
        locationPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setCityAddress(cityAddress)
                .setCoordinate(coordinate.toCoordinatePreferences()).setTimeZone(timeZone)
                .build()
        }
    }

    override suspend fun updateCityAddress(cityAddress: String) {
        locationPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setCityAddress(cityAddress).build()
        }
    }

    override suspend fun updateCoordinate(coordinate: Coordinate) {
        locationPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setCoordinate(coordinate.toCoordinatePreferences()).build()
        }
    }

    override suspend fun updateTimeZone(timeZone: String) {
        locationPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setTimeZone(timeZone).build()
        }
    }

    override suspend fun saveTemperatureUnit(unit: TemperatureUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT] = unit.name
        }
    }

    override suspend fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT] = unit.name
        }
    }

    private companion object {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperatureUnit")
        val WIND_SPEED_UNIT = stringPreferencesKey("windSpeedUnit")
    }
}
