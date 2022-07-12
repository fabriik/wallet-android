package com.fabriik.trade.ui.features.swap

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapInputBinding
import kotlinx.coroutines.flow.collect
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.breadwallet.breadbox.formatCryptoForUi
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.customview.SwapCardView
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment
import java.math.BigDecimal

class SwapInputFragment : Fragment(),
    FabriikView<SwapInputContract.State, SwapInputContract.Effect> {

    private lateinit var binding: FragmentSwapInputBinding
    private val viewModel: SwapInputViewModel by viewModels()

    private val cardSwapCallback = object : SwapCardView.Callback {
        override fun onReplaceCurrenciesClicked() {
            viewModel.setEvent(SwapInputContract.Event.ReplaceCurrenciesClicked)
        }

        override fun onSourceCurrencyClicked() {
            viewModel.setEvent(SwapInputContract.Event.SourceCurrencyClicked)
        }

        override fun onDestinationCurrencyClicked() {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyClicked)
        }

        override fun onSellingCurrencyFiatAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.SourceCurrencyFiatAmountChange(amount))
        }

        override fun onSellingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.SourceCurrencyCryptoAmountChange(amount))
        }

        override fun onBuyingCurrencyFiatAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyFiatAmountChange(amount))
        }

        override fun onBuyingCurrencyCryptoAmountChanged(amount: BigDecimal) {
            viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyCryptoAmountChange(amount))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

            cvSwap.setCallback(cardSwapCallback)

            switchMinMax.setCallback {
                when (it) {
                    FabriikSwitch.OPTION_LEFT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMinAmountClicked)
                    FabriikSwitch.OPTION_RIGHT ->
                        viewModel.setEvent(SwapInputContract.Event.OnMaxAmountClicked)
                }
            }

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

            SwapInputContract.Effect.ConfirmDialog ->
                showConfirmDialog()

            SwapInputContract.Effect.DeselectMinMaxSwitchItems ->
                binding.switchMinMax.setSelectedItem(FabriikSwitch.OPTION_NONE)

            is SwapInputContract.Effect.CurrenciesReplaceAnimation ->
                startCurrenciesReplaceAnimation(effect.stateChange)

            is SwapInputContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(binding.root, effect.message)

            is SwapInputContract.Effect.ContinueToSwapProcessing ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionSwapProcessing(
                        coinFrom = effect.sourceCurrency,
                        coinTo = effect.destinationCurrency
                    )
                )

            is SwapInputContract.Effect.UpdateTimer ->
                binding.viewTimer.setProgress(
                    SwapInputViewModel.QUOTE_TIMER, effect.timeLeft
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

            is SwapInputContract.Effect.UpdateSourceFiatAmount ->
                binding.cvSwap.setSourceFiatAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateSourceCryptoAmount ->
                binding.cvSwap.setSourceCryptoAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateDestinationFiatAmount ->
                binding.cvSwap.setDestinationFiatAmount(effect.bigDecimal)

            is SwapInputContract.Effect.UpdateDestinationCryptoAmount ->
                binding.cvSwap.setDestinationCryptoAmount(effect.bigDecimal)
        }
    }

    private fun startCurrenciesReplaceAnimation(stateChange: SwapInputContract.State.Loaded) {
        with(binding.cvSwap) {
            val sourceSelectionView = this.getSourceSelectionView()
            val sourceSelectionViewPosition = IntArray(2)
            sourceSelectionView.getLocationOnScreen(sourceSelectionViewPosition)

            val destinationSelectionView = this.getDestinationSelectionView()
            val destinationSelectionViewPosition = IntArray(2)
            destinationSelectionView.getLocationOnScreen(destinationSelectionViewPosition)

            val diffY = (destinationSelectionViewPosition[1] - sourceSelectionViewPosition[1]).toFloat()

            sourceSelectionView.startAnimation(
                TranslateAnimation(0f, 0f, 0f, diffY).apply {
                    duration = 20000
                }
            )

            destinationSelectionView.startAnimation(
                TranslateAnimation(0f, 0f, 0f, -diffY).apply {
                    duration = 20000
                }
            )

            sourceSelectionView.postDelayed(20000) {
                viewModel.setEvent(
                    SwapInputContract.Event.OnCurrenciesReplaceAnimationCompleted(
                        stateChange
                    )
                )
            }
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

            tvRateValue.text = RATE_FORMAT.format(
                state.sourceCryptoCurrency,
                state.cryptoExchangeRate.formatCryptoForUi(
                    state.destinationCryptoCurrency
                )
            )

            cvSwap.setSendingNetworkFee(state.sendingNetworkFee)
            cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)

            btnConfirm.isEnabled = state.confirmButtonEnabled

            viewTimer.isVisible = !state.cryptoExchangeRateLoading
            tvRateValue.isVisible = !state.cryptoExchangeRateLoading
            quoteLoadingIndicator.isVisible = state.cryptoExchangeRateLoading

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun showConfirmDialog() {
        //todo
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_SOURCE_SELECTION = "req_code_source_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
    }
}