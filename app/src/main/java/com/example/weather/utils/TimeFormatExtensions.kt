package com.example.weather.utils

import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

const val DATE_PATTERN = "HH:mm EEE, MMM dd"
private const val DAY_NAME_IN_WEEK_PATTERN = "EEE"
private const val SECOND_IN_MILLIS = 1000

/**
 * Convert a Timestamp to Date string with Custom Pattern param.
 */
fun Long.toDateString(timezoneOffset: Int, pattern: String): String {
    val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)
    // Get UTC time
    val instant = Instant.ofEpochSecond(this)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return instant.atOffset(zoneOffset).format(formatter)
}

/**
 * Convert a Timestamp to Day Name in week by passing Current Timestamp.
 * Because of Api, this Timestamp always bigger than Current Timestamp a little bit.
 */
fun Long.toDayNameInWeek(timezoneOffset: Int): String {
    val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)
    val today: LocalDate = LocalDate.now(zoneOffset)
    val timestampStart = today.atStartOfDay(zoneOffset).toEpochSecond()
    val timestampStop = timestampStart + DateUtils.DAY_IN_MILLIS / SECOND_IN_MILLIS
    val timestampTomorrowStop = timestampStop + DateUtils.DAY_IN_MILLIS / SECOND_IN_MILLIS
    return when (this) {
        in timestampStart until timestampStop -> "Today"
        in timestampStop until timestampTomorrowStop -> "Tomorrow"
        else -> this.toDateString(timezoneOffset, DAY_NAME_IN_WEEK_PATTERN)
    }
}
