package com.example.weatherjourney.fake.data

import com.example.weatherjourney.data.LocationRepository
import com.example.weatherjourney.model.data.Coordinate
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.checkResult
import com.example.weatherjourney.util.city1
import com.example.weatherjourney.util.coordinate1

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

    override suspend fun getCurrentCoordinate(): Result<Coordinate> {
        return Result.Success(coordinate1)
    }
}
