package com.fabriik.common.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan

fun String.toSpannableString() = SpannableString(this)

fun SpannableString.bold(start: Int, end: Int): SpannableString {
    this.setSpan(StyleSpan(Typeface.BOLD), start, end, 0)
    return this
}
