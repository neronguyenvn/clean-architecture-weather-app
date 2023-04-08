package com.example.weatherjourney.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.weatherjourney.data.DefaultPreferences
import com.example.weatherjourney.data.LocationPreferencesSerializer
import com.example.weatherjourney.data.NetworkConnectivityObserver
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.features.weather.data.remote.WeatherApi
import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class DefaultDispatcher

@Qualifier
annotation class IoDispatcher

private const val USER_PREFERENCES = "user_preferences"
private const val LOCATION_PREFERENCES = "location_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )

    @Provides
    @Singleton
    fun provideLocationPreferencesDataStore(@ApplicationContext context: Context): DataStore<LocationPreferences> =
        DataStoreFactory.create(
            serializer = LocationPreferencesSerializer,
            produceFile = { context.dataStoreFile(LOCATION_PREFERENCES) }
        )

    @Provides
    @Singleton
    fun providePreferenceRepository(
        userPreferencesStore: DataStore<Preferences>,
        locationPreferencesStore: DataStore<LocationPreferences>
    ): com.example.weatherjourney.domain.AppPreferences =
        DefaultPreferences(userPreferencesStore, locationPreferencesStore)

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher() = Dispatchers.Default

    @Provides
    @IoDispatcher
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(WeatherApi.OPENCAGE_BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        ).build()
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver =
        NetworkConnectivityObserver(context)
}
