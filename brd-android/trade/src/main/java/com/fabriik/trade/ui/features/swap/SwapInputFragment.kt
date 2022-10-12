package com.fabriik.trade.ui.features.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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
import com.breadwallet.tools.util.Utils.hideKeyboard
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.ui.dialog.FabriikGenericDialog
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.ui.customview.SwapCardView
import com.fabriik.trade.ui.customview.SwapCardView.Companion.SCALE_CRYPTO
import com.fabriik.trade.ui.dialog.SwapConfirmationDialog
import com.fabriik.trade.ui.features.assetselection.AssetSelectionFragment
import com.fabriik.trade.ui.features.authentication.SwapAuthenticationViewModel
import com.fabriik.trade.utils.EstimateSendingFee
import java.math.BigDecimal
import java.util.*

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
                cvSwap.hideKeyboard()
                viewModel.setEvent(SwapInputContract.Event.DismissClicked)
            }

            viewTimer.setProgress(
                SwapInputViewModel.QUOTE_TIMER, SwapInputViewModel.QUOTE_TIMER
            )

            cvSwap.setCallback(cardSwapCallback)

            btnConfirm.setOnClickListener {
                hideKeyboard(binding.root.context)
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

        // listen for confirmation dialog result
        parentFragmentManager.setFragmentResultListener(REQUEST_KEY_SWAP_CONFIRMATION_DIALOG, this) { _, bundle ->
            val resultKey = bundle.getString(SwapConfirmationDialog.EXTRA_RESULT)
            if (resultKey == SwapConfirmationDialog.RESULT_CONFIRM) {
                binding.root.post {
                    viewModel.setEvent(SwapInputContract.Event.OnConfirmationDialogConfirmed)
                }
            }
        }

        // listen for user authentication result
        parentFragmentManager.setFragmentResultListener(SwapAuthenticationViewModel.REQUEST_KEY, this) { _, bundle ->
            val resultKey = bundle.getString(SwapAuthenticationViewModel.EXTRA_RESULT)
            if (resultKey == SwapAuthenticationViewModel.RESULT_KEY_SUCCESS) {
                binding.root.post {
                    viewModel.setEvent(SwapInputContract.Event.OnUserAuthenticationSucceed)
                }
            }
        }

        // listen for check assets dialog result
        requireActivity().supportFragmentManager.setFragmentResultListener(SwapInputViewModel.DIALOG_CHECK_ASSETS_ARGS.requestKey, this) { _, bundle ->
            val resultKey = bundle.getString(FabriikGenericDialog.EXTRA_RESULT)
            binding.root.post { viewModel.setEvent(SwapInputContract.Event.OnCheckAssetsDialogResult(resultKey)) }
        }

        // listen for temp unavailable dialog result
        requireActivity().supportFragmentManager.setFragmentResultListener(SwapInputViewModel.DIALOG_TEMP_UNAVAILABLE_ARGS.requestKey, this) { _, bundle ->
            val resultKey = bundle.getString(FabriikGenericDialog.EXTRA_RESULT)
            binding.root.post { viewModel.setEvent(SwapInputContract.Event.OnTempUnavailableDialogResult(resultKey)) }
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

            SwapInputContract.Effect.ClearInputFocus ->
                binding.cvSwap.clearCurrentInputFieldFocus()

            SwapInputContract.Effect.RequestUserAuthentication ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionSwapAuthentication()
                )

            SwapInputContract.Effect.TransactionFailedScreen ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionSwapFailed()
                )

            is SwapInputContract.Effect.ConfirmDialog ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionConfirmationDialog(
                        requestKey = REQUEST_KEY_SWAP_CONFIRMATION_DIALOG,
                        toAmount = effect.to,
                        fromAmount = effect.from,
                        rateAmount = effect.rate,
                        sendingFeeAmount = effect.sendingFee,
                        receivingFeeAmount = effect.receivingFee
                    )
                )

            is SwapInputContract.Effect.CurrenciesReplaceAnimation ->
                startCurrenciesReplaceAnimation(effect.stateChange)

            is SwapInputContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(binding.root, effect.message)

            is SwapInputContract.Effect.ShowError ->
                FabriikToastUtil.showError(binding.root, effect.message)

            is SwapInputContract.Effect.ShowErrorMessage ->
                FabriikToastUtil.showError(binding.root, effect.error.toString(binding.root.context))

            is SwapInputContract.Effect.ShowDialog ->
                FabriikGenericDialog.newInstance(effect.args)
                    .show(parentFragmentManager)

            is SwapInputContract.Effect.ContinueToSwapProcessing ->
                findNavController().navigate(
                    SwapInputFragmentDirections.actionSwapProcessing(
                        coinFrom = effect.sourceCurrency,
                        coinTo = effect.destinationCurrency,
                        exchangeId = effect.exchangeId
                    )
                )

            is SwapInputContract.Effect.UpdateTimer ->
                binding.viewTimer.setProgress(
                    SwapInputViewModel.QUOTE_TIMER, effect.timeLeft
                )

            is SwapInputContract.Effect.SourceSelection -> {
                binding.cvSwap.hideKeyboard()
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_SOURCE_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                    )
                )
            }

            is SwapInputContract.Effect.DestinationSelection -> {
                binding.cvSwap.hideKeyboard()
                findNavController().navigate(
                    SwapInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_DESTINATION_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                    )
                )
            }

            is SwapInputContract.Effect.UpdateSourceFiatAmount ->
                binding.cvSwap.setSourceFiatAmount(effect.bigDecimal, effect.changeByUser)

            is SwapInputContract.Effect.UpdateSourceCryptoAmount ->
                binding.cvSwap.setSourceCryptoAmount(effect.bigDecimal, effect.changeByUser)

            is SwapInputContract.Effect.UpdateDestinationFiatAmount ->
                binding.cvSwap.setDestinationFiatAmount(effect.bigDecimal, effect.changeByUser)

            is SwapInputContract.Effect.UpdateDestinationCryptoAmount ->
                binding.cvSwap.setDestinationCryptoAmount(effect.bigDecimal, effect.changeByUser)
        }
    }

    override fun onResume() {
        super.onResume()
        when (viewModel.state.value) {
            is SwapInputContract.State.Loaded ->
                viewModel.setEvent(SwapInputContract.Event.OnResume)
            else -> Unit
        }
    }

    private fun startCurrenciesReplaceAnimation(stateChange: SwapInputContract.State.Loaded) {
        binding.cvSwap.startReplaceAnimation(
            replaceAnimationStarted = {
                binding.cvSwap.setReplaceButtonEnabled(false)
                binding.cvSwap.setSourceCurrency(stateChange.sourceCryptoCurrency.uppercase())
                binding.cvSwap.setDestinationCurrency(stateChange.destinationCryptoCurrency.uppercase())
            },
            replaceAnimationCompleted = {
                binding.cvSwap.setReplaceButtonEnabled(true)
                viewModel.setEvent(
                    SwapInputContract.Event.OnCurrenciesReplaceAnimationCompleted(
                        stateChange
                    )
                )
            },
        )
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
            cvSwap.setSourceCurrency(state.sourceCryptoCurrency.uppercase())
            cvSwap.setDestinationCurrency(state.destinationCryptoCurrency.uppercase())
            cvSwap.setSourceCurrencyTitle(
                getString(
                    R.string.Swap_Balance, state.sourceCryptoBalance.formatCryptoForUi(
                        currencyCode = null,
                        scale = SCALE_CRYPTO
                    ),
                    state.sourceCryptoCurrency.uppercase()
                )
            )

            tvRateValue.text = RATE_FORMAT.format(
                state.sourceCryptoCurrency,
                state.rate.formatCryptoForUi(
                    currencyCode = state.destinationCryptoCurrency,
                    scale = SCALE_CRYPTO
                )
            )

            cvSwap.setInputFieldsEnabled(state.quoteResponse != null)
            cvSwap.setSendingNetworkFee(
                when (state.sendingNetworkFee) {
                    is EstimateSendingFee.Result.Estimated -> state.sendingNetworkFee.data
                    else -> null
                }
            )
            cvSwap.setReceivingNetworkFee(state.receivingNetworkFee)

            btnConfirm.isEnabled = state.confirmButtonEnabled

            viewTimer.isVisible = !state.cryptoExchangeRateLoading && state.quoteResponse != null
            tvRateValue.isVisible = !state.cryptoExchangeRateLoading && state.quoteResponse != null
            quoteLoadingIndicator.isVisible = state.cryptoExchangeRateLoading

            content.isVisible = true
            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingVisible
            initialLoadingIndicator.isVisible = false

            val minAmount = state.quoteResponse?.minimumValueUsd ?: BigDecimal.ZERO
            val minText = minAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = false,
                showCurrencySymbol = false
            )

            val maxAmount = state.profile?.exchangeLimits?.swapAllowanceDaily ?: BigDecimal.ZERO
            val maxText = maxAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = false,
                showCurrencySymbol = false
            )

            tvKycMessage.text = getString(R.string.Swap_SwapLimits, minText, maxText)
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_SOURCE_SELECTION = "req_code_source_select"
        const val REQUEST_KEY_DESTINATION_SELECTION = "req_code_dest_select"
        const val REQUEST_KEY_SWAP_CONFIRMATION_DIALOG = "req_code_swap_confirmation"
    }
}
