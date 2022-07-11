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
import com.breadwallet.breadbox.formatCryptoForUi
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment

class SwapInputFragment : Fragment(),
    FabriikView<SwapInputContract.State, SwapInputContract.Effect> {

    private lateinit var binding: FragmentSwapInputBinding
    private val viewModel: SwapInputViewModel by viewModels()

    private val cardSwapCallback = object : SwapCardView.Callback {
        override fun onReplaceCurrenciesClicked() {
            viewModel.setEvent(SwapInputContract.Event.ReplaceCurrenciesClicked)
        }

        override fun onDestinationCurrencySelectorClicked() {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyClicked)
        }

        override fun onSourceCurrencySelectorClicked() {
            viewModel.setEvent(SwapInputContract.Event.SourceCurrencyClicked)
        }

        override fun onSellingCurrencyFiatAmountChanged(amount: BigDecimal) {
            //todo
        }

        override fun onSellingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            //todo
        }

        override fun onBuyingCurrencyFiatAmountChanged(amount: BigDecimal) {
            //todo
        }

        override fun onBuyingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            //todo
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

            viewTimer.setProgress(
                SwapInputViewModel.QUOTE_TIMER, SwapInputViewModel.QUOTE_TIMER
            )

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
        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_SOURCE_SELECTION, this) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(
                    SwapInputContract.Event.SourceCurrencyChanged(currency)
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
        when (state) {
            is SwapInputContract.State.Error ->
                handleErrorState(state)

            is SwapInputContract.State.Loading ->
                handleLoadingState(state)

            is SwapInputContract.State.Loaded ->
                handleLoadedState(state)
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

            is SwapInputContract.Effect.SourceSelection ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_SOURCE_SELECTION,
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
        }
    }

    private fun handleErrorState(state: SwapInputContract.State.Error) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
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
            cvSwap.setFiatCurrency(state.fiatCurrency)
            cvSwap.setSourceCurrency(state.sourceCryptoCurrency)
            cvSwap.setDestinationCurrency(state.destinationCryptoCurrency)
            cvSwap.setSourceCurrencyTitle(
                getString(
                    R.string.Swap_Input_IHave, state.sourceCryptoBalance.formatCryptoForUi(
                        state.sourceCryptoCurrency
                    )
                )
            )
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %f %s"
        const val REQUEST_KEY_SOURCE_SELECTION = "req_code_source_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
    }
}