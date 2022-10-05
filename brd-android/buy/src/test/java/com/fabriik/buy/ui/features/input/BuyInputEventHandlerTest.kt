package com.fabriik.buy.ui.features.input
import com.fabriik.buy.ui.features.paymentmethod.PaymentMethodFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class BuyInputEventHandlerTest {

    @Mock lateinit var paymentMethodResult: PaymentMethodFragment.Result

    @Spy val handler = object : BuyInputEventHandler {
        override fun onDismissClicked() {}
        override fun onContinueClicked() {}
        override fun onPaymentMethodClicked() {}
        override fun onCryptoCurrencyClicked() {}
        override fun onQuoteTimeoutRetry() {}
        override fun onPaymentMethodResultReceived(result: PaymentMethodFragment.Result) {}
        override fun onCryptoCurrencyChanged(currencyCode: String) {}
        override fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {}
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(BuyInputContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_continueClicked_callOnContinueClicked() {
        handler.handleEvent(BuyInputContract.Event.ContinueClicked)
        verify(handler).onContinueClicked()
    }

    @Test
    fun handleEvent_paymentMethodClicked_callOnPaymentMethodClicked() {
        handler.handleEvent(BuyInputContract.Event.PaymentMethodClicked)
        verify(handler).onPaymentMethodClicked()
    }

    @Test
    fun handleEvent_cryptoCurrencyClicked_callOnCryptoCurrencyClicked() {
        handler.handleEvent(BuyInputContract.Event.CryptoCurrencyClicked)
        verify(handler).onCryptoCurrencyClicked()
    }

    @Test
    fun handleEvent_quoteTimeoutRetry_callOnQuoteTimeoutRetry() {
        handler.handleEvent(BuyInputContract.Event.QuoteTimeoutRetry)
        verify(handler).onQuoteTimeoutRetry()
    }

    @Test
    fun handleEvent_paymentMethodChanged_callOnPaymentMethodChanged() {
        handler.handleEvent(BuyInputContract.Event.PaymentMethodResultReceived(paymentMethodResult))
        verify(handler).onPaymentMethodResultReceived(paymentMethodResult)
    }

    @Test
    fun handleEvent_cryptoCurrencyChanged_callOnCryptoCurrencyChanged() {
        val currencyCode = "test_currency"
        handler.handleEvent(BuyInputContract.Event.CryptoCurrencyChanged(currencyCode))
        verify(handler).onCryptoCurrencyChanged(currencyCode)
    }

    @Test
    fun handleEvent_fiatAmountChange_callOnFiatAmountChanged() {
        val amount = BigDecimal.TEN
        handler.handleEvent(BuyInputContract.Event.FiatAmountChange(amount))
        verify(handler).onFiatAmountChanged(amount, true)
    }

    @Test
    fun handleEvent_cryptoAmountChange_callOnCryptoAmountChanged() {
        val amount = BigDecimal.TEN
        handler.handleEvent(BuyInputContract.Event.CryptoAmountChange(amount))
        verify(handler).onCryptoAmountChanged(amount, true)
    }
}