package com.fabriik.trade.ui.customview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.tools.util.Utils
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.utils.dp
import com.fabriik.trade.R
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.databinding.ViewSwapCardBinding
import com.google.android.material.card.MaterialCardView
import java.math.BigDecimal

class SwapCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: ViewSwapCardBinding
    private var callback: Callback? = null

    private val alphaAnimationForAnimatedViews: List<ObjectAnimator>

    init {
        radius = 16.dp.toFloat()
        binding = ViewSwapCardBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        with(binding) {
            btnSwap.setOnClickListener { callback?.onReplaceCurrenciesClicked() }

            viewInputBuyingCurrency.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    callback?.onDestinationCurrencyClicked()
                }

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    callback?.onBuyingCurrencyFiatAmountChanged(amount)
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    callback?.onBuyingCurrencyCryptoAmountChanged(amount)
                }
            })

            viewInputSellingCurrency.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    callback?.onSourceCurrencyClicked()
                }

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    callback?.onSellingCurrencyFiatAmountChanged(amount)
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    callback?.onSellingCurrencyCryptoAmountChanged(amount)
                }
            })

            val animatedViews = mutableListOf<View>(
                tvBuyingCurrencyNetworkFee, tvBuyingCurrencyNetworkFeeTitle,
                tvSellingCurrencyNetworkFee, tvSellingCurrencyNetworkFeeTitle
            ).apply {
                addAll(viewInputBuyingCurrency.getAnimatedViews())
                addAll(viewInputSellingCurrency.getAnimatedViews())
            }

            alphaAnimationForAnimatedViews = animatedViews.map {
                ObjectAnimator.ofFloat(it, "alpha", 1f, 0f, 1f)
            }
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setFiatCurrency(currency: String) {
        binding.viewInputBuyingCurrency.setFiatCurrency(currency)
        binding.viewInputSellingCurrency.setFiatCurrency(currency)
    }

    fun setSourceCurrency(currency: String?) {
        binding.viewInputSellingCurrency.setCryptoCurrency(currency)
    }

    fun setDestinationCurrency(currency: String?) {
        binding.viewInputBuyingCurrency.setCryptoCurrency(currency)
    }

    fun setSendingNetworkFee(fee: FeeAmountData?) {
        binding.tvSellingCurrencyNetworkFee.isVisible = fee != null
        binding.tvSellingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency, SCALE_FIAT)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency, SCALE_CRYPTO)
            binding.tvSellingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
            binding.tvSellingCurrencyNetworkFeeTitle.setText(R.string.Swap_sendNetworkFeeNotIncluded)
        }
    }

    fun setReceivingNetworkFee(fee: FeeAmountData?) {
        binding.tvBuyingCurrencyNetworkFee.isVisible = fee != null
        binding.tvBuyingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency, SCALE_FIAT)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency, SCALE_CRYPTO)
            binding.tvBuyingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
            binding.tvBuyingCurrencyNetworkFeeTitle.setText(R.string.Swap_ReceiveNetworkFee)
        }
    }

    fun setSourceCurrencyTitle(title: String) {
        binding.viewInputSellingCurrency.setTitle(title)
    }

    fun setSourceFiatAmount(amount: BigDecimal, changeByUser: Boolean) {
        binding.viewInputSellingCurrency.setFiatAmount(amount, changeByUser)
    }

    fun setSourceCryptoAmount(amount: BigDecimal, changeByUser: Boolean) {
        binding.viewInputSellingCurrency.setCryptoAmount(amount, changeByUser)
    }

    fun setDestinationFiatAmount(amount: BigDecimal, changeByUser: Boolean) {
        binding.viewInputBuyingCurrency.setFiatAmount(amount, changeByUser)
    }

    fun setDestinationCryptoAmount(amount: BigDecimal, changeByUser: Boolean) {
        binding.viewInputBuyingCurrency.setCryptoAmount(amount, changeByUser)
    }

    fun startReplaceAnimation(replaceAnimationStarted: () -> Unit, replaceAnimationCompleted: () -> Unit) {
        binding.viewInputSellingCurrency.prepareForAnimation { sourceViewPositionY, sourceOriginalView, sourceAnimationView ->
            binding.viewInputBuyingCurrency.prepareForAnimation { destinationViewPositionY, destinationOriginalView, destinationAnimationView ->
                val diffY = (destinationViewPositionY - sourceViewPositionY).toFloat()

                replaceAnimationStarted()

                postDelayed(100) {
                    startReplaceAnimationForView(
                        diffY = diffY,
                        originalView = sourceOriginalView,
                        animatedView = sourceAnimationView
                    )
                    startReplaceAnimationForView(
                        diffY = -diffY,
                        callback = replaceAnimationCompleted,
                        originalView = destinationOriginalView,
                        animatedView = destinationAnimationView
                    )

                    val animatorSet = AnimatorSet()
                    animatorSet.duration = FADE_ANIMATION_DURATION
                    animatorSet.playTogether(alphaAnimationForAnimatedViews)
                    animatorSet.start()
                }
            }
        }
    }

    fun setInputFieldsEnabled(enabled: Boolean) {
        binding.viewInputBuyingCurrency.setInputFieldsEnabled(enabled)
        binding.viewInputSellingCurrency.setInputFieldsEnabled(enabled)
    }

    fun clearCurrentInputFieldFocus() {
        Utils.hideKeyboard(binding.root.context)
        binding.viewInputBuyingCurrency.clearCurrentInputFieldFocus()
        binding.viewInputSellingCurrency.clearCurrentInputFieldFocus()
    }

    fun hideKeyboard() {
        with(binding) {
            viewInputBuyingCurrency.hideKeyboard()
            viewInputSellingCurrency.hideKeyboard()
        }
    }

    fun setReplaceButtonEnabled(enabled: Boolean) {
        binding.btnSwap.isEnabled = enabled
    }

    private fun startReplaceAnimationForView(animatedView: View, originalView: View, diffY: Float, callback: () -> Unit = {}) {
        animatedView.startAnimation(
            TranslateAnimation(0f, 0f, 0f, diffY).apply {
                duration = REPLACE_CURRENCIES_DURATION
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        originalView.isVisible = true
                        animatedView.isInvisible = true
                        callback()
                    }
                })
            }
        )
    }

    interface Callback {
        fun onReplaceCurrenciesClicked()
        fun onDestinationCurrencyClicked()
        fun onSourceCurrencyClicked()
        fun onSellingCurrencyFiatAmountChanged(amount: BigDecimal)
        fun onSellingCurrencyCryptoAmountChanged(amount: BigDecimal)
        fun onBuyingCurrencyFiatAmountChanged(amount: BigDecimal)
        fun onBuyingCurrencyCryptoAmountChanged(amount: BigDecimal)
    }

    companion object {
        const val SCALE_FIAT = 2
        const val SCALE_CRYPTO = 8
        const val FADE_ANIMATION_DURATION = 1000L
        const val REPLACE_CURRENCIES_DURATION = 500L
    }
}
