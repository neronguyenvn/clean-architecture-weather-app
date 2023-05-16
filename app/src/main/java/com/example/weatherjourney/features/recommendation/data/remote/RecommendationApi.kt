package com.example.weatherjourney.features.recommendation.data.remote

import com.example.weatherjourney.features.recommendation.data.remote.dto.AirQuality
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface RecommendationApi {

    @GET
    suspend fun getAirQuality(
        @Url url: String = OPENMETEO_AIR_QUALITY_URL,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timeZone: String,
        @Query("hourly", encoded = true) hourlyParams: String = "uv_index,european_aqi",
        @Query("timeformat") timeFormat: String = "unixtime",
    ): AirQuality

    companion object {
        const val OPENMETEO_AIR_QUALITY_URL =
            "https://air-quality-api.open-meteo.com/v1/air-quality"
    }
}
