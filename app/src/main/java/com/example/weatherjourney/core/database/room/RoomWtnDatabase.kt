package com.example.weatherjourney.core.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.model.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class RoomWtnDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
