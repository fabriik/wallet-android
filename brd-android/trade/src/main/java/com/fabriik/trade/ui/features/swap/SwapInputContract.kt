package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object OriginCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()
        data class OriginCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class OriginCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class OriginCurrencyChanged(val currencyCode: String) : Event()
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object ConfirmDialog : Effect()
        data class OriginSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) : Effect()
    }

    data class State(
        val timer: Int,
        val quoteLoading: Boolean = false,
        val currencies: List<String>,
        val originCurrency: Currency,
        val destinationCurrency: Currency,
        val sendingNetworkFee: Currency? = null,
        val receivingNetworkFee: Currency? = null,
        val originCurrencyBalance: BigDecimal,
        val rateOriginToDestinationCurrency: BigDecimal
    ) : FabriikContract.State
}

data class Currency(
    val title: String,
    val amount: BigDecimal = BigDecimal.ZERO,
    val fiatValue: BigDecimal = BigDecimal.ZERO
)