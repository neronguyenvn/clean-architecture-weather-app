package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.common.util.Result.Success
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.WeatherDao
import com.example.weatherjourney.core.database.model.coordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.asEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val locationRepository: LocationRepository,
    private val network: WtnNetworkDataSource,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,

    @Dispatcher(IO)
    private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun refreshWeatherOfDisplayedLocation() {
        withContext(ioDispatcher) {
            locationDao.observeDisplayed().first()?.let { location ->
                val weather = network.getWeather(location.coordinate, location.timeZone)
                launch {
                    weatherDao.upsertDaily(weather.daily.asEntity(location.id))
                }
                launch {
                    weatherDao.upsertHourly(weather.hourly.asEntity(location.id))
                }
            }
        }
    }

    override suspend fun refreshWeatherOfCurrentLocation() {
        withContext(ioDispatcher) {
            val currentCoordinate = locationRepository.getCurrentCoordinateStream().first()
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
                        locationDao.insert(
                            reverseGeocoding.asEntity(currentCoordinate.data, true)
                        ).toInt()
                    } else {
                        if (!location.isDisplayed) {
                            locationDao.deleteDisplayed()
                            locationDao.updateToDisplayedById(location.id)
                        }
                        location.id
                    }
                }

                launch {
                    weatherDao.upsertDaily(weather.await().daily.asEntity(locationId))
                }
                launch {
                    weatherDao.upsertHourly(weather.await().hourly.asEntity(locationId))
                }
            }
        }
    }
}
