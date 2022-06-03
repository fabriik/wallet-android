package com.fabriik.common.utils

import android.R.attr.button
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
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

fun EditText.showKeyboard() {
    postDelayed( {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        requestFocus()
        manager?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        setSelection(text.length)
    }, 100)
}

fun EditText.hideKeyboard() {
    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    manager?.hideSoftInputFromWindow(windowToken, 0)
}

fun Editable?.textOrEmpty() = if (isNullOrEmpty()) "" else toString()

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
