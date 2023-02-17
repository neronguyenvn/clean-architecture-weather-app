package com.example.weatherjourney.weather.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE lat = :latitude AND long = :longitude")
    fun observeLocation(latitude: Double, longitude: Double): Flow<LocationEntity>

    @Query("SELECT * FROM location WHERE isCurrentLocation = 1")
    fun observeCurrentLocation(): Flow<LocationEntity>

    @Query("SELECT * FROM location ORDER BY isCurrentLocation DESC")
    fun observeLocations(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Delete
    suspend fun delete(location: LocationEntity)
}
