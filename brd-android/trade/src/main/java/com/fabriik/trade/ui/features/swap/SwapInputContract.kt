package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.model.SupportedTradingPair
import com.fabriik.trade.ui.features.assetselection.AssetSelectionAdapter
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object OriginCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()
        object OnMinAmountClicked : Event()
        object OnMaxAmountClicked : Event()
        data class OriginCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class OriginCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class OriginCurrencyChanged(val currencyCode: String) : Event()
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class OriginSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>) : Effect()
    }

    data class State(
        val initialLoadingVisible: Boolean = false,
        val tradingPairs: List<SupportedTradingPair> = emptyList(),
        val selectedTradingPair: SupportedTradingPair? = null,
        val baseCurrencyCryptoBalance: BigDecimal = BigDecimal.ZERO,
        val termCurrencyCryptoBalance: BigDecimal = BigDecimal.ZERO,
        /*val timer: Int,val quoteLoading: Boolean = false,
        val originCurrency: String,
        val originCurrencyBalance: BigDecimal,
        val originFiatAmount: BigDecimal = BigDecimal.ZERO,
        val originCryptoAmount: BigDecimal = BigDecimal.ZERO,
        val destinationCurrency: String,
        val destinationFiatAmount: BigDecimal = BigDecimal.ZERO,
        val destinationCryptoAmount: BigDecimal = BigDecimal.ZERO,
        val sendingNetworkFee: String? = null,
        val receivingNetworkFee: String? = null,
        val rateOriginToDestinationCurrency: BigDecimal*/
    ) : FabriikContract.State
}