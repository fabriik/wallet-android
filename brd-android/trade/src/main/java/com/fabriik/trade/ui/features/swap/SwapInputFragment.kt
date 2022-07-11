package com.fabriik.trade.ui.features.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapInputBinding
import com.fabriik.trade.ui.customview.SwapCardView
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.fabriik.trade.ui.dialog.ConfirmationArgs
import com.fabriik.trade.ui.dialog.ConfirmationDialog
import com.fabriik.trade.ui.dialog.Currency
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment

class SwapInputFragment : Fragment(),
    FabriikView<SwapInputContract.State, SwapInputContract.Effect> {

    private lateinit var binding: FragmentSwapInputBinding
    private val viewModel: SwapInputViewModel by viewModels()

    private val cardSwapCallback = object : SwapCardView.Callback {
        override fun onReplaceCurrenciesClicked() {
            viewModel.setEvent(SwapInputContract.Event.ReplaceCurrenciesClicked)
        }

        override fun onBuyingCurrencySelectorClicked() {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyClicked)
        }

        override fun onSellingCurrencySelectorClicked() {
            viewModel.setEvent(SwapInputContract.Event.OriginCurrencyClicked)
        }

        override fun onSellingCurrencyFiatAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.OriginCurrencyFiatAmountChange(amount))
        }

        override fun onSellingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.OriginCurrencyCryptoAmountChange(amount))
        }

        override fun onBuyingCurrencyFiatAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyFiatAmountChange(amount))
        }

        override fun onBuyingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyCryptoAmountChange(amount))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swap_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapInputBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SwapInputContract.Event.DismissClicked)
            }

            cvSwap.setFiatCurrency(BRSharedPrefs.getPreferredFiatIso())
            cvSwap.setCallback(cardSwapCallback)

            btnConfirm.setOnClickListener {
                viewModel.setEvent(SwapInputContract.Event.ConfirmClicked)
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
        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_ORIGIN_SELECTION, this) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(
                    SwapInputContract.Event.OriginCurrencyChanged(currency)
                )
            }
        }

        // listen for destination currency changes
        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_DESTINATION_SELECTION, this) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(
                    SwapInputContract.Event.DestinationCurrencyChanged(currency)
                )
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }

    override fun render(state: SwapInputContract.State) {
        with(binding) {
            cvSwap.setSellingCurrencyTitle(getString(R.string.Swap_Input_IHave, state.originCurrencyBalance, state.originCurrency))
            cvSwap.setOriginCurrency(state.originCurrency)
            cvSwap.setSendingNetworkFee(state.sendingNetworkFee)
            cvSwap.setDestinationCurrency(state.destinationCurrency)
            cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)

            viewTimer.setProgress(SwapInputViewModel.QUOTE_TIMER, state.timer)
            tvRateValue.text = RATE_FORMAT.format(
                state.originCurrency, state.rateOriginToDestinationCurrency, state.destinationCurrency
            )

            viewTimer.isVisible = !state.quoteLoading
            tvRateValue.isVisible = !state.quoteLoading
            quoteLoadingIndicator.isVisible = state.quoteLoading
        }
    }

    override fun handleEffect(effect: SwapInputContract.Effect) {
        when (effect) {
            SwapInputContract.Effect.Dismiss ->
                requireActivity().finish()

            is SwapInputContract.Effect.OriginSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_ORIGIN_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                    )
                )

            is SwapInputContract.Effect.DestinationSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_DESTINATION_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                        sourceCurrency = effect.sourceCurrency
                    )
                )

            SwapInputContract.Effect.ConfirmDialog ->
                showConfirmDialog()
        }
    }

    private fun showConfirmDialog() {
        val state = viewModel.currentState
        val fm = requireActivity().supportFragmentManager
        val originCurrency =
            Currency(
                state.originCurrency,
                state.originCryptoAmount,
                state.originFiatAmount
            )
        val destinationCurrency =
            Currency(
                state.destinationCurrency,
                state.destinationCryptoAmount,
                state.destinationFiatAmount
            )

        //TODO: Update network fees
        val sendingFeeCurrency = Currency("BSV", BigDecimal.ZERO, BigDecimal.ZERO)
        val receivingFeeCurrency = Currency("BTC", BigDecimal.ZERO, BigDecimal.ZERO)

        val args = ConfirmationArgs(
            swapFromCurrency = originCurrency,
            swapToCurrency = destinationCurrency,
            sendingFeeCurrency = sendingFeeCurrency,
            receivingFeeCurrency = receivingFeeCurrency,
            rate = state.rateOriginToDestinationCurrency.toString(), //TODO prepare rate string
            totalCost = (BigDecimal.ZERO) //TODO convert and calculate total cost
        )
        ConfirmationDialog(args).show(fm, ConfirmationDialog.CONFIRMATION_TAG)
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %f %s"
        const val REQUEST_KEY_ORIGIN_SELECTION = "req_code_origin_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
    }
}