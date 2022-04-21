package com.fabriik.signup.utils

import android.app.Application
import android.content.Context
import android.graphics.Paint
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.fabriik.signup.R
import com.fabriik.signup.utils.validators.Validator

internal fun SavedStateHandle.toBundle() = bundleOf(
    *keys().map {
        Pair(it, get(it) as Any?)
    }.toTypedArray()
)

internal fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

internal fun TextView.clickableSpan(
    fullTextRes: Int,
    clickableParts: Map<Int, () -> Unit>,
    underline: Boolean = true
) {
    val clickablePartsAsStrings = clickableParts.map {
        context.getString(it.key) to it.value
    }.toMap()

    val clickableTexts = clickablePartsAsStrings.keys.toTypedArray()
    val fullText = context.getString(fullTextRes, *clickableTexts)
    val spannable = fullText.toSpannable()

    clickablePartsAsStrings.forEach {
        val startIndex = fullText.indexOf(it.key)
        val endIndex = startIndex + it.key.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                it.value()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = underline
            }
        }

        spannable.setSpan(
            clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
}

internal fun Fragment.hideKeyboard()  {
    ViewCompat.getWindowInsetsController(requireView())
        ?.hide(WindowInsetsCompat.Type.ime())
}

internal fun EditText.setValidator(validator: Validator) {
    doAfterTextChanged {
        val valid = validator(it?.toString() ?: "")
        setValidationState(valid)
    }
}

private fun EditText.setValidationState(valid: Boolean) {
    val background = if (valid) {
        R.drawable.fabriik_edittext_border_green
    } else {
        R.drawable.fabriik_edittext_border
    }

    val drawable = if (valid) {
        R.drawable.ic_green_check_small
    } else {
        null
    }

    setBackgroundResource(background)
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        0, 0, drawable ?: 0, 0
    )
}

internal fun AndroidViewModel.getString(@StringRes string: Int) : String {
    return getApplication<Application>().applicationContext.getString(string)
}