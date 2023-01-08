package com.example.weather.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Database Model for Location.
 */
@Entity(tableName = "location")
data class Location(
    @ColumnInfo(name = "city") val city: String = "",
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString()
)
