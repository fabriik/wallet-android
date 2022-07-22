package com.fabriik.trade.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ExchangeOrder(
    @Json(name = "order_id")
    val orderId: String,

    @Json(name = "status")
    val status: ExchangeOrderStatus,

    @Json(name = "source")
    val source: ExchangeSource,

    @Json(name = "destination")
    val destination: ExchangeSource,

    @Json(name = "timestamp")
    val timestamp: Long,
)

@JsonClass(generateAdapter = true)
data class ExchangeSource(
    @Json(name = "currency")
    val currency: String,

    @Json(name = "usd_amount")
    val usdAmount: BigDecimal?,

    @Json(name = "currency_amount")
    val currencyAmount: BigDecimal,

    @Json(name = "transaction_id")
    val transactionId: String?
)

enum class ExchangeOrderStatus {
    @Json(name = "PENDING")
    PENDING,

    @Json(name = "FAILED")
    FAILED,

    @Json(name = "COMPLETE")
    COMPLETE,

    @Json(name = "REFUNDED")
    REFUNDED,
}