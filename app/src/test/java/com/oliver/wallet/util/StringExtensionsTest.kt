package com.oliver.wallet.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun testDataFormatWithValidDateString() {
        val inputDate = "2024-09-17 14:30"
        val expectedOutput = "17/09/2024 as 14:30"

        val formattedDate = inputDate.dataFormat()

        assertEquals(expectedOutput, formattedDate)
    }

    @Test
    fun testDataFormatWithInvalidDateString() {
        val inputDate = "invalid-date"

        val formattedDate = inputDate.dataFormat()

        assertEquals("", formattedDate)
    }

    @Test
    fun testToDecimalFormatWithPositiveFloat() {
        val inputNumber = 12345.679f
        val expectedOutput = "12,345.679"

        val formattedNumber = inputNumber.toDecimalFormat()

        assertEquals(expectedOutput, formattedNumber)
    }

    @Test
    fun testToDecimalFormatWithZero() {
        val inputNumber = 0f
        val expectedOutput = "0.000"

        val formattedNumber = inputNumber.toDecimalFormat()

        assertEquals(expectedOutput, formattedNumber)
    }

    @Test
    fun testToDecimalFormatWithNegativeFloat() {
        val inputNumber = -12345.679f
        val expectedOutput = "-12,345.679"

        val formattedNumber = inputNumber.toDecimalFormat()

        assertEquals(expectedOutput, formattedNumber)
    }

    @Test
    fun testToDecimalFormatWithCountPlaces() {
        val inputNumber = 0f
        val expectedOutput = 3

        val formattedNumber = inputNumber.toDecimalFormat()

        val places = formattedNumber.indexOf(".")

        assertEquals(expectedOutput, formattedNumber.substring(places + 1).length)
    }
}