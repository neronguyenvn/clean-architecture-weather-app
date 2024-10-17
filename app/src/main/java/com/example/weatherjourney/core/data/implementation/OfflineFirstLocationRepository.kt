package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.model.search.Location
import com.example.weatherjourney.core.model.search.asEntity
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OfflineFirstLocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val network: WtnNetworkDataSource,
    private val weatherRepository: WeatherRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : LocationRepository {

    override fun getLocationsWithWeatherStream(): Flow<List<LocationEntityWithWeather>> {
        return locationDao.observeAllWithWeather()
    }

    override suspend fun saveLocation(location: Location) {
        locationDao.insert(location.asEntity())
    }

    override suspend fun deleteLocation(locationId: Int) {
        locationDao.deleteById(locationId.toLong())
    }

    override suspend fun getLocationsByAddress(address: String): Flow<List<Location>> {
        return flow<List<Location>> {
            network.getLocations(address)
        }.catch { it.printStackTrace() }
    }
}
