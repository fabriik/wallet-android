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
data class SwapTransactionData(

    @Json(name = "exchange_id")
    val exchangeId: String,

    @Json(name = "exchange_status")
    val exchangeStatus: ExchangeOrderStatus,

    @Json(name = "deposit_currency")
    val depositCurrency: String,

    @Json(name = "deposit_quantity")
    val depositQuantity: BigDecimal,

    @Json(name = "deposit_hash")
    val depositHash: String?,

    @Json(name = "withdrawal_currency")
    val withdrawalCurrency: String,

    @Json(name = "withdrawal_quantity")
    val withdrawalQuantity: BigDecimal,

    @Json(name = "withdrawal_hash")
    val withdrawalHash: String?,

    @Json(name = "timestamp")
    val timestamp: Long
): Parcelable {
    fun getDepositCurrencyUpperCase() = depositCurrency.toUpperCase(Locale.ROOT)

    fun getWithdrawalCurrencyUpperCase() = withdrawalCurrency.toUpperCase(Locale.ROOT)
}