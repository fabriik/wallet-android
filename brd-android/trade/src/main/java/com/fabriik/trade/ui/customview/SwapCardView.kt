package com.fabriik.trade.ui.customview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.utils.dp
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.databinding.ViewSwapCardBinding
import com.google.android.material.card.MaterialCardView
import java.math.BigDecimal

class SwapCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: ViewSwapCardBinding
    private var callback: Callback? = null

    private val alphaAnimationForAnimatedViews : List<ObjectAnimator>

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

    fun setSendingNetworkFee(fee: AmountData?) {
        binding.tvSellingCurrencyNetworkFee.isVisible = fee != null
        binding.tvSellingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency)
            binding.tvSellingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
        }
    }

    fun setReceivingNetworkFee(fee: AmountData?) {
        binding.tvBuyingCurrencyNetworkFee.isVisible = fee != null
        binding.tvBuyingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency)
            binding.tvBuyingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
        }
    }

    fun setSourceCurrencyTitle(title: String) {
        binding.viewInputSellingCurrency.setTitle(title)
    }

    fun setSourceFiatAmount(amount: BigDecimal) {
        binding.viewInputSellingCurrency.setFiatAmount(amount)
    }

    fun setSourceCryptoAmount(amount: BigDecimal) {
        binding.viewInputSellingCurrency.setCryptoAmount(amount)
    }

    fun setDestinationFiatAmount(amount: BigDecimal) {
        binding.viewInputBuyingCurrency.setFiatAmount(amount)
    }

    fun setDestinationCryptoAmount(amount: BigDecimal) {
        binding.viewInputBuyingCurrency.setCryptoAmount(amount)
    }

    fun startReplaceAnimation(replaceAnimationCompleted: () -> Unit) {
        val sourceSelectionView = binding.viewInputSellingCurrency.getSelectionView()
        val sourceSelectionViewPosition = IntArray(2)
        sourceSelectionView.getLocationOnScreen(sourceSelectionViewPosition)

        val destinationSelectionView = binding.viewInputBuyingCurrency.getSelectionView()
        val destinationSelectionViewPosition = IntArray(2)
        destinationSelectionView.getLocationOnScreen(destinationSelectionViewPosition)

        val diffY = (destinationSelectionViewPosition[1] - sourceSelectionViewPosition[1]).toFloat()

        sourceSelectionView.startAnimation(
            TranslateAnimation(0f, 0f, 0f, diffY).apply {
                duration = REPLACE_CURRENCIES_DURATION
            }
        )

        destinationSelectionView.startAnimation(
            TranslateAnimation(0f, 0f, 0f, -diffY).apply {
                duration = REPLACE_CURRENCIES_DURATION
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        replaceAnimationCompleted()
                    }
                })
            }
        )

        val animatorSet = AnimatorSet()
        animatorSet.duration = FADE_ANIMATION_DURATION
        animatorSet.playTogether(alphaAnimationForAnimatedViews)
        animatorSet.start()
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
        const val FADE_ANIMATION_DURATION = 1000L
        const val REPLACE_CURRENCIES_DURATION = 500L
    }
}
