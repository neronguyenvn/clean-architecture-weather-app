package com.example.weatherjourney.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "location")
data class LocationEntity(
    val cityAddress: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String,
    val isCurrentLocation: Boolean = false,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)
