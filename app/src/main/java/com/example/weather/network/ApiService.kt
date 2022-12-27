package com.example.weather.network

import com.example.weather.BuildConfig
import com.example.weather.model.geocoding.Geocoding
import com.example.weather.model.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/onecall"

interface ApiService {
    @GET("geocode/v1/json")
    suspend fun getGeocoding(
        @Query("q") city: String,
        @Query("pretty") pretty: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): Geocoding

    @GET
    suspend fun getWeather(
        @Url url: String = OPENWEATHER_BASE_URL,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("appid") key: String = BuildConfig.OPENWEATHER_API_KEY
    ): Weather
}