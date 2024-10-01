package com.oliver.wallet.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateValueFormatter : ValueFormatter() {

    private val dateFormat = SimpleDateFormat("dd-MMM", Locale.getDefault())

    private val dateList = List(365) {//adiciona 7 dias, apartir do dia atual
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -it)
        }.time
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val dateIndex = value.toInt()
        return dateList.getOrNull(dateIndex)?.let(dateFormat::format) ?: ""
    }
}