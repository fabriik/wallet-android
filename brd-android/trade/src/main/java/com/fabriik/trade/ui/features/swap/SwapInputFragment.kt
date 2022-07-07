package com.fabriik.trade.ui.features.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapInputBinding
import kotlinx.coroutines.flow.collect
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.breadwallet.breadbox.formatCryptoForUi
import com.fabriik.common.utils.FabriikToastUtil

class SwapInputFragment : Fragment(),
    FabriikView<SwapInputContract.State, SwapInputContract.Effect> {

    private lateinit var binding: FragmentSwapInputBinding
    private val viewModel: SwapInputViewModel by viewModels()

    /*private val cardSwapCallback = object : SwapCardView.Callback {
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
    }*/

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

            /*cvSwap.setFiatCurrency(BRSharedPrefs.getPreferredFiatIso())
            cvSwap.setCallback(cardSwapCallback)

            switchMinMax.setCallback {
                when (it) {
                    FabriikSwitch.OPTION_LEFT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMinAmountClicked)
                    FabriikSwitch.OPTION_RIGHT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMaxAmountClicked)
                }
            }*/

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
/*
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
        }*/
    }

    override fun render(state: SwapInputContract.State) {
        when (state) {
            is SwapInputContract.State.Error ->
                handleErrorState(state)

            is SwapInputContract.State.Loading ->
                handleLoadingState(state)

            is SwapInputContract.State.Loaded ->
                handleLoadedState(state)

            /*is SwapInputContract.State.Loaded -> with(binding) {
                content.isVisible = true
                initialLoadingIndicator.isVisible = false

                cvSwap.setSendingNetworkFee(state.sendingNetworkFee)
                cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)
                cvSwap.setOriginCurrency(state.selectedPair.baseCurrency)
                cvSwap.setDestinationCurrency(state.selectedPair.termCurrency)
                cvSwap.setSellingCurrencyTitle(
                    getString(
                        R.string.Swap_Input_IHave, state.sourceCurrencyBalance.formatCryptoForUi(
                            state.selectedPair.baseCurrency
                        )
                    )
                )

                when (state.quoteState) {
                    is SwapInputContract.QuoteState.Loading -> {
                        viewTimer.isVisible = false
                        tvRateValue.isVisible = false
                        quoteLoadingIndicator.isVisible = true
                    }

                    is SwapInputContract.QuoteState.Loaded -> {
                        viewTimer.isVisible = true
                        tvRateValue.isVisible = true
                        quoteLoadingIndicator.isVisible = false

                        viewTimer.setProgress(SwapInputViewModel.QUOTE_TIMER, state.timer)
                        tvRateValue.text = RATE_FORMAT.format(
                            state.selectedPair.baseCurrency,
                            state.quoteState.buyRate.formatCryptoForUi(
                                state.selectedPair.termCurrency
                            )
                        )
                    }
                }
            }*/
        }

        /*with(binding) {
            cvSwap.setSendingNetworkFee(state.sendingNetworkFee)
            cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)
        }*/
    }

    private fun handleErrorState(state: SwapInputContract.State.Error) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun handleLoadingState(state: SwapInputContract.State.Loading) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun handleLoadedState(state: SwapInputContract.State.Loaded) {
        with(binding) {
            cvSwap.setSourceCurrency(state.selectedPair.baseCurrency)
            cvSwap.setSourceCurrencyTitle(
                getString(
                    R.string.Swap_Input_IHave, state.sourceCryptoBalance.formatCryptoForUi(
                        state.selectedPair.baseCurrency
                    )
                )
            )
            cvSwap.setDestinationCurrency(state.selectedPair.termCurrency)

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
        }
    }

    override fun handleEffect(effect: SwapInputContract.Effect) {
        when (effect) {
            SwapInputContract.Effect.Dismiss ->
                requireActivity().finish()

            is SwapInputContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(binding.root, effect.message)

            is SwapInputContract.Effect.ContinueToSwapProcessing ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionSwapProcessing(
                        coinFrom = effect.sourceCurrency,
                        coinTo = effect.destinationCurrency
                    )
                )

            /*

            SwapInputContract.Effect.DeselectMinMaxSwitchItems ->
                binding.switchMinMax.setSelectedItem(FabriikSwitch.OPTION_NONE)

            is SwapInputContract.Effect.OriginSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_ORIGIN_SELECTION,
                        currencies = effect.currencies.toTypedArray()
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

            is SwapInputContract.Effect.UpdateSourceFiatAmount ->
                binding.cvSwap.setSourceFiatAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateSourceCryptoAmount ->
                binding.cvSwap.setSourceCryptoAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateDestinationFiatAmount ->
                binding.cvSwap.setDestinationFiatAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateDestinationCryptoAmount ->
                binding.cvSwap.setDestinationCryptoAmount(effect.bigDecimal)*/
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_ORIGIN_SELECTION = "req_code_origin_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
    }
}