package com.oliver.wallet.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateValueFormatterTest {

    private val dateValueFormatter = DateValueFormatter()

    @Test
    fun testGetAxisLabelForValidIndices() {
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())

        for (i in 0 until 30) {
            val expectedDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }.time
            val expectedLabel = dateFormat.format(expectedDate)
            val actualLabel = dateValueFormatter.getAxisLabel(i.toFloat(), null)
            assertEquals(expectedLabel, actualLabel)
        }
    }

    @Test
    fun testGetAxisLabelForOutRangeIndex() {
        // Testando um índice fora do intervalo (por exemplo, 7)
        val actualLabel = dateValueFormatter.getAxisLabel(30f, null)
        assertEquals("", actualLabel)
    }

    @Test
    fun testGetAxisLabelForNegativeIndex() {
        // Testando um índice negativo (-1)
        val actualLabel = dateValueFormatter.getAxisLabel(-1f, null)
        assertEquals("", actualLabel)
    }
}