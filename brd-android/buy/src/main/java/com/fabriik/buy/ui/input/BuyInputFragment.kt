package com.fabriik.buy.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentBuyInputBinding
import kotlinx.coroutines.flow.collect
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.breadwallet.tools.util.Utils.hideKeyboard
import com.breadwallet.util.formatFiatForUi
import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.customview.CurrencyInputView
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment
import com.fabriik.trade.ui.features.swap.SwapInputViewModel
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
            val selectedPaymentInstrument = bundle.getParcelable(PaymentMethodFragment.RESULT_KEY) as PaymentInstrument?
            if (selectedPaymentInstrument != null) {
                viewModel.setEvent(BuyInputContract.Event.PaymentMethodChanged(selectedPaymentInstrument))
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
                findNavController().navigate(BuyInputFragmentDirections.actionAddCard())

            is BuyInputContract.Effect.PaymentMethodSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionPaymentMethod(
                        effect.paymentInstruments.toTypedArray()
                    )
                )

            is BuyInputContract.Effect.OpenOrderPreview ->
                findNavController().navigate(BuyInputFragmentDirections.actionOrderPreview())

            is BuyInputContract.Effect.CryptoSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_CRYPTO_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                    )
                )

            is BuyInputContract.Effect.ShowToast -> if (effect.redInfo) {
                FabriikToastUtil.showRedInfo(binding.root, effect.message)
            } else {
                FabriikToastUtil.showInfo(binding.root, effect.message)
            }

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
                state.exchangeRate.formatFiatForUi(
                    state.fiatCurrency
                )
            )

            //todo: change to custom view
            tvSelectPaymentMethod.text = state.selectedPaymentMethod?.last4Numbers?.let { "**** **** **** $it" } ?: getString(R.string.Buy_Input_SelectPaymentMethod)

            viewCryptoInput.setFiatCurrency(state.fiatCurrency)
            viewCryptoInput.setCryptoCurrency(state.cryptoCurrency)

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
            quoteLoadingIndicator.isVisible = state.rateLoadingVisible
            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingVisible

            if (state.isKyc2) {
                tvKycMessage.text = getString(com.fabriik.trade.R.string.Swap_KycLimits_Kyc1)
            } else if(state.isKyc1) {
                tvKycMessage.text = getString(com.fabriik.trade.R.string.Swap_KycLimits_Kyc2)
            }
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_CRYPTO_SELECTION = "req_code_crypto_select"
    }
}