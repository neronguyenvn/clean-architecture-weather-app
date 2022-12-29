package com.example.weather.network

import com.example.weather.BuildConfig
import com.example.weather.model.geocoding.ForwardGeocoding
import com.example.weather.model.geocoding.ReverseGeocoding
import com.example.weather.model.weather.Weather
import com.example.weather.utils.OPENWEATHER_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("geocode/v1/json")
    suspend fun getGeocoding(
        @Query("q", encoded = true) city: String,
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") noAnnotation: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ForwardGeocoding

    @GET("geocode/v1/json")
    suspend fun getCity(
        @Query("q") location: String,
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") noAnnotation: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ReverseGeocoding

    @GET
    suspend fun getWeather(
        @Url url: String = OPENWEATHER_BASE_URL,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("appid") key: String = BuildConfig.OPENWEATHER_API_KEY
    ): Weather
}
