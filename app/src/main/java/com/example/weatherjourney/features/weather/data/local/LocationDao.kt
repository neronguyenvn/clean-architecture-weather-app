package com.example.weatherjourney.features.weather.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE latitude = :latitude AND longitude = :longitude")
    fun observeLocation(latitude: Double, longitude: Double): Flow<LocationEntity>

    @Query("SELECT * FROM location WHERE isCurrentLocation = 1")
    fun observeCurrentLocation(): Flow<LocationEntity>

    @Query("SELECT * FROM location ORDER BY isCurrentLocation DESC")
    fun observeLocations(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}
