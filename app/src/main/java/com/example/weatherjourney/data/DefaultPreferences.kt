package com.example.weatherjourney.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.locationpreferences.LocationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class DefaultPreferences(
    private val userPreferencesStore: DataStore<Preferences>,
    private val locationPreferencesStore: DataStore<LocationPreferences>,
) : AppPreferences {

    override val locationPreferencesFlow: Flow<LocationPreferences> = locationPreferencesStore.data
        .catch { exception ->
            if (exception is IOException) {
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
                preferences[WIND_SPEED_UNIT] ?: WindSpeedUnit.KILOMETER_PER_HOUR.name,
            )
        }

    override val pressureUnitFlow: Flow<PressureUnit> =
        userPreferencesStore.data.map { preferences ->
            PressureUnit.valueOf(
                preferences[PRESSURE_UNIT] ?: PressureUnit.HECTOPASCAL.name,
            )
        }

    override val timeFormatUnitFlow: Flow<TimeFormatUnit> =
        userPreferencesStore.data.map { preferences ->
            TimeFormatUnit.valueOf(
                preferences[TIME_FORMAT_UNIT] ?: TimeFormatUnit.TWENTY_FOUR.name,
            )
        }

    override suspend fun isFirstTimeRunApp(): Boolean =
        userPreferencesStore.data.first().toPreferences().let { preferences ->
            preferences[IS_FIRST_TIME] ?: true
        }

    override suspend fun updateLocation(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String,
        isCurrentLocation: Boolean?,
    ) {
        locationPreferencesStore.updateData { preferences ->
            val builder = preferences.toBuilder()
                .setCityAddress(cityAddress)
                .setLatitude(coordinate.latitude)
                .setLongitude(coordinate.longitude)
                .setTimeZone(timeZone)

            if (isCurrentLocation != null) {
                builder.setIsCurrentLocation(isCurrentLocation).build()
            } else {
                builder.build()
            }
        }
    }

    override suspend fun updateIsCurrentLocation(isCurrentLocation: Boolean) {
        locationPreferencesStore.updateData { preferences ->
            preferences.toBuilder()
                .setIsCurrentLocation(isCurrentLocation)
                .build()
        }
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT] = unit.name
        }
    }

    override suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT] = unit.name
        }
    }

    override suspend fun updatePressureUnit(unit: PressureUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[PRESSURE_UNIT] = unit.name
        }
    }

    override suspend fun updateTimeFormatUnit(unit: TimeFormatUnit) {
        userPreferencesStore.edit { preferences ->
            preferences[TIME_FORMAT_UNIT] = unit.name
        }
    }

    override suspend fun setFirstTimeRunAppToFalse() {
        userPreferencesStore.edit { preferences ->
            preferences[IS_FIRST_TIME] = false
        }
    }

    private companion object {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperatureUnit")
        val WIND_SPEED_UNIT = stringPreferencesKey("windSpeedUnit")
        val PRESSURE_UNIT = stringPreferencesKey("pressureUnit")
        val TIME_FORMAT_UNIT = stringPreferencesKey("timeFormatUnit")
        val IS_FIRST_TIME = booleanPreferencesKey("isFirstTime")
    }
}
