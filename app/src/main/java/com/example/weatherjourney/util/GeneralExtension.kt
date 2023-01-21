package com.example.weatherjourney.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

const val DATE_PATTERN = "MMMM dd, HH:mm"
const val HOUR_PATTERN = "EEE HH:mm"
const val DAY_NAME_IN_WEEK_PATTERN = "EEE"

/**
 * Round a Double number to make it has a certain number of digits after decimal point.
 */
fun Double.roundTo(n: Int): Double = "%.${n}f".format(Locale.getDefault(), this).toDouble()

fun Long.toDateString(timezoneOffset: Int, pattern: String): String {
    val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)
    // Get UTC time
    val instant = Instant.ofEpochSecond(this)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return instant.atOffset(zoneOffset).format(formatter)
}

fun String.capitalizeByWord() = this.split(" ")
    .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

// TODO: Only show Today and Tomorrow if this is current location weather
/*fun Long.toDayNameInWeek(timezoneOffset: Int): String {
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
}*/