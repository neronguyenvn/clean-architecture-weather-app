package com.example.weatherjourney.core.common.util

import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * The regional indicators go from 0x1F1E6 (A) to 0x1F1FF (Z).
 * This is the A regional indicator value minus 65 decimal so
 * that we can just add this to the A-Z char
 */
private const val REGIONAL_INDICATOR_OFFSET = 0x1F1A5

fun getCurrentDate(timeZone: String): String {
    val instant = Instant.now()
    val timeZoneId = ZoneId.of(timeZone)
    return instant.atZone(timeZoneId).format(TimeUtils.DEFAULT_TIME_FORMATTER)
}

fun Long.toDate(
    timeZone: String,
    pattern: String
): String {
    val instant = Instant.ofEpochSecond(this)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return instant.atZone(ZoneId.of(timeZone)).format(formatter)
}

fun String.toFlagEmoji(): String {
    if (this.length != 2 || !this[0].isLetter() || !this[1].isLetter()) {
        return this
    }

    // upper case is important because we are calculating offset
    val countryCodeCaps = this.uppercase()
    val offset = REGIONAL_INDICATOR_OFFSET
    val firstLetter = Character.toChars(countryCodeCaps[0].code + offset)
    val secondLetter = Character.toChars(countryCodeCaps[1].code + offset)

    return String(firstLetter) + String(secondLetter)
}

@OptIn(ExperimentalStdlibApi::class)
fun Long.toDayNameInWeek(timeZone: String): String {
    val today = LocalDate.now().atStartOfDay(ZoneId.of(timeZone))
    val tomorrow = today.plusDays(1)
    val dayAfterTomorrow = tomorrow.plusDays(1)

    return when (Instant.ofEpochSecond(this).atZone(ZoneId.of(timeZone))) {
        in today..<tomorrow -> "Today"
        in tomorrow..<dayAfterTomorrow -> "Tomorrow"
        else -> this.toDate(timeZone, TimeUtils.SHORT_DAY_NAME_PATTERN)
    }
}

fun List<Long>.filterPastHours() =
    this.filter { it > Instant.now().minusMillis(DateUtils.HOUR_IN_MILLIS).epochSecond }

fun List<Long>.countPastHoursToday() =
    this.count { it <= Instant.now().minusMillis(DateUtils.HOUR_IN_MILLIS).epochSecond }

fun Context.checkPermission(permission: String) =
    this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
