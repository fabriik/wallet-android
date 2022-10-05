package com.fabriik.common.utils.validators

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmailValidatorTest {

    @Test
    fun invoke_emptyInput_returnFalse() {
        val actual = EmailValidator("")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidEmail1_returnFalse() {
        val actual = EmailValidator("support")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidEmail2_returnFalse() {
        val actual = EmailValidator("support@fabriik")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidEmail3_returnFalse() {
        val actual = EmailValidator("support@.c")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_invalidEmail4_returnFalse() {
        val actual = EmailValidator("support&#%@fabriik.com")
        Assert.assertFalse(actual)
    }

    @Test
    fun invoke_validEmail1_returnTrue() {
        val actual = EmailValidator("hello@fabriik.com")
        Assert.assertTrue(actual)
    }
}

