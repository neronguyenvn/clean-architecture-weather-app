package com.example.weather.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather.model.database.Location

@Database(entities = [Location::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
