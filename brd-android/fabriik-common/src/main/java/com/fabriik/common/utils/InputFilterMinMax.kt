package com.fabriik.common.utils

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(val min: Int, val max: Int) : InputFilter {

    constructor(min: String, max: String) : this(
        min = Integer.parseInt(min),
        max = Integer.parseInt(max)
    )

    override fun filter(
        source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence? {
        try {
            val input = Integer.parseInt(dest.toString() + source.toString())
            if (input in min..max) {
                return null
            }
        } catch (ex: NumberFormatException) {
            ex.printStackTrace()
        }
        return ""
    }
}