package com.example.weatherjourney.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class DoubleListConverter {

    @TypeConverter
    fun fromString(value: String): DoubleListHolder {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun listToString(list: DoubleListHolder): String {
        return Json.encodeToString(list)
    }
}

@Serializable
data class DoubleListHolder(val list: List<Double>)
