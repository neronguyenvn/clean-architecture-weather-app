package com.example.weatherjourney.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class FloatListConverter {

    @TypeConverter
    fun floatListToString(list: FloatListHolder): String =
        Json.encodeToString(list)

    @TypeConverter
    fun floatToDoubleList(value: String?): FloatListHolder? =
        value?.let { Json.decodeFromString(value) }
}

@Serializable
data class FloatListHolder(val list: List<Float>)