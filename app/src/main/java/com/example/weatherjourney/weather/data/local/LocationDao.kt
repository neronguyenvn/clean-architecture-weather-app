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
    fun getLocation(latitude: Double, longitude: Double): Flow<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM location")
    fun getLocations(): Flow<List<LocationEntity>>

    @Delete
    suspend fun delete(location: LocationEntity)
}
