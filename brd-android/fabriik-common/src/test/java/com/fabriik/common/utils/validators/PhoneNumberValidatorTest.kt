package com.fabriik.common.utils.validators

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhoneNumberValidatorTest {

    @Test
    fun invoke_emptyInput_returnFalse() {
        val actual = PhoneNumberValidator("")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidNumber1_returnFalse() {
        val actual = PhoneNumberValidator("213 (3123")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidNumber2_returnFalse() {
        val actual = PhoneNumberValidator("+213 (3123")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_validNumber_returnTrue() {
        val actual = PhoneNumberValidator("+386 12 345 678")
        Assert.assertTrue(actual)
    }
}

