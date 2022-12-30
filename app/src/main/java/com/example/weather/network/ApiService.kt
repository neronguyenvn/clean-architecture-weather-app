package com.example.weather.network

import com.example.weather.BuildConfig
import com.example.weather.model.geocoding.ForwardGeocoding
import com.example.weather.model.geocoding.ReverseGeocoding
import com.example.weather.model.weather.AllWeather
import com.example.weather.utils.OPENWEATHER_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Api Service for handle Api calls, is injected to where need it
 */
interface ApiService {
    /**
     * Get ForwardGeocoding model by call Api with CityName, is used to get Location
     */
    @GET("geocode/v1/json")
    suspend fun getForwardGeocoding(
        @Query("q", encoded = true) city: String,
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") noAnnotation: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ForwardGeocoding

    /**
     * Get ForwardGeocoding model by call Api with Location, is used to get CityName
     */
    @GET("geocode/v1/json")
    suspend fun getReverseGeocoding(
        @Query("q") location: String,
        @Query("pretty") pretty: Int = 1,
        @Query("no_annotations") noAnnotation: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ReverseGeocoding

    /**
     * Get AllWeather model by call Api with Location, is used to get
     * all weather info of that location
     */
    @GET
    suspend fun getAllWeather(
        @Url url: String = OPENWEATHER_BASE_URL,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("appid") key: String = BuildConfig.OPENWEATHER_API_KEY
    ): AllWeather
}
