package com.fabriik.common.utils.validators

import org.junit.Assert
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun invoke_emptyInput_returnFalse() {
        val actual = PasswordValidator("")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_tooShortInput_returnFalse() {
        val actual = PasswordValidator("abvA21!")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphabeticInput_returnFalse() {
        val actual = PasswordValidator("abcdefgh")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphanumericInput1_returnFalse() {
        val actual = PasswordValidator("abcd1234")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_alphanumericInput2_returnFalse() {
        val actual = PasswordValidator("ABCD1234")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_validInput_returnTrue() {
        val actual = PasswordValidator("abAd1234")
        Assert.assertTrue(actual)
    }
}

