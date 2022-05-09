package com.fabriik.signup.utils

import android.graphics.Paint
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.fabriik.signup.R

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

internal fun EditText.setValidationState(valid: Boolean) {
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