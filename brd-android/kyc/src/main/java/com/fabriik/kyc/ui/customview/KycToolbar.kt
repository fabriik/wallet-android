package com.fabriik.kyc.ui.customview

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isInvisible
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialKycToolbarBinding
import com.google.android.material.appbar.AppBarLayout

class KycToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {

    private val binding: PartialKycToolbarBinding

    init {
        stateListAnimator = StateListAnimator().apply {
            addState(IntArray(0), ObjectAnimator.ofFloat(this, "elevation", 0f))
        }

        setBackgroundColor(Color.TRANSPARENT)

        binding = PartialKycToolbarBinding.inflate(
            LayoutInflater.from(context), this
        )

        parseAttributes(attrs)
    }

    fun setTitle(string: String?) {
        binding.tvTitle.text = string
    }

    fun setShowBackButton(show: Boolean) {
        binding.btnBack.isInvisible = !show
    }

    fun setShowDismissButton(show: Boolean) {
        binding.btnDismiss.isInvisible = !show
    }

    fun setBackButtonClickListener(listener: OnClickListener) {
        binding.btnBack.setOnClickListener(listener)
    }

    fun setDismissButtonClickListener(listener: OnClickListener) {
        binding.btnDismiss.setOnClickListener(listener)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KycToolbar)
        setTitle(typedArray.getString(R.styleable.KycToolbar_title))
        setShowBackButton(typedArray.getBoolean(R.styleable.KycToolbar_showBack, true))
        setShowDismissButton(typedArray.getBoolean(R.styleable.KycToolbar_showDismiss, true))
        typedArray.recycle()
    }
}