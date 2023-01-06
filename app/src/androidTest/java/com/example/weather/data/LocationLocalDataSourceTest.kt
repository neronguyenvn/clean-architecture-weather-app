package com.example.weather.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weather.city1
import com.example.weather.coordinate1
import com.example.weather.location1
import com.example.weather.location2
import com.example.weather.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationLocalDataSourceTest {
    private lateinit var database: AppDatabase
    private lateinit var locationDao: LocationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        locationDao = database.locationDao()
    }

    @Test
    fun locationLocalDataSource_LocationInserted_LocationRead() = runTest {
        // Arrange
        val locationLocalDataSource = LocationLocalDataSource(locationDao)

        // Act
        locationLocalDataSource.saveLocation(location1)

        // Assert
        assert(locationLocalDataSource.getCityName(coordinate1) == Result.Success(city1))
        assert(locationLocalDataSource.getCoordinate(city1) == Result.Success(coordinate1))
    }

    @Test
    fun locationLocalDataSource_LocationInserted_DiffrentLocationRead() = runTest {
        // Arrange
        val locationLocalDataSource = LocationLocalDataSource(locationDao)

        // Act
        locationLocalDataSource.saveLocation(location2)

        // Assert
        assert(locationLocalDataSource.getCityName(coordinate1) is Result.Error)
        assert(locationLocalDataSource.getCoordinate(city1) is Result.Error)
    }

    @After
    fun closeDb() {
        database.close()
    }
}
