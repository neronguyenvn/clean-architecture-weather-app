package com.example.weatherjourney.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Transaction
    @Query("SELECT * FROM location")
    fun observeAllWithWeather(): Flow<List<LocationEntityWithWeather>>

    @Transaction
    @Query("SELECT * FROM location WHERE id = :id")
    fun observeWithWeather(id: Int): Flow<LocationEntityWithWeather>

    @Query("SELECT * FROM location")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM location WHERE id = :id")
    suspend fun getById(id: Int): LocationEntity

    @Insert
    suspend fun insert(locationEntity: LocationEntity): Long

    @Query("DELETE FROM location WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
