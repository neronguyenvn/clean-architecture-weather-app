package com.example.weatherjourney.core.database

import androidx.room.Dao
import androidx.room.Upsert
import com.example.weatherjourney.core.database.model.DailyWeatherEntity
import com.example.weatherjourney.core.database.model.HourlyWeatherEntity

@Dao
interface WeatherDao {

    @Upsert
    suspend fun upsertDaily(dailyWeatherEntity: DailyWeatherEntity)

    @Upsert
    suspend fun upsertHourly(hourlyWeatherEntity: HourlyWeatherEntity)
}
