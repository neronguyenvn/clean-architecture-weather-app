package com.example.weatherjourney.util

import android.content.Context
import android.content.pm.PackageManager
import android.text.format.DateUtils
import com.example.weatherjourney.R
import com.example.weatherjourney.constants.DAY_NAME_IN_WEEK_PATTERN
import com.example.weatherjourney.constants.GENERAL_TIME_FORMATTER
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The regional indicators go from 0x1F1E6 (A) to 0x1F1FF (Z).
 * This is the A regional indicator value minus 65 decimal so
 * that we can just add this to the A-Z char
 */
private const val REGIONAL_INDICATOR_OFFSET = 0x1F1A5

/**
 * Round a Double number to make it has a certain number of digits after decimal point.
 */
fun Double.roundTo(n: Int): Double = String.format(Locale.US, "%.${n}f", this).toDouble()

fun getCurrentDate(timeZone: String): String {
    val instant = Instant.now()
    val timeZoneId = ZoneId.of(timeZone)
    return instant.atZone(timeZoneId).format(GENERAL_TIME_FORMATTER)
}

fun Long.toDate(timeZone: String, pattern: String): String {
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

@Suppress("TooGenericExceptionCaught")
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
