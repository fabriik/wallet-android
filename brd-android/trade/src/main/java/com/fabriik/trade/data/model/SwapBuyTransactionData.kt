package com.fabriik.trade.data.model

import android.os.Parcelable
import com.fabriik.trade.data.response.ExchangeOrderStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class SwapBuyTransactionData(

    @Json(name = "order_id")
    val exchangeId: String,

    @Json(name = "status")
    val exchangeStatus: ExchangeOrderStatus,

    @Json(name = "status_details")
    val exchangeStatusDetails: String,

    @Json(name = "source")
    val source: Data,

    @Json(name = "destination")
    val destination: Data,

    @Json(name = "timestamp")
    val timestamp: Long
): Parcelable {

    fun isBuyTransaction() = "usd".equals(source.currency, true)

    fun isSwapTransaction() = !isBuyTransaction()

    fun getDepositCurrencyUpperCase() = source.currency.toUpperCase(Locale.ROOT)

    fun getWithdrawalCurrencyUpperCase() = destination.currency.toUpperCase(Locale.ROOT)
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Data(

    @Json(name = "currency")
    val currency: String,

    @Json(name = "currency_amount")
    val currencyAmount: BigDecimal,

    @Json(name = "transaction_id")
    val transactionId: String?
): Parcelable