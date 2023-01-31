package com.example.weatherjourney.weather.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE latitude = :latitude AND longitude = :longitude")
    fun getLocationByCoordinate(latitude: Double, longitude: Double): Flow<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM location")
    fun getLocations(): Flow<List<LocationEntity>>
}
