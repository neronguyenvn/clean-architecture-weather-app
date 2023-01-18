package com.example.weatherjourney.weather.data.source.remote

import com.example.weatherjourney.BuildConfig
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather
import com.example.weatherjourney.weather.data.source.remote.dto.ReverseGeocoding
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("geocode/v1/json")
    suspend fun getReverseGeocoding(
        @Query("q") location: String,
        @Query("no_annotations") noAnnotation: Int = 1,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ReverseGeocoding

    @GET
    suspend fun getAllWeather(
        @Url url: String = OPENWEATHER_BASE_URL,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("appid") key: String = BuildConfig.OPENWEATHER_API_KEY
    ): AllWeather

    companion object {
        const val OPENCAGE_BASE_URL = "https://api.opencagedata.com"
        const val OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/onecall"
        const val OPENWEATHER_IMAGE_BASE_URL = "https://openweathermap.org/img/wn/"
    }
}
