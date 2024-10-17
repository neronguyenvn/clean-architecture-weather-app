package com.example.weatherjourney.core.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    private val DEFAULT_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    private const val DATE_24_PATTERN = "MMMM dd, HH:mm"
    private const val DATE_AM_PM_PATTERN = "MMMM dd, h:mm a"
    const val HOUR_PATTERN = "EEE HH:mm"
    const val DAY_NAME_IN_WEEK_PATTERN = "EEE"

    fun formatTimeTo24Hour(timeStr: String): String {
        val dateTime = LocalDateTime.parse(timeStr, DEFAULT_TIME_FORMATTER)
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_24_PATTERN))
    }

    fun formatTimeToAmPm(timeStr: String): String {
        val dateTime = LocalDateTime.parse(timeStr, DEFAULT_TIME_FORMATTER)
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_AM_PM_PATTERN))
    }
}
