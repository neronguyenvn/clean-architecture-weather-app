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
import com.example.weatherjourney.data.DefaultPreferenceRepository
import com.example.weatherjourney.data.LocationPreferencesSerializer
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.locationpreferences.LocationPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
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
    fun providePreferenceRepository(userPreferencesStore: DataStore<Preferences>, locationPreferencesStore: DataStore<LocationPreferences>): PreferenceRepository =
        DefaultPreferenceRepository(userPreferencesStore, locationPreferencesStore)

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher() = Dispatchers.Default

    @Provides
    @IoDispatcher
    fun provideIoDispatcher() = Dispatchers.IO
}
