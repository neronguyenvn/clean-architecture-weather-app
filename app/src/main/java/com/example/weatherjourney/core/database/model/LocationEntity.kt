package com.example.weatherjourney.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherjourney.core.model.Coordinate

@Entity(tableName = "location")
data class LocationEntity(
    val address: String,
    val countryCode: String,
    val timeZone: String,
    val latitude: Float,
    val longitude: Float,
    val isDisplayed: Boolean,
    val isCurrentLocation: Boolean,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)

val LocationEntity.coordinate: Coordinate get() = Coordinate(latitude, longitude)
