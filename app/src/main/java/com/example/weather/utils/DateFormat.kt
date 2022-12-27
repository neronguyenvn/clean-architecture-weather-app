package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateFormat @Inject constructor() : SimpleDateFormat("EEE, MMM dd", Locale.US) {

    fun convertUnixTimeToDate(unixTime: Long): String {
        return try {
            val date = Date(unixTime * 1000)
            this.format(date)
        } catch (e: NumberFormatException) {
            e.toString()
        }
    }
}