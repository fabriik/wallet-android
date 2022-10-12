package com.fabriik.buy.ui.features.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.tools.util.Utils.hideKeyboard
import com.breadwallet.util.formatFiatForUi
import com.fabriik.buy.R
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.buy.databinding.FragmentBuyInputBinding
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.buy.ui.features.orderpreview.OrderPreviewFragment
import com.fabriik.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.fabriik.buy.ui.features.timeout.PaymentTimeoutFragment
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.customview.CurrencyInputView
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal

class BuyInputFragment : Fragment(),
    FabriikView<BuyInputContract.State, BuyInputContract.Effect> {

    private lateinit var binding: FragmentBuyInputBinding
    private val viewModel: BuyInputViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyInputBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BuyInputContract.Event.DismissClicked)
            }

            viewCryptoInput.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    viewModel.setEvent(BuyInputContract.Event.CryptoCurrencyClicked)
                }

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    viewModel.setEvent(BuyInputContract.Event.FiatAmountChange(amount))
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    viewModel.setEvent(BuyInputContract.Event.CryptoAmountChange(amount))
                }
            })

            btnContinue.setOnClickListener {
                hideKeyboard(binding.root.context)
                viewModel.setEvent(BuyInputContract.Event.ContinueClicked)
            }

            cvPaymentMethod.setOnClickListener {
                viewModel.setEvent(BuyInputContract.Event.PaymentMethodClicked)
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        // listen for origin currency changes
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_CRYPTO_SELECTION,
            this
        ) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(BuyInputContract.Event.CryptoCurrencyChanged(currency))
            }
        }

        // listen for destination currency changes
        parentFragmentManager.setFragmentResultListener(PaymentMethodFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getParcelable(PaymentMethodFragment.RESULT_KEY) as PaymentMethodFragment.Result?
            result?.let { viewModel.setEvent(BuyInputContract.Event.PaymentMethodResultReceived(it)) }
        }

        // listen for payment timeout callback
        parentFragmentManager.setFragmentResultListener(PaymentTimeoutFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(PaymentTimeoutFragment.RESULT_KEY)
            if (result == PaymentTimeoutFragment.RESULT_TRY_AGAIN) {
                viewModel.setEvent(BuyInputContract.Event.QuoteTimeoutRetry)
            }
        }

        // listen for order preview callback
        parentFragmentManager.setFragmentResultListener(OrderPreviewFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(OrderPreviewFragment.RESULT_KEY)
            if (result == OrderPreviewFragment.RESULT_NEW_QUOTE) {
                viewModel.setEvent(BuyInputContract.Event.QuoteTimeoutRetry)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }

    override fun render(state: BuyInputContract.State) {
        when (state) {
            is BuyInputContract.State.Error ->
                handleErrorState(state)

            is BuyInputContract.State.Loading ->
                handleLoadingState(state)

            is BuyInputContract.State.Loaded ->
                handleLoadedState(state)
        }
    }

    override fun handleEffect(effect: BuyInputContract.Effect) {
        when (effect) {
            BuyInputContract.Effect.Dismiss ->
                requireActivity().finish()

            BuyInputContract.Effect.AddCard ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionAddCard(
                        AddCardFlow.BUY
                    )
                )

            is BuyInputContract.Effect.PaymentMethodSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionPaymentMethod(
                        AddCardFlow.BUY
                    )
                )

            is BuyInputContract.Effect.OpenOrderPreview ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionOrderPreview(
                        networkFee = effect.networkFee,
                        fiatAmount = effect.fiatAmount,
                        fiatCurrency = effect.fiatCurrency,
                        quoteResponse = effect.quoteResponse,
                        cryptoCurrency = effect.cryptoCurrency,
                        paymentInstrument = effect.paymentInstrument
                    )
                )

            is BuyInputContract.Effect.CryptoSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_CRYPTO_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                    )
                )

            is BuyInputContract.Effect.ShowError ->
                FabriikToastUtil.showError(binding.root, effect.message)

            is BuyInputContract.Effect.UpdateFiatAmount ->
                binding.viewCryptoInput.setFiatAmount(effect.amount, effect.changeByUser)

            is BuyInputContract.Effect.UpdateCryptoAmount ->
                binding.viewCryptoInput.setCryptoAmount(effect.amount, effect.changeByUser)
        }
    }

    private fun handleErrorState(state: BuyInputContract.State.Error) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun handleLoadingState(state: BuyInputContract.State.Loading) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun handleLoadedState(state: BuyInputContract.State.Loaded) {
        with(binding) {
            btnContinue.isEnabled = state.continueButtonEnabled

            tvRateValue.text = RATE_FORMAT.format(
                state.cryptoCurrency,
                state.oneCryptoUnitToFiatRate.formatFiatForUi(
                    currencyCode = state.fiatCurrency,
                    showCurrencyName = true
                )
            )

            binding.viewCreditCard.isVisible = state.selectedPaymentMethod != null
            binding.tvSelectPaymentMethod.isVisible = state.selectedPaymentMethod == null
            state.selectedPaymentMethod?.let { viewCreditCard.setPaymentInstrument(it) }

            viewCryptoInput.setFiatCurrency(state.fiatCurrency)
            viewCryptoInput.setCryptoCurrency(state.cryptoCurrency)

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
            quoteLoadingIndicator.isVisible = state.rateLoadingVisible
            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingVisible

            tvRateValue.isVisible = !state.rateLoadingVisible && state.quoteResponse != null

            val minAmount = state.quoteResponse?.minimumValueUsd ?: BigDecimal.ZERO
            val minText = minAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = false,
                showCurrencySymbol = false
            )

            val maxAmount = state.quoteResponse?.maximumValueUsd ?: BigDecimal.ZERO
            val maxText = maxAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = false,
                showCurrencySymbol = false
            )

            tvLimitsDisclaimer.text = getString(R.string.Buy_BuyLimits, minText, maxText)
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_CRYPTO_SELECTION = "req_code_crypto_select"
    }
}