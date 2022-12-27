package com.example.weather.network

import com.example.weather.BuildConfig
import com.example.weather.model.Geocoding
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("geocode/v1/json")
    suspend fun getGeocoding(
        @Query("q") city: String,
        @Query("pretty") pretty: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): Geocoding
}