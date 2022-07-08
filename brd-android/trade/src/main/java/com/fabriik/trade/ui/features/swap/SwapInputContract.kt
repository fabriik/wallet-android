package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object OnMinAmountClicked : Event()
        object OnMaxAmountClicked : Event()
        object SourceCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()

        data class SourceCurrencyChanged(val currencyCode: String) : Event()
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()

    /*data class OriginCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class OriginCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()*/
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class ContinueToSwapProcessing(
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()
        data class UpdateTimer(val timeLeft: Int) : Effect()
        data class SourceSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) : Effect()
        /*object DeselectMinMaxSwitchItems : Effect()
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
            val quoteResponse: QuoteResponse,
            val fiatCurrency: String,
            val sourceCryptoBalance: BigDecimal,
            val sourceCryptoCurrency: String,
            val destinationCryptoCurrency: String,
            val cryptoExchangeRate: BigDecimal,
            val cryptoExchangeRateLoading: Boolean = false
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