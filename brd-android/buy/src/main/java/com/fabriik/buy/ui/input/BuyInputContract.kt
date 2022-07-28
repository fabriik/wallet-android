package com.fabriik.buy.ui.input

import com.fabriik.common.ui.base.FabriikContract
import java.math.BigDecimal

interface BuyInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ContinueClicked : Event()
        object MinAmountClicked : Event()
        object MaxAmountClicked : Event()
        object PaymentMethodClicked : Event()
        object CryptoCurrencyClicked : Event()

        data class FiatAmountChange(val amount: BigDecimal) : Event()
        data class CryptoAmountChange(val amount: BigDecimal) : Event()
        data class PaymentMethodChanged(val cardNumber: String) : Event() //todo: replace cardNumber with some object
        data class CryptoCurrencyChanged(val currencyCode: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object PaymentMethodSelection : Effect()
        object DeselectMinMaxSwitchItems : Effect()

        data class ShowToast(val message: String, val redInfo: Boolean = false) : Effect()
        data class CryptoSelection(val currencies: List<String>) : Effect()
        data class OpenOrderPreview(val cryptoCurrency: String) : Effect()
        data class UpdateFiatAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateCryptoAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
    }

    data class State(
        val fiatAmount: BigDecimal = BigDecimal.ZERO,
        val cryptoAmount: BigDecimal = BigDecimal.ZERO,
        val exchangeRate: BigDecimal,
        val fiatCurrency: String = "USD",
        val cryptoCurrency: String,
        val continueButtonEnabled: Boolean = false,
        val rateLoadingVisible: Boolean = false,
        val initialLoadingVisible: Boolean = false,
        val fullScreenLoadingVisible: Boolean = false
    ) : FabriikContract.State
}