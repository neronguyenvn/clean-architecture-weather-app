package com.example.weatherjourney.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.model.Coordinate
import kotlinx.coroutines.flow.first

class DefaultPreferenceRepository(private val dataStore: DataStore<Preferences>) :
    PreferenceRepository {

    override suspend fun getLastCoordinate() =
        mapCoordinatePreferences(dataStore.data.first().toPreferences())

    override suspend fun saveCoordinate(coordinate: Coordinate) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = coordinate.latitude
            preferences[LONGITUDE] = coordinate.longitude
        }
    }

    private fun mapCoordinatePreferences(preferences: Preferences): Coordinate {
        return Coordinate(preferences[LATITUDE] ?: 0.0, preferences[LONGITUDE] ?: 0.0)
    }

    private companion object {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
    }
}
