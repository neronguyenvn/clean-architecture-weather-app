package com.example.weatherjourney.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.weatherjourney.core.database.util.DoubleListHolder
import com.example.weatherjourney.core.database.util.IntListHolder
import com.example.weatherjourney.core.database.util.LongListHolder

@Entity(
    tableName = "hourly_weather",
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("locationId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class HourlyWeatherEntity(
    var time: LongListHolder,
    var temperatures: DoubleListHolder,
    var weatherCodes: IntListHolder,
    var pressures: DoubleListHolder,
    var windSpeeds: DoubleListHolder,
    var humidities: DoubleListHolder,

    @PrimaryKey
    val locationId: Int,
)
