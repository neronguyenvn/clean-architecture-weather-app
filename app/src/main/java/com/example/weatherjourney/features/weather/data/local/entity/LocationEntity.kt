package com.example.weatherjourney.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    val cityAddress: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String,
    val isCurrentLocation: Boolean = false,
    val countryCode: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
