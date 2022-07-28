package com.fabriik.buy.ui.input

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.trade.ui.features.swap.AmountConverter
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance
import java.math.BigDecimal

class BuyInputViewModel(
    application: Application
) : FabriikViewModel<BuyInputContract.State, BuyInputContract.Event, BuyInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val amountConverter = AmountConverter(
        direct.instance(), direct.instance(), currentState.fiatCurrency
    )

    override fun createInitialState() = BuyInputContract.State()

    override fun handleEvent(event: BuyInputContract.Event) {
        when (event) {
            BuyInputContract.Event.DismissClicked ->
                setEffect { BuyInputContract.Effect.Dismiss }

            BuyInputContract.Event.ContinueClicked ->
                onContinueClicked()

            BuyInputContract.Event.CryptoCurrencyClicked ->
                onCryptoCurrencyClicked()

            BuyInputContract.Event.PaymentMethodClicked ->
                onPaymentMethodClicked()

            is BuyInputContract.Event.MinAmountClicked ->
                onMinAmountClicked()

            is BuyInputContract.Event.MaxAmountClicked ->
                onMaxAmountClicked()

            is BuyInputContract.Event.CryptoCurrencyChanged ->
                onCryptoCurrencyChanged(event.currencyCode)

            is BuyInputContract.Event.PaymentMethodChanged ->
                onPaymentMethodChanged(event.cardNumber)

            is BuyInputContract.Event.FiatAmountChange ->
                onFiatAmountChanged(event.amount, true)

            is BuyInputContract.Event.CryptoAmountChange ->
                onCryptoAmountChanged(event.amount, true)
        }
    }

    private fun onCryptoCurrencyChanged(currencyCode: String) {
        //todo
    }

    private fun onPaymentMethodChanged(cardNumber: String) {
        //todo
    }

    private fun onMinAmountClicked() {
        //todo
    }

    private fun onMaxAmountClicked() {
        //todo
    }

    private fun onCryptoCurrencyClicked() {
        // todo
    }

    private fun onPaymentMethodClicked() {
        // todo
    }

    private fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {
        // todo
    }

    private fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {
        // todo
    }

    private fun onContinueClicked() {
        //todo
    }
}