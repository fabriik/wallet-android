package com.fabriik.common.utils.validators

import org.junit.Assert
import org.junit.Test

class ConfirmationCodeValidatorTest {

    @Test
    fun invoke_emptyInput_returnFalse() {
        val actual = ConfirmationCodeValidator("")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_tooShortInput_returnFalse() {
        val actual = ConfirmationCodeValidator("12345")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_tooLongInput_returnFalse() {
        val actual = ConfirmationCodeValidator("1234567")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphabeticInput_returnFalse() {
        val actual = ConfirmationCodeValidator("abcdef")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphanumericInput_returnFalse() {
        val actual = ConfirmationCodeValidator("123abc")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_validInput_returnTrue() {
        val actual = ConfirmationCodeValidator("123456")
        Assert.assertTrue(actual)
    }
}

