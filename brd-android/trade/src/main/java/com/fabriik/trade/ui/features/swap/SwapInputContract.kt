package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.trade.data.model.TradingPair
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()

        /*
        object ConfirmClicked : Event()
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
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()*/
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class ContinueToSwapProcessing(
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()
        /*object DeselectMinMaxSwitchItems : Effect()
        data class OriginSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) : Effect()
        data class UpdateSourceFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateSourceCryptoAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationCryptoAmount(val bigDecimal: BigDecimal) : Effect()*/
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val tradingPairs: List<TradingPair>,
            val selectedPair: TradingPair,
            val sourceCryptoBalance: BigDecimal
        ) : State()

        /*data class Loaded(
            val timer: Int = 0,
            val tradingPairs: List<TradingPair>,
            val selectedPair: TradingPair,
            //todo: fee loading indicators, disable button
            val sendingNetworkFee: NetworkFeeData? = null,
            val receivingNetworkFee: NetworkFeeData? = null,
            val quoteState: QuoteState = QuoteState.Loading,
            val sourceFiatAmount: BigDecimal = BigDecimal.ZERO,
            val sourceCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val sourceCurrencyBalance: BigDecimal = BigDecimal.ZERO,
            val destinationFiatAmount: BigDecimal = BigDecimal.ZERO,
            val destinationCryptoAmount: BigDecimal = BigDecimal.ZERO,
            @FabriikSwitch.Companion.SwitchOption val selectedMinMaxOption: Int = FabriikSwitch.OPTION_NONE,
        ) : State()*/
    }

    /*sealed class QuoteState {
        object Loading : QuoteState()
        data class Loaded(
            val timerTimestamp: Long,
            val sellRate: BigDecimal,
            val buyRate: BigDecimal
        ) : QuoteState()
    }

    data class NetworkFeeData(
        val fiatAmount: BigDecimal,
        val fiatCurrency: String,
        val cryptoAmount: BigDecimal,
        val cryptoCurrency: String
    )*/
}