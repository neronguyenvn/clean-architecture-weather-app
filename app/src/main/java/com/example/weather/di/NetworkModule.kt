package com.example.weather.di

import com.example.weather.network.ApiService
import com.example.weather.utils.OPENCAGE_BASE_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Module for injecting Network Services.
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    /**
     *  Inject Api Service used to handle Api Requests.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     *  Inject and configure Retrofit instance.
     */
    @Provides
    fun provideRetrofit(
        converterFactory: Converter.Factory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OPENCAGE_BASE_URL)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }

    /**
     * Inject and configure Converter Factory instance.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideKotlinSerialization(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return json.asConverterFactory(contentType)
    }

    /**
     *  Inject and configure OkHttpClient instance.
     */
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Inject and configure HttpLoggingInterceptor instance.
     */
    @Provides
    fun provideHTTPLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            setLevel(Level.BODY)
        }
}
