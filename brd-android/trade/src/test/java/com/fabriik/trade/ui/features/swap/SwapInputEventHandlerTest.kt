package com.fabriik.trade.ui.features.swap

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class SwapInputEventHandlerTest {

    @Mock lateinit var stateMock: SwapInputContract.State.Loaded

    @Spy val handler = object : SwapInputEventHandler {
        override fun onResume() {}
        override fun onDismissClicked() {}
        override fun onConfirmClicked() {}
        override fun onSourceCurrencyClicked() {}
        override fun onReplaceCurrenciesClicked() {}
        override fun onDestinationCurrencyClicked() {}
        override fun onUserAuthenticationSucceed() {}
        override fun onConfirmationDialogConfirmed() {}
        override fun onCheckAssetsDialogResult(result: String?) {}
        override fun onTempUnavailableDialogResult(result: String?) {}
        override fun onSourceCurrencyChanged(currencyCode: String) {}
        override fun onDestinationCurrencyChanged(currencyCode: String) {}
        override fun onSourceCurrencyFiatAmountChange(sourceFiatAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onSourceCurrencyCryptoAmountChange(sourceCryptoAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onDestinationCurrencyFiatAmountChange(destFiatAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onDestinationCurrencyCryptoAmountChange(destCryptoAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onCurrenciesReplaceAnimationCompleted(state: SwapInputContract.State.Loaded) {}
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(SwapInputContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(SwapInputContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_sourceCurrencyClicked_callOnSourceCurrencyClicked() {
        handler.handleEvent(SwapInputContract.Event.SourceCurrencyClicked)
        verify(handler).onSourceCurrencyClicked()
    }

    @Test
    fun handleEvent_replaceCurrenciesClicked_callOnReplaceCurrenciesClicked() {
        handler.handleEvent(SwapInputContract.Event.ReplaceCurrenciesClicked)
        verify(handler).onReplaceCurrenciesClicked()
    }

    @Test
    fun handleEvent_destinationCurrencyClicked_callOnDestinationCurrencyClicked() {
        handler.handleEvent(SwapInputContract.Event.DestinationCurrencyClicked)
        verify(handler).onDestinationCurrencyClicked()
    }

    @Test
    fun handleEvent_onConfirmationDialogConfirmed_callOnConfirmationDialogConfirmed() {
        handler.handleEvent(SwapInputContract.Event.OnConfirmationDialogConfirmed)
        verify(handler).onConfirmationDialogConfirmed()
    }

    @Test
    fun handleEvent_onUserAuthenticationSucceed_callOnUserAuthenticationSucceed() {
        handler.handleEvent(SwapInputContract.Event.OnUserAuthenticationSucceed)
        verify(handler).onUserAuthenticationSucceed()
    }

    @Test
    fun handleEvent_onResume_callOnResume() {
        handler.handleEvent(SwapInputContract.Event.OnResume)
        verify(handler).onResume()
    }

    @Test
    fun handleEvent_onCheckAssetsDialogResult_callOnCheckAssetsDialogResult() {
        val result = "test_result"
        handler.handleEvent(SwapInputContract.Event.OnCheckAssetsDialogResult(result))
        verify(handler).onCheckAssetsDialogResult(result)
    }

    @Test
    fun handleEvent_onTempUnavailableDialogResult_callOnTempUnavailableDialogResult() {
        val result = "test_result"
        handler.handleEvent(SwapInputContract.Event.OnTempUnavailableDialogResult(result))
        verify(handler).onTempUnavailableDialogResult(result)
    }

    @Test
    fun handleEvent_sourceCurrencyChanged_callOnSourceCurrencyChanged() {
        val currencyCode = "test_currency"
        handler.handleEvent(SwapInputContract.Event.SourceCurrencyChanged(currencyCode))
        verify(handler).onSourceCurrencyChanged(currencyCode)
    }

    @Test
    fun handleEvent_destinationCurrencyChanged_callOnDestinationCurrencyChanged() {
        val currencyCode = "test_currency"
        handler.handleEvent(SwapInputContract.Event.DestinationCurrencyChanged(currencyCode))
        verify(handler).onDestinationCurrencyChanged(currencyCode)
    }

    @Test
    fun handleEvent_sourceCurrencyFiatAmountChange_callOnSourceCurrencyFiatAmountChange() {
        val amount = BigDecimal.TEN
        handler.handleEvent(SwapInputContract.Event.SourceCurrencyFiatAmountChange(amount))
        verify(handler).onSourceCurrencyFiatAmountChange(amount, true)
    }

    @Test
    fun handleEvent_sourceCurrencyCryptoAmountChange_callOnSourceCurrencyCryptoAmountChange() {
        val amount = BigDecimal.TEN
        handler.handleEvent(SwapInputContract.Event.SourceCurrencyCryptoAmountChange(amount))
        verify(handler).onSourceCurrencyCryptoAmountChange(amount, true)
    }

    @Test
    fun handleEvent_destinationCurrencyFiatAmountChange_callOnDestinationCurrencyFiatAmountChange() {
        val amount = BigDecimal.TEN
        handler.handleEvent(SwapInputContract.Event.DestinationCurrencyFiatAmountChange(amount))
        verify(handler).onDestinationCurrencyFiatAmountChange(amount, true)
    }

    @Test
    fun handleEvent_destinationCurrencyCryptoAmountChange_callOnDestinationCurrencyCryptoAmountChange() {
        val amount = BigDecimal.TEN
        handler.handleEvent(SwapInputContract.Event.DestinationCurrencyCryptoAmountChange(amount))
        verify(handler).onDestinationCurrencyCryptoAmountChange(amount, true)
    }

    @Test
    fun handleEvent_onCurrenciesReplaceAnimationCompleted_callOnCurrenciesReplaceAnimationCompleted() {
        handler.handleEvent(SwapInputContract.Event.OnCurrenciesReplaceAnimationCompleted(stateMock))
        verify(handler).onCurrenciesReplaceAnimationCompleted(stateMock)
    }
}