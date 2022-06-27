package com.fabriik.common.utils

import android.text.Editable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ExtensionsTest {

    companion object {
        private const val emptyString = ""
        private const val text = "Test"
        private const val intValue = 123
    }

    private val editableNull = null
    private val editableEmpty = Editable.Factory().newEditable(emptyString)
    private val editableText = Editable.Factory().newEditable(text)
    private val editableInt = Editable.Factory().newEditable(intValue.toString())

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
        Assert.assertEquals(intValue, actual)
    }

    @Test(expected = NumberFormatException::class)
    fun asInt_editableText_returnException() {
        val actual = editableText.asInt()
        Assert.fail()
    }

    @Test
    @Config(qualifiers = "ldpi")
    fun dp_ldpi_returnDP() {
        val actual = intValue.dp
        val expected = (intValue * 0.75).toInt()
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Config(qualifiers = "mdpi")
    fun dp_mdpi_returnDP() {
        val actual = intValue.dp
        Assert.assertEquals(intValue, actual)
    }

    @Test
    @Config(qualifiers = "hdpi")
    fun dp_hdpi_returnDP() {
        val actual = intValue.dp
        val expected = (intValue * 1.5).toInt()
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Config(qualifiers = "xhdpi")
    fun dp_xhdpi_returnDP() {
        val actual = intValue.dp
        Assert.assertEquals((intValue * 2), actual)
    }

    @Test
    @Config(qualifiers = "xxhdpi")
    fun dp_xxhdpi_returnDP() {
        val actual = intValue.dp
        Assert.assertEquals(intValue * 3, actual)
    }

    @Test
    @Config(qualifiers = "xxxhdpi")
    fun dp_xxxdpi_returnDP() {
        val actual = intValue.dp
        Assert.assertEquals(intValue * 4, actual)
    }

    @Test
    @Config(qualifiers = "ldpi")
    fun sp_ldpi_returnSP() {
        val actual = intValue.sp
        val expected = (intValue * 0.75).toInt()
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Config(qualifiers = "mdpi")
    fun sp_mdpi_returnSP() {
        val actual = intValue.sp
        Assert.assertEquals(intValue, actual)
    }

    @Test
    @Config(qualifiers = "hdpi")
    fun sp_hdpi_returnSP() {
        val actual = intValue.sp
        val expected = (intValue * 1.5).toInt()
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Config(qualifiers = "xhdpi")
    fun sp_xhdpi_returnSP() {
        val actual = intValue.sp
        Assert.assertEquals(intValue * 2, actual)
    }

    @Test
    @Config(qualifiers = "xxhdpi")
    fun sp_xxhdpi_returnSP() {
        val actual = intValue.sp
        Assert.assertEquals(intValue * 3, actual)
    }

    @Test
    @Config(qualifiers = "xxxhdpi")
    fun sp_xxxhdpi_returnSP() {
        val actual = intValue.sp
        Assert.assertEquals(intValue * 4, actual)
    }
}