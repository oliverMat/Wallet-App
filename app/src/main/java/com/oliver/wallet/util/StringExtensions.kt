package com.oliver.wallet.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.dataFormat(): String {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val desiredFormat = SimpleDateFormat("dd/MM/yyyy 'as' HH:mm", Locale.US)

    val date: Date? = originalFormat.parse(this)

    return date?.let { desiredFormat.format(it) } ?: ""
}

fun Float.toDecimalFormat(): String {
    return ("%,.3f".format(Locale.getDefault(), this).replace(",000",""))
}