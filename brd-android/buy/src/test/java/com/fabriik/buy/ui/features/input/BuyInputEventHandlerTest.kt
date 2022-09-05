package com.fabriik.buy.ui.features.input
import com.fabriik.common.data.model.PaymentInstrument
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class BuyInputEventHandlerTest {

    @Spy val handler = object : BuyInputEventHandler {
        override fun onDismissClicked() {}
        override fun onContinueClicked() {}
        override fun onPaymentMethodClicked() {}
        override fun onCryptoCurrencyClicked() {}
        override fun onQuoteTimeoutRetry() {}
        override fun onPaymentMethodChanged(paymentInstrument: PaymentInstrument) {}
        override fun onCryptoCurrencyChanged(currencyCode: String) {}
        override fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {}
        override fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {}
    }
}