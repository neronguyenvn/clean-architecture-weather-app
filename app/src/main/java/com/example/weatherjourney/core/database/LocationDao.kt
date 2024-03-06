package com.example.weatherjourney.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.LocationWithWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location WHERE isDisplayed = 1")
    fun observeDisplayed(): Flow<LocationEntity?>

    @Transaction
    @Query("SELECT * FROM location WHERE isDisplayed = 1")
    fun observeDisplayedWithWeather(): Flow<LocationWithWeather?>

    @Transaction
    @Query("SELECT * FROM location")
    fun observeAllWithWeather(): Flow<List<LocationWithWeather>>

    @Insert
    suspend fun insert(locationEntity: LocationEntity): Long

    @Query("SELECT * FROM location WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getByCoordinate(latitude: Float, longitude: Float): LocationEntity?

    @Query("UPDATE location SET isDisplayed = 1 WHERE id = :id")
    suspend fun updateToDisplayedById(id: Int)

    @Query("DELETE FROM location WHERE isDisplayed = 1")
    suspend fun deleteDisplayed(): Int

    @Query("DELETE FROM location WHERE id = :locationId")
    suspend fun deleteById(locationId: Int): Int
}
