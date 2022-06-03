package com.fabriik.kyc.ui.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialCheckedTextviewBinding

class CheckedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private val binding: PartialCheckedTextviewBinding

    init {
        setBackgroundColor(Color.TRANSPARENT)

        binding = PartialCheckedTextviewBinding.inflate(
            LayoutInflater.from(context), this
        )

        parseAttributes(attrs)
    }

    fun setContent(string: String?) {
        binding.tvContent.text = string
    }

    fun setIcon(icon: Drawable?) {
        binding.icChecked.setImageDrawable(icon)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedTextView)
            setContent(typedArray.getString(R.styleable.CheckedTextView_contentText))
            setIcon(typedArray.getDrawable(R.styleable.CheckedTextView_iconImage))
            typedArray.recycle()
        }
    }
}
