package com.example.weatherjourney.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class IntListConverter {

    @TypeConverter
    fun intListToString(list: IntListHolder): String =
        Json.encodeToString(list)

    @TypeConverter
    fun stringToIntList(value: String?): IntListHolder? =
        value?.let { Json.decodeFromString(value) }
}

@Serializable
data class IntListHolder(val list: List<Int>)


