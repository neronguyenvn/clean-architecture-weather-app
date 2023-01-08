package com.example.weather.util

import android.app.Activity
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.text.format.DateUtils.SECOND_IN_MILLIS
import androidx.core.app.ActivityCompat
import com.example.weather.model.data.Coordinate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Check this activity having the permission passed in or not.
 */
fun Activity.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Round a Double number to make it has a certain number of digits after decimal point.
 */
fun Double.roundTo(n: Int): Double = "%.${n}f".format(Locale.US, this).toDouble()

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

fun Coordinate.isValid(): Boolean = latitude != 0.0 && longitude != 0.0
