package com.fabriik.kyc.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialAddProofButtonBinding

class AddProofButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding: PartialAddProofButtonBinding = PartialAddProofButtonBinding.inflate(
        LayoutInflater.from(context), this
    )

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        setBackgroundResource(R.drawable.bg_add_document)
        parseAttributes(attrs)
    }

    fun setText(string: String?) {
        binding.tvText.text = string
    }

    fun setIconTop(drawable: Drawable?) {
        binding.ivTop.setImageDrawable(drawable)
    }

    fun setIconBottom(drawable: Drawable?) {
        binding.ivBottom.setImageDrawable(drawable)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddProofButton)
        setText(typedArray.getString(R.styleable.AddProofButton_android_text))
        setIconTop(typedArray.getDrawable(R.styleable.AddProofButton_iconTop))
        setIconBottom(typedArray.getDrawable(R.styleable.AddProofButton_iconBottom))
        typedArray.recycle()
    }
}