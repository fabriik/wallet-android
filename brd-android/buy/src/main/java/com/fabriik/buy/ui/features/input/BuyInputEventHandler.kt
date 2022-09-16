package com.fabriik.buy.ui.features.input

import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikEventHandler
import java.math.BigDecimal

interface BuyInputEventHandler: FabriikEventHandler<BuyInputContract.Event> {

    override fun handleEvent(event: BuyInputContract.Event) {
        return when (event) {
            is BuyInputContract.Event.DismissClicked -> onDismissClicked()
            is BuyInputContract.Event.ContinueClicked -> onContinueClicked()
            is BuyInputContract.Event.QuoteTimeoutRetry -> onQuoteTimeoutRetry()
            is BuyInputContract.Event.PaymentMethodClicked -> onPaymentMethodClicked()
            is BuyInputContract.Event.CryptoCurrencyClicked -> onCryptoCurrencyClicked()
            is BuyInputContract.Event.FiatAmountChange -> onFiatAmountChanged(event.amount, true)
            is BuyInputContract.Event.CryptoAmountChange -> onCryptoAmountChanged(event.amount, true)
            is BuyInputContract.Event.PaymentMethodChanged -> onPaymentMethodChanged(event.paymentInstrument)
            is BuyInputContract.Event.CryptoCurrencyChanged -> onCryptoCurrencyChanged(event.currencyCode)
        }
    }

    fun onDismissClicked()

    fun onContinueClicked()

    fun onPaymentMethodClicked()

    fun onCryptoCurrencyClicked()

    fun onQuoteTimeoutRetry()

    fun onPaymentMethodChanged(paymentInstrument: PaymentInstrument)

    fun onCryptoCurrencyChanged(currencyCode: String)

    fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean)

    fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean)
}