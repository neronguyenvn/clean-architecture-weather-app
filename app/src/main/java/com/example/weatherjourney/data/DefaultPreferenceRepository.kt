package com.example.weatherjourney.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.example.weatherjourney.weather.domain.mapper.toCoordinatePreferences
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.model.unit.TimeFormatUnit
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

    override val pressureUnitFlow: Flow<PressureUnit> =
        userPreferencesStore.data.map { preferences ->
            PressureUnit.valueOf(
                preferences[PRESSURE_UNIT] ?: PressureUnit.HECTOPASCAL.name
            )
        }

    override val timeFormatUnitFlow: Flow<TimeFormatUnit> =
        userPreferencesStore.data.map { preferences ->
            TimeFormatUnit.valueOf(
                preferences[TIME_FORMAT_UNIT] ?: TimeFormatUnit.TWENTY_FOUR.name
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

    override suspend fun savePressureUnit(unit: PressureUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[PRESSURE_UNIT] = unit.name
        }
    }

    override suspend fun saveTimeFormatUnit(unit: TimeFormatUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[TIME_FORMAT_UNIT] = unit.name
        }
    }

    private companion object {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperatureUnit")
        val WIND_SPEED_UNIT = stringPreferencesKey("windSpeedUnit")
        val PRESSURE_UNIT = stringPreferencesKey("pressureUnit")
        val TIME_FORMAT_UNIT = stringPreferencesKey("timeFormatUnit")
    }
}
