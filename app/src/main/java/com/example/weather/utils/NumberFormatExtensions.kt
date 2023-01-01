package com.example.weather.utils

import java.util.Locale

/**
 * Round a Double number to make it has a certain number of digits after decimal point
 */
fun Double.roundTo(n: Int): Double {
    return "%.${n}f".format(Locale.US, this).toDouble()
}
