package com.fabriik.common.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.fabriik.common.R
import com.fabriik.common.utils.dp

class FabriikSwitch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
        setPadding(4.dp)
        setBackgroundResource(R.drawable.bg_rounded_tertiary)
    }
}