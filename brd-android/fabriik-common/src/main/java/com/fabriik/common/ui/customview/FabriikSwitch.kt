package com.fabriik.common.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IntDef
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FabriikSwitch)
        val leftOptionText = typedArray.getString(R.styleable.FabriikSwitch_leftOptionText) ?: ""
        val rightOptionText = typedArray.getString(R.styleable.FabriikSwitch_rightOptionText) ?: ""
        val selectedOption = typedArray.getInt(R.styleable.FabriikSwitch_selectedOption, 0)
        typedArray.recycle()

        tvLeftOption = addOptionView(leftOptionText)
        tvRightOption = addOptionView(rightOptionText)

        setSelectedItem(selectedOption)
    }

    private fun setSelectedItem(@SwitchOption selectedOption: Int) {
        tvLeftOption.isActivated = selectedOption == OPTION_LEFT
        tvRightOption.isActivated = selectedOption == OPTION_RIGHT
    }

    private fun addOptionView(text: CharSequence) : TextView {
        val tvOption = TextView(ContextThemeWrapper(context, R.style.FabriikCustomSwitchItemStyle))
        tvOption.text = text
        addView(tvOption, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f))
        return tvOption
    }

    companion object {
        @IntDef(OPTION_NONE, OPTION_LEFT, OPTION_RIGHT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SwitchOption

        const val OPTION_NONE = 0
        const val OPTION_LEFT = 1
        const val OPTION_RIGHT = 2
    }
}