package com.fabriik.common.utils

import android.text.Editable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExtensionsTest {

    private val editableNull = null
    private val editableEmpty = Editable.Factory().newEditable(emptyString)
    private val editableText = Editable.Factory().newEditable(text)
    private val editableInt = Editable.Factory().newEditable(numbers.toString())

    @Test
    fun textOrEmpty_null_returnEmptyString() {
        val actual = editableNull.textOrEmpty()
        Assert.assertEquals(emptyString, actual)
    }

    @Test
    fun textOrEmpty_emptyEditable_returnEmptyString() {
        val actual = editableEmpty.textOrEmpty()
        Assert.assertEquals(emptyString, actual)
    }

    @Test
    fun textOrEmpty_editableText_returnText() {
        val actual = editableText.textOrEmpty()
        Assert.assertEquals(text, actual)
    }

    @Test
    fun asInt_null_returnNull() {
        val actual = editableNull.asInt()
        Assert.assertNull(actual)
    }

    @Test
    fun asInt_emptyEditable_returnNull() {
        val actual = editableEmpty.asInt()
        Assert.assertNull(actual)
    }

    @Test
    fun asInt_editableInt_returnInt() {
        val actual = editableInt.asInt()
        Assert.assertEquals(numbers, actual)
    }

    @Test(expected = NumberFormatException::class)
    fun asInt_editableText_returnException() {
        val actual = editableText.asInt()
        Assert.fail()
    }

    companion object {
        private const val emptyString = ""
        private const val text = "Test"
        private const val numbers = 123
    }
}