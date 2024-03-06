package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.common.util.Result.Success
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.WeatherDao
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.coordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Inject

class OfflineFirstWeatherRepository @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val network: WtnNetworkDataSource,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,

    @Dispatcher(IO)
    private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun refreshWeatherOfLocation(locationOrDisplayedOne: LocationEntity?) {
        withContext(ioDispatcher) {
            (locationOrDisplayedOne ?: locationDao.observeDisplayed().firstOrNull())
                ?.let { location ->
                    val weather = network.getWeather(location.coordinate, location.timeZone)
                    refreshWeather(weather, location.id)
                }
        }
    }

    override suspend fun refreshWeatherOfCurrentLocation() {
        withContext(ioDispatcher) {
            val currentCoordinate = gpsRepository.getCurrentCoordinateStream().first()
            if (currentCoordinate is Success) {

                val weather = async {
                    network.getWeather(currentCoordinate.data, TimeZone.getDefault().id)
                }

                val locationId = locationDao.getByCoordinate(
                    currentCoordinate.data.latitude,
                    currentCoordinate.data.longitude
                ).let { location ->
                    if (location == null) {
                        val reverseGeocoding = network.getReverseGeocoding(currentCoordinate.data)
                        locationDao.insert(reverseGeocoding.asEntity(currentCoordinate.data))
                    } else {
                        location.id
                    }
                }

                refreshWeather(weather.await(), locationId)
            }
        }
    }


    // TODO: Apply Channel to boost up
    override suspend fun refreshWeatherOfLocations() {
        locationDao.getAll().forEach { location ->
            val weather = network.getWeather(location.coordinate, location.timeZone)
            refreshWeather(weather, location.id)
        }
    }

    private suspend fun refreshWeather(weather: NetworkWeather, locationId: Long) {
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
