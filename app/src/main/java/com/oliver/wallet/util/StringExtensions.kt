package com.oliver.wallet.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.dataFormat(): String {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val desiredFormat = SimpleDateFormat("dd/MM/yyyy 'as' HH:mm", Locale.US)

    return try {
        val date: Date? = originalFormat.parse(this)
        date?.let { desiredFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}

fun Float.toDecimalFormat(): String {
    return "%,.3f".format(Locale.getDefault(), this)
}