package com.fabriik.buy.ui.features.buydetails

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.response.ExchangeOrder
import java.math.BigDecimal
import java.math.RoundingMode

interface BuyDetailsContract {

    sealed class Event : FabriikContract.Event {
        object LoadData : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object TransactionIdClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class CopyToClipboard(val data: String) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Loading : State()
        object Error : State()
        data class Loaded(
            val data: ExchangeOrder
        ) : State() {
            val cardFeePercent: Float
                get() = data.buyCardFeesPercent ?: 0f

            val cardFee: BigDecimal //todo: read from API
                get() = (data.destination.usdAmount ?: BigDecimal.ZERO) * (cardFeePercent / 100).toBigDecimal()

            val networkFee: BigDecimal //todo: read from API
                get() = (data.source.usdAmount ?: BigDecimal.ZERO) - cardFee - (data.destination.usdAmount ?: BigDecimal.ZERO)

            val fiatPriceForOneCryptoUnit: BigDecimal //todo: read from API
                get() = (BigDecimal.ONE.divide(data.destination.currencyAmount, 20, RoundingMode.HALF_UP)) * (data.destination.usdAmount ?: BigDecimal.ZERO)
        }
    }
}