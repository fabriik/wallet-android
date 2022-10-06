package com.fabriik.trade.ui.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.breadwallet.tools.util.TokenUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.fabriik.common.utils.viewScope
import com.fabriik.trade.R
import com.fabriik.trade.databinding.ViewCryptoIconBinding
import kotlinx.coroutines.*
import java.io.File
import java.util.Locale
import com.bumptech.glide.request.target.Target

class CryptoIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val defaultTokenColor = R.color.light_gray
    private val binding = ViewCryptoIconBinding.inflate(
        LayoutInflater.from(context), this
    )

    init {
        setBackgroundResource(R.drawable.token_icon_background_transparent)
    }

    fun postLoadIcon(currencyCode: String) {
        post { loadIcon(viewScope, currencyCode) }
    }

    fun loadIcon(scope: CoroutineScope, currencyCode: String, callback: () -> Unit = {}) {
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
                    Glide.with(context)
                        .load(File(tokenIconPath))
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                callback()
                                return false
                            }
                        })
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