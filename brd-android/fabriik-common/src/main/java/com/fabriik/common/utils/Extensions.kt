package com.fabriik.common.utils

import android.R.attr.button
import android.app.Application
import android.content.res.Resources
import android.graphics.Paint
import android.text.Editable
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle


fun AndroidViewModel.getString(@StringRes string: Int, vararg formatArgs: Any?): String {
    return getApplication<Application>().applicationContext.getString(string, *formatArgs)
}

fun SavedStateHandle.toBundle() = bundleOf(
    *keys().map {
        Pair(it, get(it) as Any?)
    }.toTypedArray()
)

fun Button.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun Editable?.textOrEmpty() = if (isNullOrEmpty()) "" else toString()

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

