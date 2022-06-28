package com.fabriik.common.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
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
        val tvOption = TextView(context)
        tvOption.setPadding(12.dp)
        tvOption.text = text
        tvOption.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        tvOption.setBackgroundResource(R.drawable.bg_switch_item)
        addView(tvOption, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f))
        return tvOption
    }
}