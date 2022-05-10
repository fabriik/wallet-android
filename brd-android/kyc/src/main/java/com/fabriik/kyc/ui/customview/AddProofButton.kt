package com.fabriik.kyc.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.PartialAddProofButtonBinding

class AddProofButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val cornersRadius = resources.getDimensionPixelOffset(
        R.dimen.radius_l
    )

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

    fun setPreviewImage(image: Uri?) {
        binding.tvText.isVisible = image == null
        binding.ivTop.isVisible = image == null
        binding.ivBottom.isVisible = image == null
        binding.ivPreviewPhoto.isVisible = image != null

        Glide.with(context)
            .load(image)
            .transform(CenterCrop(), RoundedCorners(cornersRadius))
            .into(binding.ivPreviewPhoto)
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddProofButton)
        setText(typedArray.getString(R.styleable.AddProofButton_android_text))
        setIconTop(typedArray.getDrawable(R.styleable.AddProofButton_iconTop))
        setIconBottom(typedArray.getDrawable(R.styleable.AddProofButton_iconBottom))
        typedArray.recycle()
    }
}