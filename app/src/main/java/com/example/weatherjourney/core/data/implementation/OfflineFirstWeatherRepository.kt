package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.WeatherDao
import com.example.weatherjourney.core.database.model.coordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstWeatherRepository @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val network: WtnNetworkDataSource,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun refreshWeatherOfLocations() {
        locationDao.getAll().forEach { location ->
            val weather = network.getWeather(location.coordinate, location.timeZone)
            refreshWeather(weather, location.id)
        }
    }

    private suspend fun refreshWeather(
        weather: NetworkWeather,
        locationId: Int
    ) {
        withContext(ioDispatcher) {
            launch {
                weatherDao.upsertDaily(weather.daily.asEntity(locationId))
            }
            launch {
                weatherDao.upsertHourly(weather.hourly.asEntity(locationId))
            }
        }
    }
}
