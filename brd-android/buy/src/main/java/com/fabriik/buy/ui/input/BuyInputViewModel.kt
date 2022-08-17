package com.fabriik.buy.ui.input

import android.app.Application
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal
import com.fabriik.common.utils.getString
import java.math.RoundingMode

class BuyInputViewModel(
    application: Application
) : FabriikViewModel<BuyInputContract.State, BuyInputContract.Event, BuyInputContract.Effect>(
    application
), KodeinAware {

    init {
        loadInitialData()
    }

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private val currentLoadedState: BuyInputContract.State.Loaded?
        get() = state.value as BuyInputContract.State.Loaded?

    override fun createInitialState() = BuyInputContract.State.Loading

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
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                cryptoCurrency = currencyCode
                // todo: update exchange rate
            )
        }
    }

    private fun onPaymentMethodChanged(cardNumber: String) {
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
        val paymentMethods = currentLoadedState?.paymentInstruments ?: return

        setEffect {
            if (paymentMethods.isEmpty()) {
                BuyInputContract.Effect.AddCard
            } else {
                BuyInputContract.Effect.PaymentMethodSelection
            }
        }
    }

    private fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        val cryptoAmount = fiatAmount.divide(state.exchangeRate, 20, RoundingMode.HALF_UP)
        setState {
            state.copy(
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
        val state = currentLoadedState ?: return

        val fiatAmount = cryptoAmount * state.exchangeRate
        setState {
            state.copy(
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
        val state = currentLoadedState ?: return
        //todo
        setEffect { BuyInputContract.Effect.OpenOrderPreview(state.cryptoCurrency) }
    }

    private fun updateAmounts(fiatAmountChangedByUser: Boolean, cryptoAmountChangedByUser: Boolean) {
        val state = currentLoadedState ?: return

        setEffect { BuyInputContract.Effect.UpdateFiatAmount(state.fiatAmount, fiatAmountChangedByUser) }
        setEffect { BuyInputContract.Effect.UpdateCryptoAmount(state.cryptoAmount, cryptoAmountChangedByUser) }
    }

    private fun loadInitialData() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.getPaymentInstruments() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState {
                            BuyInputContract.State.Loaded(
                                exchangeRate = BigDecimal("21002.12"),
                                cryptoCurrency = "BTC",
                                paymentInstruments = it.data ?: emptyList()
                            )
                        }
                    Status.ERROR ->
                        showErrorState()
                }
            }
        )
    }
    private fun showErrorState() {
        setState { BuyInputContract.State.Error }
        setEffect {
            BuyInputContract.Effect.ShowToast(
                getString(R.string.Swap_Input_Error_Network)
            )
        }
    }
}