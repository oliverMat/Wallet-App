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

fun Float.toDecimalFormatTreePlaces(): String {
    return "%,.3f".format(Locale.getDefault(), this).replace(".", ",")
}

fun Float.toDecimalFormatTwoPlaces(): String {
    return "%,.2f".format(Locale.getDefault(), this).replace(".", ",")
}

fun formatCurrencyInput(input: String): String {
    val numericValue = input.replace("[^\\d]".toRegex(), "").toLongOrNull() ?: 0L
    val formattedValue = "%.2f".format(numericValue / 100.0)
    return formattedValue.replace(".", ",")
}