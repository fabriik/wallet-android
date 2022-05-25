package com.fabriik.common.data

import androidx.core.content.ContextCompat
import com.fabriik.common.R
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.showErrorState(errorState: Boolean) {
    foreground = ContextCompat.getDrawable(
        context, if (errorState) R.drawable.bg_input_view_error else R.drawable.bg_input_view
    )
}