package com.fabriik.common.utils.adapter

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class BigDecimalAdapterTest {

    @Test
    fun fromJson_numberIsOne_returnBigDecimalOne() {
        val actual = BigDecimalAdapter.fromJson("1")
        assertEquals(BigDecimal.ONE, actual)
    }

    @Test
    fun fromJson_numberIsTen_returnBigDecimalTen() {
        val actual = BigDecimalAdapter.fromJson("10")
        assertEquals(BigDecimal.TEN, actual)
    }

    @Test
    fun fromJson_numberIsTenPointTen_returnBigDecimalTenPointTen() {
        val actual = BigDecimalAdapter.fromJson("10.10")
        assertEquals("10.10".toBigDecimal(), actual)
    }

    @Test
    fun toJson_bigDecimalOne_returnOneAsString() {
        val actual = BigDecimalAdapter.toJson(BigDecimal.ONE)
        assertEquals("1", actual)
    }

    @Test
    fun toJson_bigDecimalTen_returnTenAsString() {
        val actual = BigDecimalAdapter.toJson(BigDecimal.TEN)
        assertEquals("10", actual)
    }

    @Test
    fun toJson_bigDecimalTenPointTen_returnTenPointTenAsString() {
        val actual = BigDecimalAdapter.toJson("10.10".toBigDecimal())
        assertEquals("10.10", actual)
    }
}