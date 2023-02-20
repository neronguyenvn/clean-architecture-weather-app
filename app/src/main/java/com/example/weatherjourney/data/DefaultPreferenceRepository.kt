package com.example.weatherjourney.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.weather.domain.model.unit.WindSpeedUnit
import kotlinx.coroutines.flow.first

class DefaultPreferenceRepository(private val dataStore: DataStore<Preferences>) :
    PreferenceRepository {

    override suspend fun getLastCoordinate() =
        mapCoordinatePreferences(dataStore.data.first().toPreferences())

    override suspend fun getLastTimeZone(): String =
        dataStore.data.first().toPreferences()[TIMEZONE] ?: ""

    override suspend fun getLastCityAddress(): String =
        dataStore.data.first().toPreferences()[CITY_ADDRESS] ?: ""

    override suspend fun getTemperatureUnit(): TemperatureUnit =
        TemperatureUnit.valueOf(
            dataStore.data.first().toPreferences()[TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name
        )

    override suspend fun getWindSpeedUnit(): WindSpeedUnit =
        WindSpeedUnit.valueOf(
            dataStore.data.first().toPreferences()[WIND_SPEED_UNIT]
                ?: WindSpeedUnit.KILOMETER_PER_HOUR.name
        )

    override suspend fun saveCoordinate(coordinate: Coordinate) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = coordinate.lat
            preferences[LONGITUDE] = coordinate.long
        }
    }

    override suspend fun saveTimeZone(timeZone: String) {
        dataStore.edit { preferences ->
            preferences[TIMEZONE] = timeZone
        }
    }

    override suspend fun saveCityAddress(cityAddress: String) {
        dataStore.edit { preferences ->
            preferences[CITY_ADDRESS] = cityAddress
        }
    }

    override suspend fun saveTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT] = unit.name
        }
    }

    override suspend fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        dataStore.edit { preferences ->
            preferences[WIND_SPEED_UNIT] = unit.name
        }
    }

    private fun mapCoordinatePreferences(preferences: Preferences): Coordinate {
        return Coordinate(preferences[LATITUDE] ?: 0.0, preferences[LONGITUDE] ?: 0.0)
    }

    private companion object {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val TIMEZONE = stringPreferencesKey("timeZone")
        val CITY_ADDRESS = stringPreferencesKey("cityAddress")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperatureUnit")
        val WIND_SPEED_UNIT = stringPreferencesKey("windSpeedUnit")
    }
}
