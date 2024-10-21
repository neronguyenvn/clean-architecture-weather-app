package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.WeatherDao
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.coordinate
import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.Location
import com.example.weatherjourney.core.network.NetworkDataSource
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.asEntity
import javax.inject.Inject

class OfflineFirstWeatherRepository @Inject constructor(
    private val network: NetworkDataSource,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,
) : WeatherRepository {

    override suspend fun refreshWeatherOfLocations() {
        locationDao.getAll().forEach { location ->
            refreshWeatherOfLocation(location)
        }
    }


    override suspend fun refreshWeatherOfLocation(locationId: Int) {
        val location = locationDao.getById(locationId)
        refreshWeatherOfLocation(location)
    }

    private suspend fun refreshWeatherOfLocation(location: LocationEntity) {
        val weather = network.getWeather(location.coordinate, location.timeZone)
        weatherDao.upsertDaily(weather.daily.asEntity(location.id))
        weatherDao.upsertHourly(weather.hourly.asEntity(location.id))
    }
}
