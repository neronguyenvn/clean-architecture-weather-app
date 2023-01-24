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

fun String.toFlagEmoji(): String {
    // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
    if (this.length != 2) {
        return this
    }

    val countryCodeCaps = this.uppercase() // upper case is important because we are calculating offset
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    // 2. It then checks if both characters are alphabet
    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
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
