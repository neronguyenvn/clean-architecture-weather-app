package com.example.weatherjourney.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.SavedCity
import com.example.weatherjourney.core.model.weather.WeatherType

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

fun LocationEntity.toSavedCity(temp: Double, weatherType: WeatherType) = SavedCity(
    temp = temp,
    weatherType = weatherType,
    cityAddress = cityAddress,
    coordinate = coordinate,
    isCurrentLocation = isCurrentLocation,
    timeZone = timeZone,
    countryCode = countryCode,
    id = id,
)

val LocationEntity.coordinate: Coordinate
    get() = Coordinate(latitude, longitude)