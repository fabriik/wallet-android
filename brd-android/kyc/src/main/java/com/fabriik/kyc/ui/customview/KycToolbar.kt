package com.fabriik.kyc.ui.customview

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialKycToolbarBinding
import com.google.android.material.appbar.AppBarLayout

class KycToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {

    private val defaultTintColor = ContextCompat.getColor(
        context, R.color.light_icons_01
    )

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

    fun setShowTitleButton(show: Boolean) {
        binding.tvTitle.isVisible = show
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

    fun setTintColor(@ColorInt color: Int) {
        binding.tvTitle.setTextColor(color)
        binding.btnBack.imageTintList = ColorStateList.valueOf(color)
        binding.btnDismiss.imageTintList = ColorStateList.valueOf(color)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KycToolbar)
        setTitle(typedArray.getString(R.styleable.KycToolbar_title))
        setTintColor(typedArray.getColor(R.styleable.KycToolbar_tintColor, defaultTintColor))
        setShowBackButton(typedArray.getBoolean(R.styleable.KycToolbar_showBack, true))
        setShowTitleButton(typedArray.getBoolean(R.styleable.KycToolbar_showTitle, true))
        setShowDismissButton(typedArray.getBoolean(R.styleable.KycToolbar_showDismiss, true))
        typedArray.recycle()
    }
}