package com.example.weatherjourney.util

import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils
import com.example.weatherjourney.R
import com.example.weatherjourney.constants.DAY_NAME_IN_WEEK_PATTERN
import com.example.weatherjourney.constants.TODAY_TIME_FORMATTER
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Round a Double number to make it has a certain number of digits after decimal point.
 */
fun Double.roundTo(n: Int): Double = String.format(Locale.US, "%.${n}f", this).toDouble()

fun getCurrentDate(timeZone: String): String {
    val instant = Instant.now()
    val timeZoneId = ZoneId.of(timeZone)
    return instant.atZone(timeZoneId).format(TODAY_TIME_FORMATTER)
}

fun Long.toDate(timeZone: String, pattern: String): String {
    val instant = Instant.ofEpochSecond(this)

    val formatter = DateTimeFormatter.ofPattern(pattern)
    return instant.atZone(ZoneId.of(timeZone)).format(formatter)
}

fun String.toFlagEmoji(): String {
    // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
    if (this.length != 2) {
        return this
    }

    val countryCodeCaps =
        this.uppercase() // upper case is important because we are calculating offset
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    // 2. It then checks if both characters are alphabet
    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

@OptIn(ExperimentalStdlibApi::class)
fun Long.toDayNameInWeek(timeZone: String): UiText {
    val today = LocalDate.now().atStartOfDay(ZoneId.of(timeZone))
    val tomorrow = today.plusDays(1)
    val dayAfterTomorrow = tomorrow.plusDays(1)

    return when (Instant.ofEpochSecond(this).atZone(ZoneId.of(timeZone))) {
        in today..<tomorrow -> UiText.StringResource(R.string.today)
        in tomorrow..<dayAfterTomorrow -> UiText.StringResource(R.string.tomorrow)
        else -> UiText.DynamicString(this.toDate(timeZone, DAY_NAME_IN_WEEK_PATTERN))
    }
}

fun List<Long>.filterPastHours() =
    this.filter { it > Instant.now().minusMillis(DateUtils.HOUR_IN_MILLIS).epochSecond }

inline fun <T, R : Any> T.runCatching(block: T.() -> R): Result<R> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}

fun List<Long>.countPastHoursToday() =
    this.count { it <= Instant.now().minusMillis(DateUtils.HOUR_IN_MILLIS).epochSecond }

fun Context.checkPermission(permission: String) =
    this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

fun Any?.isNull() = this == null
