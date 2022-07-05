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
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.features.assetselection.AssetSelectionAdapter
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

            switchMinMax.setCallback {
                when (it) {
                    FabriikSwitch.OPTION_LEFT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMinAmountClicked)
                    FabriikSwitch.OPTION_RIGHT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMaxAmountClicked)
                }
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
            REQUEST_KEY_ORIGIN_SELECTION,
            this
        ) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(
                    SwapInputContract.Event.OriginCurrencyChanged(currency)
                )
            }
        }

        // listen for destination currency changes
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_DESTINATION_SELECTION,
            this
        ) { _, bundle ->
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
        when (state) {
            is SwapInputContract.State.Empty -> with(binding) {
                content.isVisible = false
                initialLoadingIndicator.isVisible = true
            }

            is SwapInputContract.State.Loaded -> with(binding) {
                content.isVisible = true
                initialLoadingIndicator.isVisible = false

                cvSwap.setOriginCurrency(state.selectedPair.baseCurrency)
                cvSwap.setDestinationCurrency(state.selectedPair.termCurrency)
            }
        }
        /*with(binding) {
            state.selectedTradingPair.let {
                cvSwap.setOriginCurrency(it?.baseCurrency)
                cvSwap.setDestinationCurrency(it?.termCurrency)
            }

            /*cvSwap.setSellingCurrencyTitle(getString(R.string.Swap_Input_IHave, state.originCurrencyBalance, state.originCurrency))
            cvSwap.setOriginCurrency(state.originCurrency)
            cvSwap.setSendingNetworkFee(state.sendingNetworkFee)
            cvSwap.setDestinationCurrency(state.destinationCurrency)
            cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)

            viewTimer.setProgress(SwapInputViewModel.QUOTE_TIMER, state.timer)
            tvRateValue.text = RATE_FORMAT.format(
                state.originCurrency, state.rateOriginToDestinationCurrency, state.destinationCurrency
            )
*/
            viewTimer.isVisible = !state.quoteLoadingVisible
            tvRateValue.isVisible = !state.quoteLoadingVisible
            quoteLoadingIndicator.isVisible = state.quoteLoadingVisible

            content.isVisible = !state.initialLoadingVisible
            initialLoadingIndicator.isVisible = state.initialLoadingVisible
        }*/
    }

    override fun handleEffect(effect: SwapInputContract.Effect) {
        when (effect) {
            SwapInputContract.Effect.Dismiss ->
                requireActivity().finish()

            is SwapInputContract.Effect.OriginSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        REQUEST_KEY_ORIGIN_SELECTION,
                        effect.currencies.toTypedArray()
                    )
                )

            is SwapInputContract.Effect.DestinationSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        REQUEST_KEY_DESTINATION_SELECTION,
                        effect.currencies.toTypedArray()
                    )
                )

            is SwapInputContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    binding.root, effect.message
                )
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %f %s"
        const val REQUEST_KEY_ORIGIN_SELECTION = "req_code_origin_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
    }
}