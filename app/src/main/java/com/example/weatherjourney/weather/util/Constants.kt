package com.example.weatherjourney.weather.util

import java.time.format.DateTimeFormatter

// Delay time to make users feel loading is more real only when API calls retrieved so fast
const val DELAY_TIME = 1250L

val TODAY_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
const val DATE_24_PATTERN = "MMMM dd, HH:mm"
const val DATE_AM_PM_PATTERN = "MMMM dd, h:mm a"

const val HOUR_PATTERN = "EEE HH:mm"

const val DAY_NAME_IN_WEEK_PATTERN = "EEE"
