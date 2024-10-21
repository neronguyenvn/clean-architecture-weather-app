package com.example.weatherjourney.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.WeatherDao
import com.example.weatherjourney.core.database.model.DailyWeatherEntity
import com.example.weatherjourney.core.database.model.HourlyWeatherEntity
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.util.DoubleListConverter
import com.example.weatherjourney.core.database.util.IntListConverter
import com.example.weatherjourney.core.database.util.LongListConverter

@Database(
    entities = [
        LocationEntity::class,
        DailyWeatherEntity::class,
        HourlyWeatherEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    IntListConverter::class,
    LongListConverter::class,
    DoubleListConverter::class
)
abstract class RoomWtnDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun weatherDao(): WeatherDao
}
