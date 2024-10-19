package com.example.weatherjourney.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.Location

@Entity(tableName = "location")
data class LocationEntity(
    val address: String,
    val countryCode: String,
    val timeZone: String,
    val latitude: Double,
    val longitude: Double,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)

val LocationEntity.coordinate
    get() = Coordinate.create(latitude, longitude)

fun LocationEntity.asExternalModel() = Location(
    id = id,
    address = address,
    countryCode = countryCode,
    timeZone = timeZone,
    coordinate = coordinate
)
