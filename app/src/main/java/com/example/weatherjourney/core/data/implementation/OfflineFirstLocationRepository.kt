package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.IO
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.model.asExternalModel
import com.example.weatherjourney.core.model.Location
import com.example.weatherjourney.core.model.LocationWithWeather
import com.example.weatherjourney.core.model.asEntity
import com.example.weatherjourney.core.network.NetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstLocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val network: NetworkDataSource,
    private val weatherRepository: WeatherRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : LocationRepository {

    override fun getLocationsWithWeather(): Flow<List<LocationWithWeather>> {
        return locationDao.observeAllWithWeather().map { locationEntities ->
            locationEntities.map { it.asExternalModel() }
        }
    }

    override fun getLocationWithWeather(id: Int): Flow<LocationWithWeather> {
        return locationDao.observeWithWeather(id).map { it.asExternalModel() }
    }

    override suspend fun saveLocation(location: Location) {
        locationDao.insert(location.asEntity())
    }

    override suspend fun deleteLocation(id: Int) {
        locationDao.deleteById(id.toLong())
    }

    override suspend fun getLocationsByAddress(address: String): Flow<List<Location>> {
        return flow<List<Location>> {
            network.searchLocationsByAddress(address)
        }.catch { it.printStackTrace() }
    }
}
