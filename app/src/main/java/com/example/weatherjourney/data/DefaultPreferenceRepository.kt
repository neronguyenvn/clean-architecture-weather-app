package com.example.weatherjourney.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.model.Coordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultPreferenceRepository(private val dataStore: DataStore<Preferences>) :
    PreferenceRepository {

    override val coordinateFlow: Flow<Coordinate> = dataStore.data
        .map { Coordinate(it[LATITUDE] ?: 0.0, it[LONGITUDE] ?: 0.0) }

    override suspend fun saveCoordinate(coordinate: Coordinate) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = coordinate.latitude
            preferences[LONGITUDE] = coordinate.longitude
        }
    }

    private companion object {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
    }
}
