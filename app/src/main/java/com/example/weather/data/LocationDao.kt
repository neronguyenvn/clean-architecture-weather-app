package com.example.weather.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.database.Location

/**
 * Data Access Object for Location
 */
@Dao
interface LocationDao {

    /**
     * Get LocationDbModel by CityName
     */
    @Query("SELECT * FROM location WHERE city = :city")
    fun getLocationByCity(city: String): Location?

    /**
     * Get LocationDbModel by Location
     */
    @Query("SELECT * FROM location WHERE latitude = :latitude AND longitude = :longitude")
    fun getLocationByCoordinate(latitude: Double, longitude: Double): Location?

    /**
     * Insert a Location in the database. If the Location already exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)
}
