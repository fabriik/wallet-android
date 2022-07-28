package com.fabriik.buy.ui.input

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import java.math.BigDecimal
import java.math.RoundingMode

class BuyInputViewModel(
    application: Application
) : FabriikViewModel<BuyInputContract.State, BuyInputContract.Event, BuyInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    override fun createInitialState() = BuyInputContract.State(
        cryptoCurrency = "BTC", //todo: set first enabled wallet
        exchangeRate = BigDecimal("0.121") //todo: get from API
    )

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
        setState {
            copy(
                cryptoCurrency = currencyCode,
                // todo: update exchange rate
            )
        }
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
        setEffect {
            BuyInputContract.Effect.CryptoSelection(
                listOf("BTC", "BSV", "ETH") //todo: get list of enabled wallets
            )
        }
    }

    private fun onPaymentMethodClicked() {
        // todo
    }

    private fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {
        val cryptoAmount = fiatAmount * currentState.exchangeRate
        setState {
            copy(
                fiatAmount = fiatAmount,
                cryptoAmount = cryptoAmount,
            )
        }

        updateAmounts(
            fiatAmountChangedByUser = changeByUser,
            cryptoAmountChangedByUser = false,
        )
    }

    private fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {
        val fiatAmount = cryptoAmount.divide(currentState.exchangeRate, 5, RoundingMode.HALF_UP)
        setState {
            copy(
                fiatAmount = fiatAmount,
                cryptoAmount = cryptoAmount,
            )
        }

        updateAmounts(
            fiatAmountChangedByUser = false,
            cryptoAmountChangedByUser = changeByUser,
        )
    }

    private fun onContinueClicked() {
        //todo
    }

    private fun updateAmounts(fiatAmountChangedByUser: Boolean, cryptoAmountChangedByUser: Boolean) {
        setEffect { BuyInputContract.Effect.UpdateFiatAmount(currentState.fiatAmount, fiatAmountChangedByUser) }
        setEffect { BuyInputContract.Effect.UpdateCryptoAmount(currentState.cryptoAmount, cryptoAmountChangedByUser) }

        if (fiatAmountChangedByUser || cryptoAmountChangedByUser) {
            setEffect { BuyInputContract.Effect.DeselectMinMaxSwitchItems }
        }
    }
}