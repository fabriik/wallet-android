package com.fabriik.common.ui.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.fabriik.common.utils.dp

class FabriikSwitch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvLeftOption : TextView
    private val tvRightOption : TextView

    init {
        weightSum = 1f
        orientation = HORIZONTAL

        setPadding(4.dp)
        setBackgroundResource(R.drawable.bg_rounded_tertiary)

        tvLeftOption = addOptionView("Option 1")
        tvRightOption = addOptionView("Option 2")
    }

    private fun addOptionView(text: String) : TextView {
        val tvOption = TextView(ContextThemeWrapper(context, R.style.FabriikCustomSwitchItemStyle))
        tvOption.text = text
        addView(tvOption, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f))
        return tvOption
    }
}