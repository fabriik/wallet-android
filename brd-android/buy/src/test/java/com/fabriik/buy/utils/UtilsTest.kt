package com.fabriik.buy.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun formatCardNumber_inputIsNull_returnEmptyString() {
        val actual = formatCardNumber(null)
        assertEquals("", actual)
    }

    @Test
    fun formatCardNumber_emptyInput_returnEmptyString() {
        val actual = formatCardNumber("")
        assertEquals("", actual)
    }

    @Test
    fun formatCardNumber_fourDigits_returnFourDigits() {
        val actual = formatCardNumber("1234")
        assertEquals("1234", actual)
    }

    @Test
    fun formatCardNumber_fiveDigits_returnFormattedString() {
        val actual = formatCardNumber("12345")
        assertEquals("1234 5", actual)
    }

    @Test
    fun formatCardNumber_eightDigits_returnFormattedString() {
        val actual = formatCardNumber("12341234")
        assertEquals("1234 1234", actual)
    }

    @Test
    fun formatCardNumber_wholeCardNumber_returnFormattedString() {
        val actual = formatCardNumber("1234123412341234")
        assertEquals("1234 1234 1234 1234", actual)
    }

    @Test
    fun formatDate_inputIsNull_returnEmptyString() {
        val actual = formatDate(null)
        assertEquals("", actual)
    }

    @Test
    fun formatDate_emptyString_returnEmpty() {
        val actual = formatDate("")
        assertEquals("", actual)
    }

    @Test
    fun formatDate_oneDigit_returnSameString() {
        val actual = formatDate("1")
        assertEquals("1", actual)
    }

    @Test
    fun formatDate_twoDigits_returnSameString() {
        val actual = formatDate("12")
        assertEquals("12", actual)
    }

    @Test
    fun formatDate_threeDigits_returnFormattedString() {
        val actual = formatDate("123")
        assertEquals("12/3", actual)
    }

    @Test
    fun formatDate_fourDigits_returnFormattedExpiryDate() {
        val actual = formatDate("1234")
        assertEquals("12/34", actual)
    }
}