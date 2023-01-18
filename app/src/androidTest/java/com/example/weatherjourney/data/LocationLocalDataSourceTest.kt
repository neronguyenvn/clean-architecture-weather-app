package com.example.weatherjourney.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherjourney.city1
import com.example.weatherjourney.coordinate1
import com.example.weatherjourney.location1
import com.example.weatherjourney.location2
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.source.local.AppDatabase
import com.example.weatherjourney.weather.data.source.local.LocationDao
import com.example.weatherjourney.weather.data.source.local.LocationLocalDataSource
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
        val expectedCity = city1
        val expectedCoordinate = coordinate1

        // Act
        locationLocalDataSource.saveLocation(location1)
        val actualCity = locationLocalDataSource.getCityName(coordinate1)
        val actualCoordinate = locationLocalDataSource.getCoordinate(city1)

        // Assert
        assertTrue(actualCity is Result.Success)
        assertEquals((actualCity as Result.Success).data, expectedCity)

        assertTrue(actualCoordinate is Result.Success)
        assertEquals((actualCoordinate as Result.Success).data, expectedCoordinate)
    }

    @Test
    fun locationLocalDataSource_LocationInserted_DifferentLocationRead() = runTest {
        // Arrange
        val locationLocalDataSource = LocationLocalDataSource(locationDao)

        // Act
        locationLocalDataSource.saveLocation(location2)
        val result1 = locationLocalDataSource.getCityName(coordinate1)
        val result2 = locationLocalDataSource.getCoordinate(city1)

        // Assert
        assertTrue(result1 is Result.Error)
        assertTrue((result1 as Result.Error).exception is NullPointerException)

        assertTrue(result2 is Result.Error)
        assertTrue((result2 as Result.Error).exception is NullPointerException)
    }

    @After
    fun closeDb() {
        database.close()
    }
}
