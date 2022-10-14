package com.breadwallet.util

import com.breadwallet.breadbox.BreadBox
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CryptoUriParserTest {

    @Mock lateinit var breadBox: BreadBox

    private lateinit var parser: CryptoUriParser

    @Before
    fun setUp() {
        parser = CryptoUriParser(breadBox)
    }

    @Test
    fun amountToBigDecimal_onlyDecimalSeparatorInGermanFormat_returnCorrectNumber() {
        val amount = "10,35"
        val actual = parser.amountToBigDecimal(amount)
        Assert.assertEquals("10.35".toBigDecimal(), actual)
    }

    @Test
    fun amountToBigDecimal_formattedAmountInGermanFormat_returnCorrectNumber() {
        val amount = "1.000,35"
        val actual = parser.amountToBigDecimal(amount)
        Assert.assertEquals("1000.35".toBigDecimal(), actual)
    }

    @Test
    fun amountToBigDecimal_onlyDecimalSeparatorInUsFormat_returnCorrectNumber() {
        val amount = "10.35"
        val actual = parser.amountToBigDecimal(amount)
        Assert.assertEquals("10.35".toBigDecimal(), actual)
    }

    @Test
    fun amountToBigDecimal_formattedAmountInUsFormat_returnCorrectNumber() {
        val amount = "1000.35"
        val actual = parser.amountToBigDecimal(amount)
        Assert.assertEquals("1000.35".toBigDecimal(), actual)
    }
}