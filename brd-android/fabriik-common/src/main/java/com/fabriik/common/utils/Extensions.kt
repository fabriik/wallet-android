package com.fabriik.common.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.text.Editable
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.text.DecimalFormat

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
    postDelayed({
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

fun Editable?.asInt() = if (isNullOrEmpty()) null else toString().toInt()

fun min(a: BigDecimal, b: BigDecimal) = when {
    b < a -> b
    else -> a
}

fun max(a: BigDecimal, b: BigDecimal) = when {
    b > a -> b
    else -> a
}

fun EditText.disableCopyPaste() {
    isLongClickable = false
    setTextIsSelectable(false)
    customSelectionActionModeCallback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}

fun EditText.afterTextChangedDebounceFocused(action: (text: Editable?) -> Unit) {
    afterTextChangedDebounce(500) {
        if (hasFocus()) {
            action(it)
        }
    }
}

fun EditText.afterTextChangedDebounce(delayMillis: Long, callback: (Editable) -> Unit) {
    var lastInput = ""
    var debounceJob: Job? = null
    val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    doAfterTextChanged {
        if (it != null) {
            val newInput = it.toString()
            debounceJob?.cancel()

            if (lastInput != newInput) {
                lastInput = newInput

                debounceJob = uiScope.launch {
                    delay(delayMillis)

                    if (lastInput == newInput) {
                        callback(it)
                    }
                }
            }
        }
    }
}

fun TextInputLayout.showErrorState(errorState: Boolean) {
    foreground = ContextCompat.getDrawable(
        context, if (errorState) R.drawable.bg_input_view_error else R.drawable.bg_input_view
    )
}

fun Array<String>.contains(other: String, ignoreCase: Boolean = false): Boolean {
    return any { it.equals(other, ignoreCase) }
}

fun Float.formatPercent(): String {
    val decimalFormat = DecimalFormat("#.##");
    return "${decimalFormat.format(this)}%"
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
