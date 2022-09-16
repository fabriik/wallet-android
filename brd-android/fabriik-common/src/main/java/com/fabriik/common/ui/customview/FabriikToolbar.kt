package com.fabriik.common.ui.customview

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.fabriik.common.R
import com.fabriik.common.databinding.PartialFabriikToolbarBinding
import com.google.android.material.appbar.AppBarLayout

class FabriikToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {

    private val defaultTintColor = ContextCompat.getColor(
        context, R.color.light_icons_01
    )

    private val binding: PartialFabriikToolbarBinding

    init {
        stateListAnimator = StateListAnimator().apply {
            addState(IntArray(0), ObjectAnimator.ofFloat(this, "elevation", 0f))
        }

        setBackgroundColor(Color.TRANSPARENT)

        binding = PartialFabriikToolbarBinding.inflate(
            LayoutInflater.from(context), this
        )

        parseAttributes(attrs)
    }

    fun setTitle(string: String?) {
        binding.tvTitleSmall.text = string
        binding.tvTitleLarge.text = string
    }

    fun setShowBackButton(show: Boolean) {
        binding.btnBack.isInvisible = !show
    }

    fun setTitleType(@TitleType option: Int) {
        binding.tvTitleSmall.isVisible = option == TITLE_TYPE_SMALL
        binding.tvTitleLarge.isVisible = option == TITLE_TYPE_LARGE
    }

    fun setShowDismissButton(show: Boolean) {
        binding.btnDismiss.isInvisible = !show
    }

    fun setShowInfoButton(show: Boolean) {
        binding.btnInfo.isVisible = show
    }

    fun setBackButtonClickListener(listener: OnClickListener) {
        binding.btnBack.setOnClickListener(listener)
    }

    fun setDismissButtonClickListener(listener: OnClickListener) {
        binding.btnDismiss.setOnClickListener(listener)
    }

    fun setInfoButtonClickListener(listener: OnClickListener) {
        binding.btnInfo.setOnClickListener(listener)
    }

    fun setTintColor(@ColorInt color: Int) {
        binding.tvTitleSmall.setTextColor(color)
        binding.tvTitleLarge.setTextColor(color)
        binding.btnBack.imageTintList = ColorStateList.valueOf(color)
        binding.btnDismiss.imageTintList = ColorStateList.valueOf(color)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FabriikToolbar)
        setTitle(typedArray.getString(R.styleable.FabriikToolbar_title))
        setTintColor(typedArray.getColor(R.styleable.FabriikToolbar_tintColor, defaultTintColor))
        setShowBackButton(typedArray.getBoolean(R.styleable.FabriikToolbar_showBack, true))
        setTitleType(typedArray.getInt(R.styleable.FabriikToolbar_titleType, TITLE_TYPE_LARGE))
        setShowDismissButton(typedArray.getBoolean(R.styleable.FabriikToolbar_showDismiss, true))
        setShowInfoButton(typedArray.getBoolean(R.styleable.FabriikToolbar_showInfo, false))
        typedArray.recycle()
    }

    companion object {
        @IntDef(TITLE_TYPE_NONE, TITLE_TYPE_SMALL, TITLE_TYPE_LARGE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class TitleType

        const val TITLE_TYPE_NONE = 0
        const val TITLE_TYPE_SMALL = 1
        const val TITLE_TYPE_LARGE = 2
    }
}