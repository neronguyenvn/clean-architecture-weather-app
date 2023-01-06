package com.example.weather.fake.data

import com.example.weather.city1
import com.example.weather.coordinate1
import com.example.weather.data.LocationRepository
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.util.checkResult
import com.example.weather.utils.Result

class FakeLocationRepository(
    var isGetCoordinateSuccess: Boolean = true,
    var isGetCitySuccess: Boolean = true
) : LocationRepository {
    override suspend fun getCoordinateByCity(city: String): Result<Coordinate> {
        return checkResult(isGetCoordinateSuccess, coordinate1)
    }

    override suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String> {
        return checkResult(isGetCitySuccess, city1)
    }

    override suspend fun getCurrentCoordinate(): Coordinate {
        return coordinate1
    }
}
