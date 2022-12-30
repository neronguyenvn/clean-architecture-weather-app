package com.example.weather.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN = "EEE, MMM dd"
private const val DAY_NAME_IN_WEEK_PATTERN = "EEE"

/**
 * Convert a Timestamp to Date string with Custom Pattern param
 */
fun Long.toDateString(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    val date = Date(this * 1000)
    return sdf.format(date)
}

/**
 * Convert a Timestamp to Day Name in week by passing Current Timestamp.
 * Because of Api, this Timestamp always bigger than Current Timestamp a little bit.
 */
fun Long.toDayNameInWeek(currentTimestamp: Long): String {
    return when {
        (this - currentTimestamp) * 1000 < DateUtils.HOUR_IN_MILLIS * 2 -> "Today"
        (this - currentTimestamp) * 1000 < DateUtils.DAY_IN_MILLIS * 2 -> "Tomorrow"
        else -> this.toDateString(DAY_NAME_IN_WEEK_PATTERN)
    }
}
