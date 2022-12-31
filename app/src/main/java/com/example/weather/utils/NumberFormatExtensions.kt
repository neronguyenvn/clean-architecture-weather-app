package com.example.weather.utils

import java.util.Locale

fun Double.roundTo(n: Int): Double {
    return "%.${n}f".format(Locale.US, this).toDouble()
}
