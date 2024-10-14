package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.model.search.SuggestionLocation
import com.example.weatherjourney.core.model.search.asEntity
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.toSuggestionLocation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstLocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val network: WtnNetworkDataSource,
    private val weatherRepository: WeatherRepository,

    @Dispatcher(IO)
    private val ioDispatcher: CoroutineDispatcher,
) : LocationRepository {

    override fun getDisplayedLocationStream(): Flow<LocationEntity?> {
        return locationDao.observeDisplayed()
    }

    override fun getDisplayedLocationWithWeatherStream(): Flow<LocationEntityWithWeather?> {
        return locationDao.observeDisplayedWithWeather()
    }

    override fun getLocationsWithWeatherStream(): Flow<List<LocationEntityWithWeather>> {
        return locationDao.observeAllWithWeather()
    }

    override suspend fun saveLocation(location: SuggestionLocation) {
        withContext(ioDispatcher) {
            location.coordinate.run {

                locationDao.getByCoordinate(latitude, longitude)?.let { location ->

                    if (!location.isDisplayed) {
                        launch { makeLocationDisplayed(location.id.toInt()) }
                    }
                    weatherRepository.refreshWeatherOfLocation(location)
                    return@withContext
                }
            }

            val entity = location.asEntity(true)
            locationDao.observeDisplayed().firstOrNull()?.let {
                locationDao.updateDisplayedById(it.id, false)
            }
            val insertedId = locationDao.insert(entity)

            weatherRepository.refreshWeatherOfLocation(entity.copy(id = insertedId))
        }
    }

    override suspend fun deleteLocation(locationId: Int) {
        locationDao.deleteById(locationId.toLong())
    }

    override suspend fun getSuggestionLocations(address: String): Result<List<SuggestionLocation>> =
        runCatching {
            withContext(ioDispatcher) {
                network.getForwardGeocoding(address = address).results.map { it.toSuggestionLocation() }

            }
        }

    override suspend fun makeLocationDisplayed(locationId: Int) {
        locationDao.observeDisplayed().firstOrNull()?.let {
            locationDao.updateDisplayedById(it.id, false)
        }
        locationDao.updateDisplayedById(locationId.toLong(), true)
    }
}
