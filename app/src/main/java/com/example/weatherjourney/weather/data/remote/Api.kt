package com.example.weatherjourney.weather.data.remote

import com.example.weatherjourney.BuildConfig
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.data.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.data.remote.dto.ReverseGeocoding
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface Api {

    @GET("geocode/v1/json")
    suspend fun getReverseGeocoding(
        @Query("q") coordinate: String,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY
    ): ReverseGeocoding

    @GET
    suspend fun getAllWeather(
        @Url url: String = OPENMETEO_WEATHER_URL,
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("timezone") timeZone: String,
        @Query("timeformat") timeFormat: String = "unixtime"
    ): AllWeather

    @GET
    suspend fun getForwardGeocoding(
        @Url url: String = OPENMETEO_GEOCODING_URL,
        @Query("name") cityAddress: String
    ): ForwardGeocoding

    companion object {
        const val OPENCAGE_BASE_URL = "https://api.opencagedata.com"
        const val OPENMETEO_WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,relativehumidity_2m,weathercode,surface_pressure,windspeed_10m&daily=weathercode,temperature_2m_max,temperature_2m_min"
        const val OPENMETEO_GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"
    }
}
