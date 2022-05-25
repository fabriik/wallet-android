package com.fabriik.common.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import com.fabriik.common.R

class ErrorBubbleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(ContextThemeWrapper(context, R.style.FabriikErrorBubbleView), attrs) {

    init {
        setOnClickListener { visibility = GONE }
    }
}