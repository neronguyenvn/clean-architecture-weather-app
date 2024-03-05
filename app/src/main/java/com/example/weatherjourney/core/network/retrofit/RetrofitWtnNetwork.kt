package com.example.weatherjourney.core.network.retrofit

import com.example.weatherjourney.BuildConfig
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.toApiCoordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.ForwardGeocoding
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.ReverseGeocoding
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val OPENCAGE_BASE_URL = "https://api.opencagedata.com"
private const val OPENMETEO_WEATHER_URL = "https://api.open-meteo.com/v1/forecast"
private const val OPENMETEO_GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"


private interface RetrofitWtnNetworkApi {

    @GET("geocode/v1/json")
    suspend fun getReverseGeocoding(
        @Query("q") coordinate: String,
        @Query("key") key: String = BuildConfig.OPENCAGE_API_KEY,
    ): ReverseGeocoding

    @GET
    suspend fun getAllWeather(
        @Url url: String = OPENMETEO_WEATHER_URL,
        @Query("latitude") lat: Float,
        @Query("longitude") long: Float,
        @Query("timezone") timeZone: String,
        @Query(
            "hourly",
            encoded = true,
        ) hourlyParams: String = "temperature_2m,relativehumidity_2m,weathercode,pressure_msl,windspeed_10m",
        @Query(
            "daily",
            encoded = true,
        ) dailyParams: String = "weathercode,temperature_2m_max,temperature_2m_min",
        @Query("timeformat") timeFormat: String = "unixtime",
    ): NetworkWeather

    @GET
    suspend fun getForwardGeocoding(
        @Url url: String = OPENMETEO_GEOCODING_URL,
        @Query("name", encoded = true) cityAddress: String,
    ): ForwardGeocoding
}

class RetrofitWtnNetwork(
    networkJson: Json,
    okHttpCallFactory: Call.Factory
) : WtnNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl(OPENCAGE_BASE_URL)
        .callFactory(okHttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType()),
        )
        .build()
        .create(RetrofitWtnNetworkApi::class.java)

    override suspend fun getWeather(
        coordinate: Coordinate,
        timeZone: String
    ): NetworkWeather = networkApi.getAllWeather(
        lat = coordinate.latitude,
        long = coordinate.longitude,
        timeZone = timeZone
    )

    override suspend fun getForwardGeocoding(cityAddress: String): ForwardGeocoding =
        networkApi.getForwardGeocoding(cityAddress = cityAddress)

    override suspend fun getReverseGeocoding(coordinate: Coordinate): ReverseGeocoding =
        networkApi.getReverseGeocoding(coordinate.toApiCoordinate())
}
