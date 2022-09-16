package com.fabriik.common.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeZero: Int = 10, digitsAfterZero: Int = 10) : InputFilter {

    private val pattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")

    override fun filter(
        source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence? {
        dest ?: return null

        val matcher = pattern.matcher(dest)
        if (!matcher.matches()) {
            return ""
        }
        return null
    }
}