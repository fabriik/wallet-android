package com.fabriik.buy.ui.input

import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikContract
import java.math.BigDecimal

interface BuyInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ContinueClicked : Event()
        object PaymentMethodClicked : Event()
        object CryptoCurrencyClicked : Event()

        data class FiatAmountChange(val amount: BigDecimal) : Event()
        data class CryptoAmountChange(val amount: BigDecimal) : Event()
        data class PaymentMethodChanged(val paymentInstrument: PaymentInstrument) : Event()
        data class CryptoCurrencyChanged(val currencyCode: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object AddCard : Effect()

        data class PaymentMethodSelection(val paymentInstruments: List<PaymentInstrument>) :
            Effect()

        data class PaymentMethodSelection(val paymentInstruments: List<PaymentInstrument>) : Effect()
        data class ShowToast(val message: String, val redInfo: Boolean = false) : Effect()
        data class CryptoSelection(val currencies: List<String>) : Effect()
        data class OpenOrderPreview(val cryptoCurrency: String) : Effect()
        data class UpdateFiatAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateCryptoAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val enabledWallets: List<String>,
            val paymentInstruments: List<PaymentInstrument>,
            val selectedPaymentMethod: PaymentInstrument? = null,
            val fiatCurrency: String = "USD",
            val cryptoCurrency: String,
            val exchangeRate: BigDecimal,
            val fiatAmount: BigDecimal = BigDecimal.ZERO,
            val cryptoAmount: BigDecimal = BigDecimal.ZERO,
            val rateLoadingVisible: Boolean = false,
            val continueButtonEnabled: Boolean = false,
            val fullScreenLoadingVisible: Boolean = false
        ) : State()
    }
}