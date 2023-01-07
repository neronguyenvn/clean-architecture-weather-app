package com.example.weather.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.weather.model.geocoding.Coordinate
import kotlinx.coroutines.flow.first

interface PreferenceRepository {
    suspend fun fetchInitialPreferences(): Coordinate
    suspend fun saveCoordinate(coordinate: Coordinate)
}

class DefaultPreferenceRepository(private val dataStore: DataStore<Preferences>) :
    PreferenceRepository {

    override suspend fun fetchInitialPreferences() =
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
