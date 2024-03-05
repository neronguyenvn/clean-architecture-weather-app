package com.example.weatherjourney.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LongListConverter {

    @TypeConverter
    fun longListToString(list: LongListHolder): String =
        Json.encodeToString(list)

    @TypeConverter
    fun stringToLongList(value: String?): LongListHolder? =
        value?.let { Json.decodeFromString(value) }
}

@Serializable
data class LongListHolder(val list: List<Long>)