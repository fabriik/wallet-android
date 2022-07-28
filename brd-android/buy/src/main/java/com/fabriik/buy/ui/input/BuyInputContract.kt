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
        data class UpdateFiatAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateCryptoAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
    }

    data class State(
        val continueButtonEnabled: Boolean = false
    ) : FabriikContract.State
}