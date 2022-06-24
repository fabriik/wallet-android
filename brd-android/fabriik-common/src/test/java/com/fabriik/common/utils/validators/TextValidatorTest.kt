package com.fabriik.common.utils.validators

import org.junit.Assert
import org.junit.Test

class TextValidatorTest {

    @Test
    fun invoke_emptyInput_returnFalse() {
        val actual = TextValidator("")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_tooShortInput_returnFalse() {
        val actual = TextValidator("a")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphabeticInput_returnTrue() {
        val actual = TextValidator("abcdef")
        Assert.assertTrue(actual)
    }

    @Test
    fun invoke_alphanumericInput_returnTrue() {
        val actual = TextValidator("123abc")
        Assert.assertTrue(actual)
    }

    @Test
    fun invoke_numericInput_returnTrue() {
        val actual = TextValidator("123456")
        Assert.assertTrue(actual)
    }
}

