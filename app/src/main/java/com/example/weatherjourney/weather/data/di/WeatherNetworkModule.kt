package com.example.weatherjourney.weather.data.di

import com.example.weatherjourney.weather.data.source.remote.ApiService
import com.example.weatherjourney.weather.data.source.remote.ApiService.Companion.OPENCAGE_BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherNetworkModule {

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(OPENCAGE_BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
            .create()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(Level.BODY)
            }
        ).build()
    }
}
