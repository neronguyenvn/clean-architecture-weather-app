package com.example.weatherjourney.features.weather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
