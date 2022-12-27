package com.example.weather.data

interface GeocodingRepository {
    suspend fun getLatAndLong(): Pair<Double, Double>
}

class DefaultGeocodingRepository : GeocodingRepository {
    override suspend fun getLatAndLong(): Pair<Double, Double> {
        return Pair(9.0, 4.5)
    }
}
