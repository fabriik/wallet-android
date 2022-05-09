package com.fabriik.common.utils

import android.app.Application
import android.text.Editable
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

fun Editable?.toStringSafe() = if (isNullOrEmpty()) "" else toString()