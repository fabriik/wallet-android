package com.fabriik.trade.ui.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.breadwallet.tools.util.TokenUtil
import com.fabriik.trade.R
import com.fabriik.trade.databinding.ViewIconBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.io.File
import java.util.Locale

class IconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val defaultTokenColor = R.color.light_gray
    private val binding = ViewIconBinding.inflate(
        LayoutInflater.from(context), this
    )

    init {
        setBackgroundResource(R.drawable.token_icon_background_transparent)
    }

    fun loadIconForCurrency(scope: CoroutineScope, currencyCode: String) {
        scope.launch {
            // Get icon for currency
            val tokenIconPath = Dispatchers.Default {
                TokenUtil.getTokenIconPath(currencyCode, false)
            }

            // Get icon color
            val tokenColor = Dispatchers.Default {
                TokenUtil.getTokenStartColor(currencyCode)
            }

            ensureActive()

            with(binding) {
                val iconDrawable = background as GradientDrawable

                if (tokenIconPath.isNullOrBlank()) {
                    binding.ivIcon.isVisible = false
                    binding.tvLetter.isVisible = true
                    binding.tvLetter.text = currencyCode.take(1).toUpperCase(Locale.ROOT)
                } else {
                    Picasso.get()
                        .load(File(tokenIconPath))
                        .into(binding.ivIcon)

                    binding.ivIcon.isVisible = true
                    binding.tvLetter.isVisible = false
                }

                // set icon color
                iconDrawable.setColor(
                    if (tokenColor.isNullOrBlank()) {
                        ContextCompat.getColor(root.context, defaultTokenColor)
                    } else {
                        Color.parseColor(tokenColor)
                    }
                )
            }
        }
    }
}