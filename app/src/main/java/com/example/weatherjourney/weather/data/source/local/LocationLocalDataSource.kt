package com.example.weatherjourney.weather.data.source.local

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate
import kotlinx.coroutines.flow.first

class LocationLocalDataSource(private val locationDao: LocationDao) : LocationDataSource {
    override suspend fun getCoordinate(city: String): Result<Coordinate> {
/*        val location = locationDao.getLocationByCity(city).first()
        return try {
            Result.Success(location.toCoordinate())
        } catch (ex: NullPointerException) {
            return Result.Error(ex)
        }*/
        return Result.Success(Coordinate())
        // TODO: Fix this with new autocomplete api
    }

    override suspend fun getCityName(coordinate: Coordinate): Result<String> {
        val location =
            locationDao.getLocationByCoordinate(coordinate.latitude, coordinate.longitude).first()
        return try {
            Result.Success(location.city)
        } catch (ex: NullPointerException) {
            Result.Error(ex)
        }
    }

    override suspend fun saveLocation(location: LocationEntity) {
        locationDao.insert(location)
    }
}
