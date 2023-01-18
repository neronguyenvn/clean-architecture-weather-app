package com.example.weatherjourney.weather.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "location")
data class LocationEntity(
    val city: String,
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)
