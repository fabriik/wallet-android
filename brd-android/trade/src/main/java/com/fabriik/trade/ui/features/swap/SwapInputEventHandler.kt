package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikEventHandler
import java.math.BigDecimal

interface SwapInputEventHandler: FabriikEventHandler<SwapInputContract.Event> {

    override fun handleEvent(event: SwapInputContract.Event) {
        return when (event) {
            is SwapInputContract.Event.OnResume -> onResume()
            is SwapInputContract.Event.ConfirmClicked -> onConfirmClicked()
            is SwapInputContract.Event.DismissClicked -> onDismissClicked()
            is SwapInputContract.Event.SourceCurrencyClicked -> onSourceCurrencyClicked()
            is SwapInputContract.Event.ReplaceCurrenciesClicked -> onReplaceCurrenciesClicked()
            is SwapInputContract.Event.DestinationCurrencyClicked -> onDestinationCurrencyClicked()
            is SwapInputContract.Event.OnUserAuthenticationSucceed -> onUserAuthenticationSucceed()
            is SwapInputContract.Event.OnConfirmationDialogConfirmed -> onConfirmationDialogConfirmed()
            is SwapInputContract.Event.OnCheckAssetsDialogResult -> onCheckAssetsDialogResult(event.result)
            is SwapInputContract.Event.OnTempUnavailableDialogResult -> onTempUnavailableDialogResult(event.result)
            is SwapInputContract.Event.SourceCurrencyChanged -> onSourceCurrencyChanged(event.currencyCode)
            is SwapInputContract.Event.DestinationCurrencyChanged -> onDestinationCurrencyChanged(event.currencyCode)
            is SwapInputContract.Event.SourceCurrencyFiatAmountChange -> onSourceCurrencyFiatAmountChange(event.amount, true)
            is SwapInputContract.Event.SourceCurrencyCryptoAmountChange -> onSourceCurrencyCryptoAmountChange(event.amount, true)
            is SwapInputContract.Event.DestinationCurrencyFiatAmountChange -> onDestinationCurrencyFiatAmountChange(event.amount, true)
            is SwapInputContract.Event.DestinationCurrencyCryptoAmountChange -> onDestinationCurrencyCryptoAmountChange(event.amount, true)
            is SwapInputContract.Event.OnCurrenciesReplaceAnimationCompleted -> onCurrenciesReplaceAnimationCompleted(event.stateChange)
        }
    }

    fun onResume()

    fun onDismissClicked()

    fun onConfirmClicked()

    fun onSourceCurrencyClicked()

    fun onReplaceCurrenciesClicked()

    fun onDestinationCurrencyClicked()

    fun onUserAuthenticationSucceed()

    fun onConfirmationDialogConfirmed()

    fun onCheckAssetsDialogResult(result: String?)

    fun onTempUnavailableDialogResult(result: String?)

    fun onSourceCurrencyChanged(currencyCode: String)

    fun onDestinationCurrencyChanged(currencyCode: String)

    fun onSourceCurrencyFiatAmountChange(sourceFiatAmount: BigDecimal, changeByUser: Boolean)

    fun onSourceCurrencyCryptoAmountChange(sourceCryptoAmount: BigDecimal, changeByUser: Boolean)

    fun onDestinationCurrencyFiatAmountChange(destFiatAmount: BigDecimal, changeByUser: Boolean)

    fun onDestinationCurrencyCryptoAmountChange(destCryptoAmount: BigDecimal, changeByUser: Boolean)

    fun onCurrenciesReplaceAnimationCompleted(state: SwapInputContract.State.Loaded)
}